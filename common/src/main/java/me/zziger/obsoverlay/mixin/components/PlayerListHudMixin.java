package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void drawStart(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        context.draw();
        OBSOverlay.getAPI().beginDraw(AllDefaultOverlayComponents.playerList);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        context.draw();
        OBSOverlay.getAPI().endDraw(AllDefaultOverlayComponents.playerList);
    }
}
