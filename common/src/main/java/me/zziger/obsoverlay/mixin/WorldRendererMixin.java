package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", at = @At(value = "RETURN"))
    private void renderWorld(CallbackInfo ci) {
        OBSOverlay.getAPI().backupDepth(false);
    }

}
