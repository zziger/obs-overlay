package me.zziger.obsoverlay.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.zziger.obsoverlay.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderHand(Lnet/minecraft/client/render/Camera;FLorg/joml/Matrix4f;)V", at = @At(value = "HEAD"))
    private void renderHand(Camera camera, float tickDelta, Matrix4f matrix4f, CallbackInfo ci) {
        OverlayRenderer renderer = OBSOverlay.getRenderer();
        if (renderer != null) renderer.renderingHands = true;
        OBSOverlay.getAPI().backupDepth(true);
    }

    @Inject(method = "renderHand(Lnet/minecraft/client/render/Camera;FLorg/joml/Matrix4f;)V", at = @At(value = "RETURN"))
    private void renderHandEnd(Camera camera, float tickDelta, Matrix4f matrix4f, CallbackInfo ci) {
        OverlayRenderer renderer = OBSOverlay.getRenderer();
        if (renderer != null) renderer.renderingHands = false;
        OBSOverlay.getAPI().backupDepth(true);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw()V", shift = At.Shift.AFTER))
    private void renderTestIcon(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, @Local() DrawContext instance) {
        instance.draw();
        if (OBSOverlayConfig.get().showTestIcon && OBSOverlay.getIsInitialized()) {
            OBSOverlay.getAPI().beginDraw(OverlayFramebufferType.NORMAL);
            try {
                instance.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("icon/checkmark"), 0, 0, 16, 16);
                instance.draw();
            } catch (Exception ignored) {
            }
            OBSOverlay.getAPI().endDraw(OverlayFramebufferType.NORMAL);
        }
    }
}
