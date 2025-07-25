package me.zziger.obsoverlay.component.type;

import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.component.IOverlayComponent;
import me.zziger.obsoverlay.component.OverlayComponentRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class DefaultOverlayComponent implements IOverlayComponent {
    private final Identifier id;
    private final boolean canAutoHide;
    private final boolean defaultOverlay;

    private boolean overlay;
    private boolean autoHide;

    public DefaultOverlayComponent(Identifier id, boolean defaultOverlay, boolean canAutoHide) {
        this.id = id;
        this.canAutoHide = canAutoHide;
        this.defaultOverlay = defaultOverlay;

        OBSOverlayConfig config = OBSOverlayConfig.get();
        overlay = config.overlayComponents.getOrDefault(getId(), defaultOverlay);
        autoHide = canAutoHide ? config.autoHideComponents.getOrDefault(getId(), true) : false;
    }

    @Override
    public boolean canAutoHide() {
        return canAutoHide;
    }

    @Override
    public String getId() {
        return id.toTranslationKey();
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
        OBSOverlayConfig.get().overlayComponents.put(getId(), value);
    }

    @Override
    public boolean isAutoHideEnabled() {
        return canAutoHide && autoHide;
    }

    @Override
    public void setAutoHideEnabled(boolean value) {
        if (!canAutoHide) return;
        this.autoHide = value;
        OBSOverlayConfig.get().autoHideComponents.put(getId(), value);
    }

    @Override
    public boolean isHidden() {
        if (!this.isAutoHideEnabled()) return false;

        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        return currentScreen != null && !OverlayComponentRegistry.ignoredScreens.contains(currentScreen.getClass());
    }
}
