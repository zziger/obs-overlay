package me.zziger.obsoverlay.neoforge.mixin;

import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SpectatorHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpectatorHud.class)
public class SpectatorHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void drawStart(DrawContext context, CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.mainHud);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(DrawContext context, CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.mainHud);
    }
}
