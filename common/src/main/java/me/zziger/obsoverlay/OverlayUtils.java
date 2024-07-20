package me.zziger.obsoverlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

public class OverlayUtils {
    public static boolean shouldRenderOverlay() {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        return currentScreen == null || currentScreen.getClass() == ChatScreen.class;
    }
}
