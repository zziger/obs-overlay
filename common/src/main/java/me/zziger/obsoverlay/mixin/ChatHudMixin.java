package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayUtils;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void drawStart(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayChat) {
            OverlayRenderer.beginDrawOrEmpty(!OBSOverlayConfig.autoHideChat || OverlayUtils.shouldRenderOverlay());
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }
}
