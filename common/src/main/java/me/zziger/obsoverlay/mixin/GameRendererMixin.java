package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
