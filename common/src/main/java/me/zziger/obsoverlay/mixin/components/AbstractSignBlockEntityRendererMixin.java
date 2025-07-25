package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OverlayUtils;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignBlockEntityRenderer.class)
public class AbstractSignBlockEntityRendererMixin {
    @Inject(method = "renderText(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SignText;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IIIZ)V", at = @At("HEAD"))
    void renderText(BlockPos pos, SignText text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int textLineHeight, int maxTextWidth, boolean front, CallbackInfo ci) {
        OverlayUtils.forceDraw(vertexConsumers);
        OBSOverlay.getAPI().beginDraw(AllDefaultOverlayComponents.signText);
    }

    @Inject(method = "renderText(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SignText;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IIIZ)V", at = @At("RETURN"))
    void renderTextEnd(BlockPos pos, SignText text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int textLineHeight, int maxTextWidth, boolean front, CallbackInfo ci) {
        OverlayUtils.forceDraw(vertexConsumers);
        OBSOverlay.getAPI().endDraw(AllDefaultOverlayComponents.signText);
    }
}
