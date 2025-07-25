package me.zziger.obsoverlay;

import me.zziger.obsoverlay.api.IOverlayAPI;
import me.zziger.obsoverlay.api.impl.DummyOverlayAPI;
import me.zziger.obsoverlay.api.impl.NormalOverlayAPI;
import me.zziger.obsoverlay.compat.ImmediatelyFastCompat;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OBSOverlay {
    public static final String MOD_ID = "obs_overlay";
    public static final Logger LOGGER = LoggerFactory.getLogger("obs_overlay");

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    private static OverlayRenderer renderer = null;
    private static IOverlayAPI api = new DummyOverlayAPI();
    private static boolean initialized = false;

    public static boolean getIsInitialized() {
        return initialized;
    }

    /**
     * This method can be invoked even if mode is not initialized.
     * Call this method each time you need the API, do not cache the result.
     * If the library is not initialized - it will return a dummy API instance, which does nothing
     * @return API instance
     */
    public static IOverlayAPI getAPI() {
        return api;
    }

    /**
     * This method can return internal renderer instance, null if not initialized
     * If possible, better use {@link #getAPI()} instead.
     * @return Internal renderer instance, or null if renderer is not initialized
     */
    @Nullable
    public static OverlayRenderer getRenderer() {
        return renderer;
    }

    public static void init() {
        OBSOverlayConfig.init();
        AllDefaultOverlayComponents.init();
    }

    public static void initRender() {
        try {
            renderer = new OverlayRenderer();
            api = new NormalOverlayAPI(renderer);
            initialized = true;
        } catch (Throwable e) {
            LOGGER.error("Failed to initialize OBS Overlay render", e);
            OverlayUtils.showToast(Text.literal("Failed to initialize OBS Overlay"), Text.literal(e.getMessage()));

            renderer = null;
            api = new DummyOverlayAPI();
        }
    }
}
