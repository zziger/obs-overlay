package me.zziger.obsoverlay;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import dev.architectury.platform.Platform;
import me.zziger.obsoverlay.error.OverlayHookException;
import me.zziger.obsoverlay.modules.Kernel32;
import me.zziger.obsoverlay.modules.MinHook;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class OverlayHook {
    private static boolean libraryInitialized = false;
    private static PointerByReference reference;
    private static final ArrayList<Handler> handlerList = new ArrayList<>();
    private static MinHook minHook;

    public interface Handler {
        void run();
    }

    public static void subscribe(Handler handler) {
        handlerList.add(handler);
    }

    public static void unsubscribe(Handler handler) {
        handlerList.remove(handler);
    }

    public static MinHook getMinHook() {
        if (minHook == null) return minHook = Native.load("MinHook", MinHook.class);
        return minHook;
    }

    public static void init() {
        if (libraryInitialized) return;
        initLibrary();
        initHook();
        libraryInitialized = true;
    }

    private static void initLibrary() {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            throw new OverlayHookException("OBS Overlay is only supported on Windows");
        }

        String arch = System.getProperty("os.arch").toLowerCase();

        if (arch.contains("aarch")) {
            throw new OverlayHookException("OBS Overlay is only supported on x64 and x86 systems");
        }

        boolean is64 = arch.equals("x86_64") || arch.equals("amd64") || arch.equals("x64") || arch.equals("ia64");
        InputStream libFile = OBSOverlay.class.getResourceAsStream(is64 ? "/lib/MinHook.x64.dll" : "/lib/MinHook.x86.dll");
        if (libFile == null) {
            throw new OverlayHookException("Failed to get MinHook dll");
        }

        File nativeDir = new File(Platform.getGameFolder().toAbsolutePath().toString().concat("/native"));
        File copyLibFile = new File(Platform.getGameFolder().toAbsolutePath().toString().concat("/native/MinHook.dll"));
        nativeDir.mkdir();

        try {
            FileOutputStream fos = new FileOutputStream(copyLibFile);
            copyLibFile.createNewFile();
            IOUtils.copy(libFile, fos);
            fos.close();
        } catch (IOException e) {
            throw new OverlayHookException("Failed to copy dependency dll");
        }

        System.setProperty("jna.library.path", nativeDir.getAbsolutePath());
        OBSOverlay.LOGGER.info("Copied dependency DLL successfully");
    }

    private static void initHook() {
        Pointer module = Kernel32.INSTANCE.GetModuleHandleA("opengl32.dll");
        Pointer proc = Kernel32.INSTANCE.GetProcAddress(module, "wglSwapBuffers");

        try {
            MinHook minhook = getMinHook();
            minhook.MH_Initialize();
            reference = new PointerByReference();

            minhook.MH_CreateHook(proc, (hDc) -> {
                handlerList.forEach(Handler::run);
                Function origFunction = Function.getFunction(reference.getValue(), Function.ALT_CONVENTION);
                return (boolean) origFunction.invoke(Boolean.class, new Object[]{hDc});
            }, reference);
            minhook.MH_EnableHook(proc);
        } catch (Exception e) {
            OBSOverlay.LOGGER.error("Failed to initialize MinHook");
            throw e;
        }
    }

}
