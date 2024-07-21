package me.zziger.obsoverlay.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import me.zziger.obsoverlay.OBSOverlayConfig;
import net.minecraft.client.gui.screen.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;

import me.zziger.obsoverlay.OBSOverlay;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import static net.neoforged.neoforge.common.NeoForge.EVENT_BUS;

@Mod(OBSOverlay.MOD_ID)
public final class OBSOverlayNeoForge {
    private static void registerModsPage() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> {
            return OBSOverlayConfig.getScreenSupplier(parent).get();
        });
    }

    public OBSOverlayNeoForge() {
        // Run our common setup.
        OBSOverlay.init();

        if (FMLEnvironment.dist.isClient()) {
            registerModsPage();
        }
    }
}
