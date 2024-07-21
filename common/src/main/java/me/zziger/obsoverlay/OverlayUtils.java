package me.zziger.obsoverlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class OverlayUtils {
    public static boolean isScreenOverlayed(Screen screen) {
        OBSOverlayConfig config = OBSOverlayConfig.get();

        if (config.hideAllScreens && MinecraftClient.getInstance().world != null) return true;
        if (config.overlayScreensClasses.contains(screen.getClass())) return true;
        if (config.overlayHandledScreensEnabled) {
            if (screen instanceof HandledScreen<?> handledScreen) {
                try {
                    Identifier id = Registries.SCREEN_HANDLER.getId(handledScreen.getScreenHandler().getType());
                    if (config.overlayHandledScreensList.contains(id.toString()))
                        return true;
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }
}
