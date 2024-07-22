package me.zziger.obsoverlay.mixin.hud;

import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
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
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.chat);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.chat);
    }
}
