package me.zziger.obsoverlay.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Inject(method = "Lcom/mojang/blaze3d/systems/RenderSystem;flipFrame(JLnet/minecraft/client/util/tracy/TracyFrameCapturer;)V", at = @At("HEAD"))
    private static void onSwap(long window, TracyFrameCapturer capturer, CallbackInfo ci) {
//        OverlayRenderer renderer = OBSOverlay.getRenderer();
//        if (renderer != null)
//            renderer.renderFrame();
    }
}
