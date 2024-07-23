package me.zziger.obsoverlay.neoforge.mixin;

import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderHotbar", at = @At(value = "HEAD"))
    private void drawStartHotbar(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"))
    private void drawEndHotbar(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderJumpMeter", at = @At(value = "HEAD"))
    private void drawStartJumpMeter(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderJumpMeter", at = @At(value = "RETURN"))
    private void drawEndJumpMeter(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderExperienceBar", at = @At(value = "HEAD"))
    private void drawStartExperienceBar(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderExperienceBar", at = @At(value = "RETURN"))
    private void drawEndExperienceBar(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderPlayerHealth", at = @At(value = "HEAD"))
    private void drawStartPlayerHealth(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderPlayerHealth", at = @At(value = "RETURN"))
    private void drawEndPlayerHealth(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderVehicleHealth", at = @At(value = "HEAD"))
    private void drawStartVehicleHealth(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderVehicleHealth", at = @At(value = "RETURN"))
    private void drawEndVehicleHealth(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderSelectedItemName", at = @At(value = "HEAD"))
    private void drawStartSelectedItemName(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderSelectedItemName", at = @At(value = "RETURN"))
    private void drawEndSelectedItemName(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderSpectatorTooltip", at = @At(value = "HEAD"))
    private void drawStartSpectatorTooltip(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderSpectatorTooltip", at = @At(value = "RETURN"))
    private void drawEndSpectatorTooltip(DrawContext arg, RenderTickCounter arg2, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

}
