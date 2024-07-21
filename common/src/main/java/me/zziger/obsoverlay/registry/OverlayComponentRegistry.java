package me.zziger.obsoverlay.registry;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class OverlayComponentRegistry {

    public static ArrayList<OverlayComponent> components = new ArrayList<>();
    public static ArrayList<Class<?>> ignoredScreens = new ArrayList<>(List.of(ChatScreen.class));
    public static HashMap<String, Class<?>> hideableScreens = new HashMap<>() {{
        put("inventory", InventoryScreen.class);
        put("creative_inventory", CreativeInventoryScreen.class);
        put("pause_menu", GameMenuScreen.class);
        put("command_block", CommandBlockScreen.class);
    }};

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

    public static void addHideableScreen(String id, Class<?> screen) {
        hideableScreens.put(id, screen);
    }
}
