package me.zziger.obsoverlay.component.type;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;

public class HUDOverlayComponent extends DefaultOverlayComponent {
    public HUDOverlayComponent(Identifier id, boolean defaultOverlay, boolean canAutoHide) {
        super(id, defaultOverlay, canAutoHide);
    }

    @Override
    public void beforeBeginDraw() {
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
    }

    @Override
    public void beforeEndDraw() {
        RenderSystem.defaultBlendFunc();
    }
}
