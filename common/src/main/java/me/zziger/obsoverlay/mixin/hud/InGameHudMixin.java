package me.zziger.obsoverlay.mixin.hud;

import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"))
    private void drawStartScoreboard(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.scoreboards);
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("RETURN"))
    private void drawEndScoreboard(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.scoreboards);
    }

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"))
    private void drawStartActionbar(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.actionbar);
    }

    @Inject(method = "renderOverlayMessage", at = @At("RETURN"))
    private void drawEndActionbar(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.actionbar);
    }

    @Inject(method = "renderTitleAndSubtitle", at = @At("HEAD"))
    private void drawStartTitleSubtitle(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.titleSubtitle);
    }

    @Inject(method = "renderTitleAndSubtitle", at = @At("RETURN"))
    private void drawEndTitleSubtitle(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.titleSubtitle);
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"))
    private void drawStartExperienceLevel(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderExperienceLevel", at = @At("RETURN"))
    private void drawEndExperienceLevel(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"))
    private void drawStartEffects(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.effects);
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("RETURN"))
    private void drawEndEffects(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.effects);
    }

    @Inject(method = "renderMainHud", at = @At("HEAD"))
    private void drawStartMainHud(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderMainHud", at = @At("RETURN"))
    private void drawEndMainHud(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }
}
