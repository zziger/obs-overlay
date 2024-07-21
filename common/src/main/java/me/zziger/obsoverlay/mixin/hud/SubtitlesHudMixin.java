package me.zziger.obsoverlay.mixin.hud;

import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import net.minecraft.client.gui.hud.SubtitlesHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubtitlesHud.class)
public class SubtitlesHudMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void drawStart(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.subtitles);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.subtitles);
    }
}
