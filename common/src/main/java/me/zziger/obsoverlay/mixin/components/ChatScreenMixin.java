package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V", shift = At.Shift.AFTER))
    private void drawStart(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.draw();
        OBSOverlay.getAPI().beginDraw(AllDefaultOverlayComponents.chatBar);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.draw();
        OBSOverlay.getAPI().endDraw(AllDefaultOverlayComponents.chatBar);
    }
}
