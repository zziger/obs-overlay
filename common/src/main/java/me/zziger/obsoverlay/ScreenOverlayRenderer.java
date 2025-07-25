package me.zziger.obsoverlay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class ScreenOverlayRenderer {

    public static void beforeScreenRender(Screen instance) {
        boolean overlay = OBSOverlayConfig.isScreenOverlayed(instance);
        if (overlay) {
            OBSOverlay.getAPI().beginDraw(OverlayFramebufferType.NORMAL);
        }
    }

    public static void afterScreenRender(Screen instance, DrawContext context) {
        context.draw();
        boolean overlay = OBSOverlayConfig.isScreenOverlayed(instance);
        if (overlay) {
            OBSOverlay.getAPI().endDraw(OverlayFramebufferType.NORMAL);
        }
    }
}
