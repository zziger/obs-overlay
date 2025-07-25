package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OverlayUtils;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    protected void draw(EntityRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        OverlayUtils.forceDraw(vertexConsumers);
        OBSOverlay.getAPI().beginDraw(state.sneaking ? AllDefaultOverlayComponents.nameTagSneaking : AllDefaultOverlayComponents.nameTag);
    }

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("RETURN"))
    protected void drawEnd(EntityRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        OverlayUtils.forceDraw(vertexConsumers);
        OBSOverlay.getAPI().endDraw(state.sneaking ? AllDefaultOverlayComponents.nameTagSneaking : AllDefaultOverlayComponents.nameTag);
    }
}
