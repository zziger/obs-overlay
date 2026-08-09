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
 * {@code libMinHook.so} (a funchook-based MinHook-compatible shim). These are the
 * same symbols obs-vkcapture's LD_PRELOAD library eventually calls into after it
 * copies the back buffer, so the overlay lands after the copy — identical
 * ordering semantics to Windows. See {@code src/native/linux/README.md}.
 */
public class OverlayHook {
    private static boolean libraryInitialized = false;
    private static final ArrayList<Handler> handlerList = new ArrayList<>();
    private static MinHook minHook;
    private static MinHookLinux minHookLinux;
    /** Strong references so JNA never collects the detour trampolines. */
    private static final List<Callback> retainedCallbacks = new ArrayList<>();
    /** DEBUG: how many times the Linux swap detour actually fired. */
    private static final java.util.concurrent.atomic.AtomicInteger swapDetourCount = new java.util.concurrent.atomic.AtomicInteger();

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

        logLinuxSymbolProbe();

        for (SwapTarget target : targets) {
            PointerByReference reference = new PointerByReference();
            boolean[] firstCall = {true};
            MinHookLinux.SwapBuffers callback = (first, second) -> {
                int n = swapDetourCount.incrementAndGet();
                if (firstCall[0]) {
                    firstCall[0] = false;
                    OBSOverlay.LOGGER.info("[DEBUG] first swap detour fire on {} thread={} swapCount={}",
                            target.name, Thread.currentThread().getName(), n);
                } else if (n % 200 == 0) {
                    OBSOverlay.LOGGER.info("[DEBUG] swap detour fired {} times on {}", n, target.name);
                }
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

        // GLVND: the pointer GLFW actually calls can come from either libGL.so.1
        // (a forwarding stub) or libGLX.so.0 (the dispatch entry). Hook all of
        // them; identical addresses are de-duplicated.
        for (String lib : new String[]{"libGL.so.1", "libGLX.so.0"}) {
            addTarget(targets, seen, resolveSymbol(lib, "glXSwapBuffers"), true, "glXSwapBuffers");
            addTarget(targets, seen, resolveSymbol(lib, "glXSwapBuffersMscOML"), true, "glXSwapBuffersMscOML");
        }

        addTarget(targets, seen, resolveSymbol("libEGL.so.1", "eglSwapBuffers"), false, "eglSwapBuffers");

        return targets;
    }

    private static void addTarget(List<SwapTarget> targets, Set<Long> seen, Pointer address, boolean glx, String name) {
        if (address == null) return;
        long value = Pointer.nativeValue(address);
        if (!seen.add(value)) return;
        targets.add(new SwapTarget(address, glx, name));
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
     * DEBUG: log every swap symbol we can see, so we can tell which present
     * path the game actually uses (GLX vs EGL, damage variants, GLFW).
     */
    private static void logLinuxSymbolProbe() {
        String[][] probes = {
                {"libGL.so.1", "glXSwapBuffers"}, {"libGLX.so.0", "glXSwapBuffers"},
                {"libGL.so.1", "glXSwapBuffersMscOML"}, {"libGLX.so.0", "glXSwapBuffersMscOML"},
                {"libEGL.so.1", "eglSwapBuffers"},
                {"libEGL.so.1", "eglSwapBuffersWithDamageKHR"},
                {"libEGL.so.1", "eglSwapBuffersWithDamageEXT"},
                {"libglfw.so.3", "glfwSwapBuffers"}, {"libglfw.so", "glfwSwapBuffers"},
                {"libGLESv2.so.2", "glClear"},
        };
        StringBuilder sb = new StringBuilder("[DEBUG] resolvable swap symbols:");
        for (String[] probe : probes) {
            Pointer p = resolveSymbol(probe[0], probe[1]);
            sb.append("\n  ")
                    .append(String.format(Locale.ROOT, "%-32s = 0x%016x (%s)",
                            probe[1], p == null ? 0 : Pointer.nativeValue(p), probe[0]));
        }
        OBSOverlay.LOGGER.info("{}", sb);
    }
}
