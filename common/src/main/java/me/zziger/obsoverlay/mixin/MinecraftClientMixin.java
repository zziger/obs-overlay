package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(RunArgs args, CallbackInfo ci) {
        OverlayRenderer.init((MinecraftClient)(Object)this);
    }

    @Inject(method = "onResolutionChanged()V", at = @At("RETURN"))
    private void onResolutionChanged(CallbackInfo ci) {
        OverlayRenderer.onResolutionChanged((MinecraftClient)(Object)this);
    }

    @Inject(method = "render(Z)V", at = @At("HEAD"))
    private void onRender(boolean tick, CallbackInfo ci) {
        OverlayRenderer.beginFrame();
    }
}
