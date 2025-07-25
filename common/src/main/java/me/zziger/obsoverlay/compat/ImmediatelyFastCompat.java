package me.zziger.obsoverlay.compat;

import dev.architectury.platform.Platform;
import net.raphimc.immediatelyfastapi.BatchingAccess;
import net.raphimc.immediatelyfastapi.ImmediatelyFastApi;

public class ImmediatelyFastCompat {
    private static boolean initialized = false;
    private static boolean hasMod = false;

    public static boolean hasImmediatelyFast() {
        if (initialized) return hasMod;
        hasMod = Platform.isModLoaded("immediatelyfast");
        initialized = true;
        return hasMod;
    }
}
