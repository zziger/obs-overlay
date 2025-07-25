package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void drawStart(DrawContext context, CallbackInfo ci) {
        context.draw();
        OBSOverlay.getAPI().beginDraw(AllDefaultOverlayComponents.debugMenu);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(DrawContext context, CallbackInfo ci) {
        context.draw();
        OBSOverlay.getAPI().endDraw(AllDefaultOverlayComponents.debugMenu);
    }
}
