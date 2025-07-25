package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OverlayUtils;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerBlockEntityRenderer.class)
public class BannerBlockEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/block/entity/BannerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"))
    private static void renderPattern(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        OverlayUtils.forceDraw(vertexConsumerProvider);
        OBSOverlay.getAPI().beginDraw(AllDefaultOverlayComponents.bannerCanvas);
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/BannerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("RETURN"))
    private static void renderPatternEnd(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        OverlayUtils.forceDraw(vertexConsumerProvider);
        OBSOverlay.getAPI().endDraw(AllDefaultOverlayComponents.bannerCanvas);
    }
}
