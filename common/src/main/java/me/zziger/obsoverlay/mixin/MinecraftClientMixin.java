package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.profiler.ProfileResult;
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

    @Inject(method = "drawProfilerResults", at = @At("HEAD"))
    private void drawStartDebugPie(DrawContext context, ProfileResult profileResult, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.debugMenu);
    }

    @Inject(method = "drawProfilerResults", at = @At("RETURN"))
    private void drawEndDebugPie(DrawContext context, ProfileResult profileResult, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.debugMenu);
    }
}
