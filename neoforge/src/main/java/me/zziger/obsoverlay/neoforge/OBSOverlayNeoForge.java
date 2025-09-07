package me.zziger.obsoverlay.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import me.zziger.obsoverlay.OBSOverlayConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import me.zziger.obsoverlay.OBSOverlay;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.common.MinecraftForge;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@Mod(OBSOverlay.MOD_ID)
public final class OBSOverlayNeoForge {
    private static void registerModsPage() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((client, parent) -> {
            return OBSOverlayConfig.getScreenSupplier(parent).get();
        }));
    }

    public OBSOverlayNeoForge() {
        // Run our common setup.
        OBSOverlay.init();

        if (FMLEnvironment.dist.isClient()) {
            registerModsPage();
        }
    }
}
