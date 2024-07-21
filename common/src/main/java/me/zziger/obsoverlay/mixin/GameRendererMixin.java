package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.OverlayUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw()V"))
    private void renderTestIcon(DrawContext instance) {
        if (OBSOverlayConfig.get().showTestIcon && OBSOverlay.libraryInitialized) {
            OverlayRenderer.beginDraw();
            try {
                instance.drawGuiTexture(Identifier.ofVanilla("icon/checkmark"), 0, 0, 16, 16);
            } catch (Exception ignored) {
            }
            OverlayRenderer.endDraw();
        }
        instance.draw();
    }
}
