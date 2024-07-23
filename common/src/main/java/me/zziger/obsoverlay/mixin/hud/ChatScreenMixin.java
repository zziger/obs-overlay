package me.zziger.obsoverlay.mixin.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import me.zziger.obsoverlay.registry.AllDefaultOverlayComponents;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V", shift = At.Shift.AFTER))
    private void drawStart(CallbackInfo ci) {
        OverlayRenderer.beginDraw(AllDefaultOverlayComponents.chatBar);
        RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void drawEnd(CallbackInfo ci) {
        OverlayRenderer.endDraw(AllDefaultOverlayComponents.chatBar);
    }
}
