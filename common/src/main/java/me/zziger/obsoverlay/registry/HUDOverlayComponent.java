package me.zziger.obsoverlay.registry;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zziger.obsoverlay.compat.ImmediatelyFastCompat;

public class HUDOverlayComponent extends DefaultOverlayComponent {
    public HUDOverlayComponent(String id, boolean defaultOverlay, boolean canAutoHide) {
        super(id, defaultOverlay, canAutoHide);
    }

    @Override
    public void beforeBeginDraw() {
        ImmediatelyFastCompat.forceDraw();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
    }

    @Override
    public void beforeEndDraw() {
        ImmediatelyFastCompat.forceDraw();
        RenderSystem.defaultBlendFunc();
    }
}
