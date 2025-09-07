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
    @Inject(method = "renderHotbar", at = @At(value = "HEAD"))
    private void drawStartHotbar(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"))
    private void drawEndHotbar(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"))
    private void drawStartScoreboard(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.scoreboards);
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("RETURN"))
    private void drawEndScoreboard(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.scoreboards);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 2))
    private void drawStartActionbar(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.actionbar);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 2))
    private void drawEndActionbar(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.actionbar);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 3))
    private void drawStartTitleSubtitle(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.titleSubtitle);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 3))
    private void drawEndTitleSubtitle(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.titleSubtitle);
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"))
    private void drawStartExperienceLevel(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderExperienceBar", at = @At("RETURN"))
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getCurrentGameMode()Lnet/minecraft/world/GameMode;"))
    private void drawStartMainHud(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getSleepTimer()I", ordinal = 0))
    private void drawEndMainHud(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHeldItemTooltip", at = @At(value = "HEAD"))
    private void drawStartSelectedItemName(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "renderHeldItemTooltip", at = @At(value = "RETURN"))
    private void drawEndSelectedItemName(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }
}
