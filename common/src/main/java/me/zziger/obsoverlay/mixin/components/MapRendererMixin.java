package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.OverlayUtils;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public class MapRendererMixin {
    @Inject(method = "draw(Lnet/minecraft/client/render/MapRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ZI)V", at = @At("HEAD"))
    private static void draw(MapRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean bl, int light, CallbackInfo ci) {
        OverlayRenderer renderer = OBSOverlay.getRenderer();

        if (renderer != null && !renderer.renderingHands) {
            OverlayUtils.forceDraw(vertexConsumers);
            OBSOverlay.getAPI().beginDraw(AllDefaultOverlayComponents.itemFrameMap);
        }
    }

    @Inject(method = "draw(Lnet/minecraft/client/render/MapRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ZI)V", at = @At("RETURN"))
    private static void drawEnd(MapRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean bl, int light, CallbackInfo ci) {
        OverlayRenderer renderer = OBSOverlay.getRenderer();

        if (renderer != null && !renderer.renderingHands) {
            OverlayUtils.forceDraw(vertexConsumers);
            OBSOverlay.getAPI().endDraw(AllDefaultOverlayComponents.itemFrameMap);
        }
    }
}
