package me.zziger.obsoverlay.mixin.client;

import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.OverlayUtils;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"))
    private void drawStartScoreboard(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayScoreboards) {
            OverlayRenderer.beginDrawOrEmpty(!OBSOverlayConfig.autoHideScoreboards || OverlayUtils.shouldRenderOverlay());
        }
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("RETURN"))
    private void drawEndScoreboard(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }

    @Inject(method = "renderOverlayMessage(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"))
    private void drawStartActionbar(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayActionbar) {
            OverlayRenderer.beginDrawOrEmpty(!OBSOverlayConfig.autoHideActionbar || OverlayUtils.shouldRenderOverlay());
        }
    }

    @Inject(method = "renderOverlayMessage(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("RETURN"))
    private void drawEndActionbar(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }

    @Inject(method = "renderTitleAndSubtitle(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"))
    private void drawStartTitleSubtitle(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayTitleSubtitle) {
            OverlayRenderer.beginDrawOrEmpty(!OBSOverlayConfig.autoHideTitleSubtitle || OverlayUtils.shouldRenderOverlay());
        }
    }

    @Inject(method = "renderTitleAndSubtitle(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("RETURN"))
    private void drawEndTitleSubtitle(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }

    @Inject(method = "renderExperienceLevel(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"))
    private void drawStartExperienceLevel(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayMainHud) {
            OverlayRenderer.beginDrawOrEmpty(!OBSOverlayConfig.autoHideMainHud || OverlayUtils.shouldRenderOverlay());
        }
    }

    @Inject(method = "renderExperienceLevel(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("RETURN"))
    private void drawEndExperienceLevel(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }

    @Inject(method = "renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"))
    private void drawStartEffects(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayEffects) {
            OverlayRenderer.beginDrawOrEmpty(!OBSOverlayConfig.autoHideEffects || OverlayUtils.shouldRenderOverlay());
        }
    }

    @Inject(method = "renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("RETURN"))
    private void drawEndEffects(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }

    @Inject(method = "renderMainHud(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"))
    private void drawStartMainHud(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayMainHud) {
            OverlayRenderer.beginDrawOrEmpty(!OBSOverlayConfig.autoHideMainHud || OverlayUtils.shouldRenderOverlay());
        }
    }

    @Inject(method = "renderMainHud(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("RETURN"))
    private void drawEndMainHud(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }
}
