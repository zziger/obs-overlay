package me.zziger.obsoverlay.neoforge.mixin;

import me.zziger.obsoverlay.OBSOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void renderScreen(Screen instance, DrawContext context, int mouseX, int mouseY, float delta) {
        OBSOverlay.beforeScreenRender(instance);
        instance.renderWithTooltip(context, mouseX, mouseY, delta);
        OBSOverlay.afterScreenRender(instance, context);
    }
}
