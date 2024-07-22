package me.zziger.obsoverlay;

import dev.architectury.event.events.common.*;
import dev.architectury.platform.Platform;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class OBSOverlay {
    public static final String MOD_ID = "obs_overlay";
    public static final Logger LOGGER = LoggerFactory.getLogger("obs_overlay");

    public static boolean libraryInitialized = false;

    public static boolean initLibrary() {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            OBSOverlay.LOGGER.error("OBS Overlay is only supported on Windows");
            return false;
        }

        String arch = System.getProperty("os.arch").toLowerCase();

        if (arch.contains("aarch")) {
            OBSOverlay.LOGGER.error("OBS Overlay is only supported on x64 and x86 systems");
            return false;
        }

        boolean is64 = arch.equals("x86_64") || arch.equals("amd64") || arch.equals("x64") || arch.equals("ia64");
        InputStream libFile = OBSOverlay.class.getResourceAsStream(is64 ? "/lib/MinHook.x64.dll" : "/lib/MinHook.x86.dll");
        if (libFile == null) {
            OBSOverlay.LOGGER.error("Failed to get MinHook.x64.dll dependency");
            return false;
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
            OBSOverlay.LOGGER.error("Failed to copy dependency DLL");
            return false;
        }

        System.setProperty("jna.library.path", nativeDir.getAbsolutePath());
        OBSOverlay.LOGGER.info("Copied dependency DLL successfully");
        return true;
    }

    public static void beforeScreenRender(Screen instance) {
        boolean overlay = OverlayUtils.isScreenOverlayed(instance);
        if (overlay) {
            OverlayRenderer.beginDraw();
        }
    }

    public static void afterScreenRender(Screen instance, DrawContext context) {
        context.draw();
        boolean overlay = OverlayUtils.isScreenOverlayed(instance);
        if (overlay) {
            OverlayRenderer.endDraw();
        }
    }


    public static void init() {
        if (initLibrary()) libraryInitialized = true;
        else OBSOverlay.LOGGER.error("Failed to initialize OBS Overlay library");

        OBSOverlayConfig.init();
        AllDefaultOverlayComponents.init();
    }
}
