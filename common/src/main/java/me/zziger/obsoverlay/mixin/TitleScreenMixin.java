package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    private void drawStart(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (OBSOverlay.libraryInitialized) return;

        context.drawTexture(Identifier.of("minecraft", "textures/gui/container/beacon.png"), 0, 0, 112, 228, 18, 18);
        context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("obs_overlay.failed_to_init"), 17, 4, 0xFFFFFF, true);
    }
}
