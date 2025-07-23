package me.zziger.obsoverlay.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public class FramebufferMixin {
    @Inject(method = "_glBindFramebuffer(II)V", at=@At(value = "HEAD"), cancellable = true)
    private static void bindFramebuffer(int target, int framebuffer, CallbackInfo ci) {
        if (OverlayRenderer.isFramebufferOverridden()) ci.cancel();
    }
}
