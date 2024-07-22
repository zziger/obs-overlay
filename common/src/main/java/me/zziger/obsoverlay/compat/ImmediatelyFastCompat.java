package me.zziger.obsoverlay.compat;

import dev.architectury.platform.Platform;
import net.raphimc.immediatelyfastapi.BatchingAccess;
import net.raphimc.immediatelyfastapi.ImmediatelyFastApi;

public class ImmediatelyFastCompat {
    private static boolean initialized = false;
    private static BatchingAccess api = null;

    public static void forceDraw() {
        if (!initialized) {
            if (Platform.isModLoaded("immediatelyfast")) {
                api = ImmediatelyFastApi.getApiImpl().getBatching();
            }
            initialized = true;
        }

        if (api != null) {
            api.forceDrawBuffers();
        }
    }
}
