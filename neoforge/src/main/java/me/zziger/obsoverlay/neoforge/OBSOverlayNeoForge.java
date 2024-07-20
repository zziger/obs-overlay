package me.zziger.obsoverlay.neoforge;

import net.neoforged.fml.common.Mod;

import me.zziger.obsoverlay.OBSOverlay;

@Mod(OBSOverlay.MOD_ID)
public final class OBSOverlayNeoForge {
    public OBSOverlayNeoForge() {
        // Run our common setup.
        OBSOverlay.init();
    }
}
