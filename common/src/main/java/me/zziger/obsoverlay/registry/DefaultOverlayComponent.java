package me.zziger.obsoverlay.registry;

import me.zziger.obsoverlay.OBSOverlayConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class DefaultOverlayComponent implements OverlayComponent {
    private final String id;
    private final boolean canAutoHide;
    private final boolean defaultOverlay;

    private boolean overlay;
    private boolean autoHide;

    public DefaultOverlayComponent(String id, boolean defaultOverlay, boolean canAutoHide) {
        this.id = id;
        this.canAutoHide = canAutoHide;
        this.defaultOverlay = defaultOverlay;

        OBSOverlayConfig config = OBSOverlayConfig.get();
        overlay = config.overlayComponents.getOrDefault(id, defaultOverlay);
        autoHide = canAutoHide ? config.autoHideComponents.getOrDefault(id, true) : false;
    }

    @Override
    public boolean canAutoHide() {
        return canAutoHide;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isOverlayEnabledDefault() {
        return defaultOverlay;
    }

    @Override
    public boolean isOverlayEnabled() {
        return this.overlay;
    }

    @Override
    public void setOverlayEnabled(boolean value) {
        this.overlay = value;
        OBSOverlayConfig.get().overlayComponents.put(id, value);
    }

    @Override
    public boolean isAutoHideEnabled() {
        return canAutoHide && autoHide;
    }

    @Override
    public void setAutoHideEnabled(boolean value) {
        if (!canAutoHide) return;
        this.autoHide = value;
        OBSOverlayConfig.get().autoHideComponents.put(id, value);
    }

    @Override
    public boolean isHidden() {
        if (!this.isAutoHideEnabled()) return false;

        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        return currentScreen != null && !OverlayComponentRegistry.ignoredScreens.contains(currentScreen.getClass());
    }
}
