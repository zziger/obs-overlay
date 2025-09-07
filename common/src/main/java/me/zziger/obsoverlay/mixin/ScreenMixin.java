package me.zziger.obsoverlay.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.zziger.obsoverlay.OverlayUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @WrapOperation(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fillGradient(IIIIII)V"))
    private void renderBlur(DrawContext instance, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, Operation<Void> original) {
        if (OverlayUtils.isScreenOverlayed((Screen) (Object) this))
            return;
        original.call(instance, startX, startY, endX, endY, colorStart, colorEnd);
    }
}
