package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OverlayUtils;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "applyBlur", at = @At("HEAD"), cancellable = true)
    private void renderBlur(float delta, CallbackInfo ci) {
        if (OverlayUtils.isScreenOverlayed((Screen) (Object) this))
            ci.cancel();
    }
}
