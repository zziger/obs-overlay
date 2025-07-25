package me.zziger.obsoverlay;

import me.zziger.obsoverlay.compat.ImmediatelyFastCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;

public class OverlayUtils {
    public static void forceDraw(VertexConsumerProvider consumer) {
        if (ImmediatelyFastCompat.hasImmediatelyFast() && consumer instanceof BatchableBufferSource batchable) batchable.draw();
        if (consumer instanceof VertexConsumerProvider.Immediate immediate) immediate.draw();
    }

    public static void showToast(Text title, Text description) {
        MinecraftClient.getInstance().submit(() ->
                MinecraftClient.getInstance()
                        .getToastManager()
                        .add(new SystemToast(SystemToast.Type.LOW_DISK_SPACE, title, description))
        );
    }
}
