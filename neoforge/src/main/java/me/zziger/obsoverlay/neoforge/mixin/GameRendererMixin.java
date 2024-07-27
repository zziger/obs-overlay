package me.zziger.obsoverlay.neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.zziger.obsoverlay.OBSOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.BEFORE))
    private void beforeScreenRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Screen screen = ((GameRenderer) (Object) this).getClient().currentScreen;
        if (screen != null) OBSOverlay.beforeScreenRender(screen);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER))
    private void afterScreenRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local(index = 12) DrawContext context) {
        Screen screen = ((GameRenderer) (Object) this).getClient().currentScreen;
        if (screen != null) OBSOverlay.afterScreenRender(screen, context);
    }
}
