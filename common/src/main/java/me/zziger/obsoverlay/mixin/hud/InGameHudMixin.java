package me.zziger.obsoverlay.mixin.hud;

import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"))
    private void drawStartScoreboard(DrawContext drawContext, ScoreboardObjective objective, CallbackInfo ci) {
        drawContext.draw();
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.scoreboards);
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("RETURN"))
    private void drawEndScoreboard(DrawContext drawContext, ScoreboardObjective objective, CallbackInfo ci) {
        drawContext.draw();
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.scoreboards);
    }

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"))
    private void drawStartActionbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.actionbar);
    }

    @Inject(method = "renderOverlayMessage", at = @At("RETURN"))
    private void drawEndActionbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.actionbar);
    }

    @Inject(method = "renderTitleAndSubtitle", at = @At("HEAD"))
    private void drawStartTitleSubtitle(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.titleSubtitle);
    }

    @Inject(method = "renderTitleAndSubtitle", at = @At("RETURN"))
    private void drawEndTitleSubtitle(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.titleSubtitle);
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"))
    private void drawStartExperienceLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderExperienceLevel", at = @At("RETURN"))
    private void drawEndExperienceLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"))
    private void drawStartEffects(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.effects);
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("RETURN"))
    private void drawEndEffects(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.effects);
    }

    @Inject(method = "renderMainHud", at = @At("HEAD"))
    private void drawStartMainHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderMainHud", at = @At("RETURN"))
    private void drawEndMainHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }
}
