package me.zziger.obsoverlay.neoforge.mixin;

import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import net.minecraft.client.gui.DrawContext;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Most mixins towards InGameHud does not work on MC Forge 1.20.1 because ForgeGui is used instead of vanilla InGameHud
// So we have this Mixin
@Mixin(ForgeGui.class)
public class ForgeGuiMixin {

    @Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
    private void drawStartJumpMeter(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
    private void drawEndJumpMeter(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderTitle", at = @At("HEAD"))
    private void drawStartTitleSubtitle(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.titleSubtitle);
    }

    @Inject(method = "renderTitle", at = @At("RETURN"))
    private void drawEndTitleSubtitle(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.titleSubtitle);
    }

    @Inject(method = "renderChat", at = @At("HEAD"))
    private void drawStartChat(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.chat);
    }

    @Inject(method = "renderChat", at = @At("RETURN"))
    private void drawEndChat(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.chat);
    }

    @Inject(method = "renderExperience", at = @At(value = "HEAD"))
    private void drawStartExperienceBar(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderExperience", at = @At(value = "RETURN"))
    private void drawEndExperienceBar(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderAir", at = @At(value = "HEAD"))
    private void drawStartPlayerAir(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderAir", at = @At(value = "RETURN"))
    private void drawEndPlayerAir(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHealth", at = @At(value = "HEAD"))
    private void drawStartPlayerHealth(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHealth", at = @At(value = "RETURN"))
    private void drawEndPlayerHealth(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderArmor", at = @At(value = "HEAD"))
    private void drawStartPlayerArmor(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderArmor", at = @At(value = "RETURN"))
    private void drawEndPlayerArmor(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderFood", at = @At(value = "HEAD"))
    private void drawStartPlayerFood(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderFood", at = @At(value = "RETURN"))
    private void drawEndPlayerFood(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHealthMount", at = @At(value = "HEAD"))
    private void drawStartVehicleHealth(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHealthMount", at = @At(value = "RETURN"))
    private void drawEndVehicleHealth(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHUDText", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
    private void drawStartDebugText(int width, int height, DrawContext guiGraphics, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.debugMenu);
    }

    @Inject(method = "renderHUDText", at = @At("RETURN"))
    private void drawEndDebugText(int width, int height, DrawContext guiGraphics, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.debugMenu);
    }

    @Inject(method = "renderFPSGraph", at = @At("HEAD"))
    private void drawStartDebugHud(DrawContext guiGraphics, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.debugMenu);
    }

    @Inject(method = "renderFPSGraph", at = @At("RETURN"))
    private void drawEndDebugHud(DrawContext guiGraphics, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.debugMenu);
    }

    @Inject(method = "renderRecordOverlay", at = @At(value = "HEAD"))
    private void drawStartActionbar(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.actionbar);
    }

    @Inject(method = "renderRecordOverlay", at = @At(value = "RETURN"))
    private void drawEndActionbar(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.actionbar);
    }

}
