package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayUtils;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void drawStart(CallbackInfo ci) {
        if (OBSOverlayConfig.overlayDebugMenu) {
            OverlayRenderer.beginDrawOrEmpty(!OBSOverlayConfig.autoHideDebugMenu || OverlayUtils.shouldRenderOverlay());
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(CallbackInfo ci) {
        OverlayRenderer.endDraw();
    }
}
