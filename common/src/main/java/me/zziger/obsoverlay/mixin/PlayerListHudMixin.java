package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.gui.hud.PlayerListHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void drawStart(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayPlayerList) {
            OverlayRenderer.beginDraw();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }
}
