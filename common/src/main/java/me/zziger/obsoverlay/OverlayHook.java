package me.zziger.obsoverlay;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import dev.architectury.platform.Platform;
import me.zziger.obsoverlay.error.OverlayHookException;
import me.zziger.obsoverlay.modules.Kernel32;
import me.zziger.obsoverlay.modules.MinHook;
import me.zziger.obsoverlay.modules.MinHookLinux;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Hooks the swap/present call so the overlay can be composited into the frame
 * that is about to be presented.
 *
 * <p><b>Windows</b> — hooks {@code wglSwapBuffers} with MinHook (the bundled
 * {@code MinHook.dll}). OBS Game Capture copies the back buffer before our hook
 * draws, so the overlay is visible locally but not in the stream.
 *
 * <p><b>Linux</b> — hooks the real {@code glXSwapBuffers} /
 * {@code glXSwapBuffersMscOML} (GLVND, in {@code libGL.so.1}/{@code libGLX.so.0})
 * or {@code eglSwapBuffers} ({@code libEGL.so.1}) with the bundled
 * {@code libMinHook.so} (a funchook-based MinHook-compatible shim).
 *
 * <p>obs-vkcapture's capture shim is {@code LD_PRELOAD}ed and interposes the
 * {@code glX*} / {@code egl*} symbols process-wide, so plain {@code dlsym} /
 * {@code glXGetProcAddress} from Java resolve <em>into the shim</em> rather than
 * the real driver. To land the hook after the shim copies the back buffer, the
 * real mapped GL libraries are located via {@code /proc/self/maps} and their
 * {@code glXSwapBuffers} / {@code glXSwapBuffersMscOML} / {@code eglSwapBuffers}
 * addresses are resolved from the ELF {@code .dynsym} table (runtime address =
 * load base + {@code st_value} − {@code p_vaddr}), which is immune to symbol
 * interposition. Any target that still lands inside the capture shim is filtered
 * out. The resulting hook sits on the same underlying real symbols the shim
 * calls into after its copy — identical ordering semantics to Windows. See
 * {@code src/native/linux/README.md}.
 */
public class OverlayHook {
    private static boolean libraryInitialized = false;
    private static final ArrayList<Handler> handlerList = new ArrayList<>();
    private static MinHook minHook;
    private static MinHookLinux minHookLinux;
    /** Strong references so JNA never collects the detour trampolines. */
    private static final List<Callback> retainedCallbacks = new ArrayList<>();

    public interface Handler {
        void run();
    }

    private enum Support {
        WINDOWS,
        LINUX
    }

    public static void subscribe(Handler handler) {
        handlerList.add(handler);
    }

    public static void unsubscribe(Handler handler) {
        handlerList.remove(handler);
    }

    public static MinHook getMinHook() {
        if (minHook == null) minHook = Native.load("MinHook", MinHook.class);
        return minHook;
    }

    public static MinHookLinux getMinHookLinux() {
        if (minHookLinux == null) minHookLinux = Native.load("MinHook", MinHookLinux.class);
        return minHookLinux;
    }

    public static void init() {
        if (libraryInitialized) return;
        Support support = detectSupport();
        initLibrary(support);
        if (support == Support.WINDOWS) {
            initHookWindows();
        } else {
            initHookLinux();
        }
        libraryInitialized = true;
    }

    private static Support detectSupport() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) return Support.WINDOWS;
        if (os.contains("linux")) return Support.LINUX;
        throw new OverlayHookException("OBS Overlay is not supported on this platform: " + os);
    }

    private static void initLibrary(Support support) {
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        boolean is64 = arch.contains("64") || arch.contains("aarch");

        String resource;
        String copyName;
        if (support == Support.WINDOWS) {
            if (arch.contains("aarch") || arch.contains("arm")) {
                throw new OverlayHookException("OBS Overlay is only supported on x64 and x86 systems");
            }
            resource = is64 ? "/lib/MinHook.x64.dll" : "/lib/MinHook.x86.dll";
            copyName = "MinHook.dll";
        } else {
            if (arch.contains("x86_64") || arch.contains("amd64")) {
                resource = "/lib/libMinHook.linux-x86_64.so";
            } else if (arch.contains("aarch64") || arch.equals("arm64")) {
                resource = "/lib/libMinHook.linux-aarch64.so";
            } else {
                throw new OverlayHookException("OBS Overlay is not supported on this Linux architecture: " + arch);
            }
            copyName = "libMinHook.so";
        }

        InputStream libFile = OBSOverlay.class.getResourceAsStream(resource);
        if (libFile == null) {
            throw new OverlayHookException("Failed to get native library " + resource);
        }

        File nativeDir = new File(Platform.getGameFolder().toAbsolutePath().toString().concat("/native"));
        File copyLibFile = new File(nativeDir, copyName);
        if (!nativeDir.exists()) nativeDir.mkdir();

        try {
            FileOutputStream fos = new FileOutputStream(copyLibFile);
            copyLibFile.createNewFile();
            IOUtils.copy(libFile, fos);
            fos.close();
        } catch (IOException e) {
            throw new OverlayHookException("Failed to copy dependency " + copyName);
        }

        System.setProperty("jna.library.path", nativeDir.getAbsolutePath());
        OBSOverlay.LOGGER.info("Copied dependency native library ({})", copyName);
    }

    private static void initHookWindows() {
        Pointer module = Kernel32.INSTANCE.GetModuleHandleA("opengl32.dll");
        Pointer proc = Kernel32.INSTANCE.GetProcAddress(module, "wglSwapBuffers");

        try {
            MinHook minhook = getMinHook();
            minhook.MH_Initialize();
            PointerByReference reference = new PointerByReference();

            MinHook.wglSwapBuffers callback = (hDc) -> {
                handlerList.forEach(Handler::run);
                Function origFunction = Function.getFunction(reference.getValue(), Function.ALT_CONVENTION);
                return (boolean) origFunction.invoke(Boolean.class, new Object[]{hDc});
            };
            retainedCallbacks.add(callback);

            minhook.MH_CreateHook(proc, callback, reference);
            minhook.MH_EnableHook(proc);
        } catch (Exception e) {
            OBSOverlay.LOGGER.error("Failed to initialize MinHook");
            throw e;
        }
    }

    private static void initHookLinux() {
        List<SwapTarget> targets = findLinuxSwapTargets();
        if (targets.isEmpty()) {
            throw new OverlayHookException(
                    "Failed to locate glXSwapBuffers/eglSwapBuffers (is libGL/libEGL loaded?)");
        }

        MinHookLinux minhook = getMinHookLinux();
        minhook.MH_Initialize();

        for (SwapTarget target : targets) {
            PointerByReference reference = new PointerByReference();
            MinHookLinux.SwapBuffers callback = (first, second) -> {
                handlerList.forEach(Handler::run);
                Function origFunction = Function.getFunction(reference.getValue(), Function.C_CONVENTION);
                if (target.glx) {
                    origFunction.invokeVoid(new Object[]{first, second});
                    return 0;
                }
                Object ret = origFunction.invoke(Integer.class, new Object[]{first, second});
                return ret instanceof Number number ? number.intValue() : 0;
            };
            retainedCallbacks.add(callback);

            int rc = minhook.MH_CreateHook(target.address, callback, reference);
            if (rc != 0) {
                OBSOverlay.LOGGER.warn("Skipping hook on {} (MH_CreateHook returned {})", target.name, rc);
                continue;
            }
            minhook.MH_EnableHook(target.address);
            OBSOverlay.LOGGER.info("Hooked {}", target.name);
        }
    }

    private record SwapTarget(Pointer address, boolean glx, String name) {
    }

    /**
     * Resolves the real GL swap functions in the process. These are the symbols
     * obs-vkcapture's LD_PRELOAD wrapper calls into after copying the back buffer
     * ({@code real_dlsym(RTLD_NEXT, ...)}), which keeps the draw-after-capture
     * ordering identical to Windows.
     */
    private static List<SwapTarget> findLinuxSwapTargets() {
        List<SwapTarget> targets = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        // Make sure the real GL libraries are resident (and thus visible in
        // /proc/self/maps) before we parse their ELF symbols from disk. JNA's
        // dlopen by SONAME returns the real library — the capture shim has a
        // different SONAME (e.g. libobs_glcapture.so), so it does not shadow
        // the real one here.
        for (String lib : new String[]{"libGL.so.1", "libGLX.so.0", "libEGL.so.1"}) {
            try {
                NativeLibrary.getInstance(lib);
            } catch (Throwable ignored) {
            }
        }

        // 1) dlsym-based resolution (libGL.so.1 / libGLX.so.0). When a capture
        //    shim (obs-vkcapture's libobs_glcapture.so, ...) is LD_PRELOAD'd it
        //    GLOBALLY interposes the glX* symbols, so these addresses land
        //    inside the shim itself — the wrong place (our detour would run
        //    before the shim copies the back buffer). We collect them anyway;
        //    step 4 drops exactly that case.
        for (String lib : new String[]{"libGL.so.1", "libGLX.so.0"}) {
            addTarget(targets, seen, resolveSymbol(lib, "glXSwapBuffers"), true, "glXSwapBuffers");
            addTarget(targets, seen, resolveSymbol(lib, "glXSwapBuffersMscOML"), true, "glXSwapBuffersMscOML");
        }

        // 2) The function pointer obs-vkcapture's shim actually final-calls: it
        //    resolves the NEXT glXGetProcAddress and calls it with the name.
        //    Only this pointer guarantees "copy happened already" when our
        //    detour runs. Without a shim it resolves to the real driver.
        addTarget(targets, seen, resolveGlxReal("libGLX.so.0", "glXSwapBuffers"),
                true, "glXSwapBuffers(real)");
        addTarget(targets, seen, resolveGlxReal("libGLX.so.0", "glXSwapBuffersMscOML"),
                true, "glXSwapBuffersMscOML(real)");

        // 3) The deterministic route that CANNOT be seized by the interposer:
        //    parse the .dynsym of the actual libGLX/libGL/libEGL files that are
        //    mapped into the process, so the returned address runs *after* the
        //    shim has copied the back buffer. This is the seam the shim's own
        //    real_dlsym(RTLD_NEXT, glXGetProcAddress) resolves to.
        for (String file : mappedSharedObjects("libGL.so.1", "libGLX.so.0", "libEGL.so.1")) {
            addTarget(targets, seen, resolveFileSymbol(file, "glXSwapBuffers"),
                    true, "glXSwapBuffers@file");
            addTarget(targets, seen, resolveFileSymbol(file, "glXSwapBuffersMscOML"),
                    true, "glXSwapBuffersMscOML@file");
            addTarget(targets, seen, resolveFileSymbol(file, "eglSwapBuffers"),
                    false, "eglSwapBuffers@file");
        }

        // 4) Drop anything that landed inside the capture shim itself (its
        //    exported glXSwapBuffers entry) — hooking that is exactly the
        //    pre-copy position that makes the overlay leak into OBS.
        List<String> shimNames = preloadShimBasenames();
        if (!shimNames.isEmpty()) {
            targets.removeIf(t -> {
                String owner = describeMapping(Pointer.nativeValue(t.address));
                for (String s : shimNames) {
                    if (owner.contains(s)) return true;
                }
                return false;
            });
            OBSOverlay.LOGGER.info("Skipped swap symbols exported by LD_PRELOAD capture shim(s) {} — hooking the real GL libs instead", shimNames);
        }

        return targets;
    }

    /** Adds a target, ignoring null/duplicate. */
    private static void addTarget(List<SwapTarget> targets, Set<Long> seen, Pointer address, boolean glx, String name) {
        if (address == null) return;
        long value = Pointer.nativeValue(address);
        if (!seen.add(value)) return;
        targets.add(new SwapTarget(address, glx, name));
    }

    /** Basenames of the LD_PRELOAD'd libraries ($LIB expanded), for shim filtering. */
    private static List<String> preloadShimBasenames() {
        List<String> names = new ArrayList<>();
        String preload = System.getenv("LD_PRELOAD");
        if (preload == null || preload.isEmpty()) return names;
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String libDir = (arch.contains("64") || arch.contains("aarch")) ? "lib64" : "lib32";
        for (String entry : preload.split("\\s+")) {
            if (entry.isEmpty()) continue;
            String expanded = entry.replace("$LIB", libDir);
            String base = new File(expanded).getName();
            if (!base.isEmpty()) names.add(base);
        }
        return names;
    }

    /**
     * Resolves the swap function the *real* driver would hand out — the same
     * value obs-vkcapture's shim goes through ({@code real_dlsym(RTLD_NEXT,
     * "glXGetProcAddressARB")(name)}). Loading the function out of the actual
     * libGLX.so.0 handle (rather than the preloaded shim) lands on the real
     * dispatch entry, so the returned pointer is the one that runs *after* the
     * shim has copied the back buffer. Returns null if it can't be resolved.
     */
    private static Pointer resolveGlxReal(String libName, String procName) {
        try {
            NativeLibrary library = NativeLibrary.getInstance(libName);
            Function getProc = getFirstAvailableFunction(library,
                    "glXGetProcAddressARB", "glXGetProcAddress");
            if (getProc == null) return null;
            return (Pointer) getProc.invoke(Pointer.class, new Object[]{procName});
        } catch (Throwable t) {
            return null;
        }
    }

    private static Function getFirstAvailableFunction(NativeLibrary library, String... names) {
        for (String name : names) {
            try {
                return library.getFunction(name);
            } catch (Throwable ignored) {
                // try next name
            }
        }
        return null;
    }

    /**
     * Real shared objects currently mapped into the process whose on-disk name
     * matches one of the given substrings (e.g. "libGL.so.1"). The capture shim
     * is excluded deliberately when it shadows the GL API.
     */
    private static List<String> mappedSharedObjects(String... nameSubstrings) {
        List<String> files = new ArrayList<>();
        Set<String> seenFiles = new HashSet<>();
        List<String> shimNames = preloadShimBasenames();
        for (String[] parts : readMaps()) {
            if (parts.length < 6) continue;
            String path = parts[5];
            if (!path.startsWith("/")) continue;
            String base = new File(path).getName();
            boolean isShim = false;
            for (String s : shimNames) {
                if (path.contains(s)) { isShim = true; break; }
            }
            if (isShim) continue; // never resolve symbols from the capture shim itself
            for (String sub : nameSubstrings) {
                if (base.startsWith(sub)) {
                    if (seenFiles.add(path)) files.add(path);
                    break;
                }
            }
        }
        return files;
    }

    /** Parsed rows of /proc/self/maps. */
    private static List<String[]> readMaps() {
        List<String[]> rows = new ArrayList<>();
        try {
            java.io.BufferedReader r = new java.io.BufferedReader(new java.io.FileReader("/proc/self/maps"));
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 6) rows.add(parts);
            }
            r.close();
        } catch (java.io.IOException ignored) {
        }
        return rows;
    }

    /**
     * Resolves a dynamic symbol from the shared object on disk by parsing its
     * ELF .dynsym, and returns the runtime address in THIS process (load base
     * taken from /proc/self/maps). Immune to LD_PRELOAD symbol interposition —
     * this is the address the interposer itself has to call through.
     */
    private static Pointer resolveFileSymbol(String filePath, String symbol) {
        try {
            long[] symVal = findDynamicSymbol(filePath, symbol);
            if (symVal == null) return null;
            long base = loadBase(filePath);
            if (base == 0) return null;
            return new Pointer(base + symVal[0] - symVal[1]);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Base address of the (offset-0) mapping of {@code filePath}. */
    private static long loadBase(String filePath) {
        String target = realPathOrNull(filePath);
        if (target == null) target = filePath;
        for (String[] parts : readMaps()) {
            if (parts.length < 6) continue;
            if (!parts[5].equals(target)) continue;
            try {
                if (Long.parseLong(parts[2], 16) != 0) continue; // first (offset 0) mapping
            } catch (NumberFormatException e) {
                continue;
            }
            String[] range = parts[0].split("-");
            return Long.parseLong(range[0], 16);
        }
        return 0;
    }

    private static String realPathOrNull(String filePath) {
        try {
            return new File(filePath).getCanonicalPath();
        } catch (java.io.IOException e) {
            return null;
        }
    }

    /** Returns [st_value, p_vaddr-of-first-PT_LOAD] for the named dynsym. */
    private static long[] findDynamicSymbol(String filePath, String symbolName) {
        try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(filePath, "r")) {
            byte[] hdr = new byte[64];
            raf.seek(0);
            raf.readFully(hdr);
            if (hdr[0] != 0x7f || hdr[1] != 'E' || hdr[2] != 'L' || hdr[3] != 'F') return null;
            long ePhoff = u64(hdr, 0x20);
            int ePhentsize = (int) u16(hdr, 0x36);
            int ePhnum = (int) u16(hdr, 0x38);
            long eShoff = u64(hdr, 0x28);
            int eShentsize = (int) u16(hdr, 0x3a);
            int eShnum = (int) u16(hdr, 0x3c);

            // First PT_LOAD's p_vaddr — runtime addr = base + (st_value - p_vaddr).
            long loadVaddr = 0;
            if (ePhnum > 0 && ePhentsize >= 56) {
                byte[] ph = new byte[56];
                for (int i = 0; i < ePhnum; i++) {
                    raf.seek(ePhoff + (long) i * ePhentsize);
                    raf.readFully(ph);
                    if (u32(ph, 0) == 1) { // PT_LOAD
                        loadVaddr = u64(ph, 16);
                        break;
                    }
                }
            }

            // Scan section headers: locate SHT_DYNSYM and the strtab it links to.
            long dynsymOff = 0, dynsymSize = 0;
            int dynsymLink = -1;
            long strOff = 0, strSize = 0;
            if (eShnum > 0 && eShentsize >= 64) {
                int[] shType = new int[eShnum], shLink = new int[eShnum];
                long[] shOff = new long[eShnum], shSize = new long[eShnum];
                byte[] sh = new byte[64];
                for (int i = 0; i < eShnum; i++) {
                    raf.seek(eShoff + (long) i * eShentsize);
                    raf.readFully(sh);
                    shType[i] = (int) u32(sh, 4);
                    shLink[i] = (int) u32(sh, 40);
                    shOff[i] = u64(sh, 24);
                    shSize[i] = u64(sh, 32);
                    if (shType[i] == 11) { // SHT_DYNSYM
                        dynsymOff = shOff[i];
                        dynsymSize = shSize[i];
                        dynsymLink = shLink[i]; // -> .dynstr
                    }
                }
                if (dynsymLink > 0 && dynsymLink < eShnum && shType[dynsymLink] == 3) {
                    strOff = shOff[dynsymLink];
                    strSize = shSize[dynsymLink];
                }
            }
            if (dynsymOff == 0 || strOff == 0) return null;

            byte[] strtab = new byte[(int) strSize];
            raf.seek(strOff);
            raf.readFully(strtab);

            byte[] sym = new byte[24];
            int nsyms = (int) (dynsymSize / 24);
            for (int i = 0; i < nsyms; i++) {
                raf.seek(dynsymOff + i * 24L);
                raf.readFully(sym);
                int stName = (int) u32(sym, 0);
                int stBind = (sym[4] & 0xff) >> 4; // GLOBAL=1, WEAK=2
                if (stBind != 1 && stBind != 2) continue;
                long stValue = u64(sym, 8);
                if (stValue == 0) continue;
                if (cstr(strtab, stName).equals(symbolName)) {
                    return new long[]{stValue, loadVaddr};
                }
            }
        } catch (java.io.IOException ignored) {
        }
        return null;
    }

    private static long u16(byte[] b, int off) {
        return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8);
    }

    private static long u32(byte[] b, int off) {
        return (b[off] & 0xFFL) | ((b[off + 1] & 0xFFL) << 8)
                | ((b[off + 2] & 0xFFL) << 16) | ((b[off + 3] & 0xFFL) << 24);
    }

    private static long u64(byte[] b, int off) {
        return u32(b, off) | (u32(b, off + 4) << 32);
    }

    private static String cstr(byte[] b, int off) {
        if (off < 0 || off >= b.length) return "";
        int end = off;
        while (end < b.length && b[end] != 0) end++;
        return new String(b, off, end - off, java.nio.charset.StandardCharsets.UTF_8);
    }

    private static Pointer resolveSymbol(String libName, String symbol) {
        try {
            NativeLibrary library = NativeLibrary.getInstance(libName);
            return library.getGlobalVariableAddress(symbol);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Returns the file mapped at {@code addr} in this process (from
     * /proc/self/maps), or "(unknown)" if not found.
     */
    private static String describeMapping(long addr) {
        try {
            try (java.io.BufferedReader r =
                         new java.io.BufferedReader(new java.io.FileReader("/proc/self/maps"))) {
                String line;
                while ((line = r.readLine()) != null) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length < 6) continue;
                    String[] range = parts[0].split("-");
                    long start = Long.parseLong(range[0], 16);
                    long end = Long.parseLong(range[1], 16);
                    if (addr >= start && addr < end) {
                        return parts[5];
                    }
                }
            }
        } catch (java.io.IOException ignored) {
        }
        return "(unknown)";
    }
}
