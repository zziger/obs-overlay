package me.zziger.obsoverlay.fabric.client;

import me.zziger.obsoverlay.OBSOverlay;
import net.fabricmc.api.ClientModInitializer;

public final class OBSOverlayFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OBSOverlay.init();
    }
}
