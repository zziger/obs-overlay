package me.zziger.obsoverlay.registry;

import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.compat.ImmediatelyFastCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class HUDOverlayComponent extends DefaultOverlayComponent {
    public HUDOverlayComponent(String id, boolean defaultOverlay, boolean canAutoHide) {
        super(id, defaultOverlay, canAutoHide);
    }

    @Override
    public void beforeBeginDraw() {
        ImmediatelyFastCompat.forceDraw();
    }

    @Override
    public void beforeEndDraw() {
        ImmediatelyFastCompat.forceDraw();
    }
}
