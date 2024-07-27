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
    private void drawStartHotbar(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"))
    private void drawEndHotbar(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderJumpMeter", at = @At(value = "HEAD"))
    private void drawStartJumpMeter(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderJumpMeter", at = @At(value = "RETURN"))
    private void drawEndJumpMeter(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderExperienceBar", at = @At(value = "HEAD"))
    private void drawStartExperienceBar(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderExperienceBar", at = @At(value = "RETURN"))
    private void drawEndExperienceBar(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHealthLevel", at = @At(value = "HEAD"))
    private void drawStartPlayerHealth(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHealthLevel", at = @At(value = "RETURN"))
    private void drawEndPlayerHealth(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderArmorLevel", at = @At(value = "HEAD"))
    private void drawStartPlayerArmor(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderArmorLevel", at = @At(value = "RETURN"))
    private void drawEndPlayerArmor(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderFoodLevel", at = @At(value = "HEAD"))
    private void drawStartPlayerFood(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderFoodLevel", at = @At(value = "RETURN"))
    private void drawEndPlayerFood(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderVehicleHealth", at = @At(value = "HEAD"))
    private void drawStartVehicleHealth(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderVehicleHealth", at = @At(value = "RETURN"))
    private void drawEndVehicleHealth(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderSelectedItemName", at = @At(value = "HEAD"))
    private void drawStartSelectedItemName(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderSelectedItemName", at = @At(value = "RETURN"))
    private void drawEndSelectedItemName(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderSpectatorTooltip", at = @At(value = "HEAD"))
    private void drawStartSpectatorTooltip(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "maybeRenderSpectatorTooltip", at = @At(value = "RETURN"))
    private void drawEndSpectatorTooltip(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

}
