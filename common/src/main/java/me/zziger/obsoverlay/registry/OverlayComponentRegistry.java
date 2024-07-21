package me.zziger.obsoverlay.registry;

import net.minecraft.client.gui.screen.ChatScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class OverlayComponentRegistry {

    public static ArrayList<OverlayComponent> components = new ArrayList<>();
    public static ArrayList<Class<?>> ignoredScreens = new ArrayList<>(List.of(ChatScreen.class));

    public static OverlayComponent registerComponent(OverlayComponent component) {
        components.add(component);
        return component;
    }

    public static void registerComponents(OverlayComponent... components) {
        Stream.of(components).forEach(OverlayComponentRegistry::registerComponent);
    }

    public static void addIgnoredScreen(Class<?> screen) {
        ignoredScreens.add(screen);
    }
}
