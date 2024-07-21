package me.zziger.obsoverlay;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.architectury.platform.Platform;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import me.zziger.obsoverlay.registry.OverlayComponentRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

@Config(name = OBSOverlay.MOD_ID)
public class OBSOverlayConfig implements ConfigData {
    private static OBSOverlayConfig INSTANCE;

    public HashMap<String, Boolean> overlayComponents = new HashMap<>();
    public HashMap<String, Boolean> autoHideComponents = new HashMap<>();

    public boolean showTestIcon;

    public static void init() {
        Path configPath = getPath();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (Files.exists(configPath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(configPath);
                OBSOverlayConfig ret = gson.fromJson(reader, OBSOverlayConfig.class);
                reader.close();
                INSTANCE = ret;
            } catch (JsonParseException | IOException var4) {
                OBSOverlay.LOGGER.error("Failed to load config", var4);
            }
        } else {
            INSTANCE = new OBSOverlayConfig();
        }
    }

    public static OBSOverlayConfig get() {
        return INSTANCE;
    }

    public static Path getPath() {
        return Platform.getConfigFolder().resolve(OBSOverlay.MOD_ID + ".json");
    }

    public static Supplier<Screen> getScreenSupplier(Screen parent) {
        OBSOverlayConfig config = get();

        return () -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("obs_overlay.config.title"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(Text.empty());


            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("obs_overlay.config.show_test_icon"), config.showTestIcon)
                    .setTooltip(Text.translatable("obs_overlay.config.show_test_icon.tooltip"))
                    .setDefaultValue(false)
                    .setSaveConsumer((value) -> config.showTestIcon = value)
                    .build());

            HashMap<String, BooleanListEntry> overlayEntries = new HashMap<>();
            SubCategoryBuilder componentsToOverlay = entryBuilder.startSubCategory(Text.translatable("obs_overlay.config.components_to_overlay"))
                    .setExpanded(true)
                    .setTooltip(Text.translatable("obs_overlay.config.components_to_overlay.tooltip"));

            OverlayComponentRegistry.components.forEach(component -> {
                BooleanListEntry entry = entryBuilder.startBooleanToggle(Text.translatable("obs_overlay.component." + component.getId()), component.isOverlayEnabled())
                        .setTooltip(Text.translatable("obs_overlay.component." + component.getId() + ".tooltip"))
                        .setDefaultValue(component.isOverlayEnabledDefault())
                        .setSaveConsumer(component::setOverlayEnabled)
                        .build();
                overlayEntries.put(component.getId(), entry);
                componentsToOverlay.add(entry);
            });
            general.addEntry(componentsToOverlay.build());

            SubCategoryBuilder autoHideComponents = entryBuilder.startSubCategory(Text.translatable("obs_overlay.config.auto_hide_components"))
                    .setExpanded(false)
                    .setTooltip(Text.translatable("obs_overlay.config.auto_hide_components.tooltip"));

            OverlayComponentRegistry.components.forEach(component -> {
                if (!component.canAutoHide()) return;
                if (!overlayEntries.containsKey(component.getId())) return;

                autoHideComponents.add(entryBuilder.startBooleanToggle(Text.translatable("obs_overlay.component." + component.getId()), component.isAutoHideEnabled())
                        .setDefaultValue(true)
                        .setRequirement(Requirement.isTrue(overlayEntries.get(component.getId())))
                        .setTooltipSupplier(() -> {
                            Text mainTooltip = Text.translatable("obs_overlay.component." + component.getId() + ".tooltip");
                            if (overlayEntries.get(component.getId()).getValue()) {
                                return Optional.of(new Text[]{mainTooltip});
                            } else {
                                Text optionName = Text.translatable("obs_overlay.component." + component.getId());
                                return Optional.of(new Text[] {mainTooltip, Text.translatable("obs_overlay.config.requires_overlay_enabled", optionName).formatted(Formatting.RED)});
                            }
                        })
                        .setSaveConsumer(component::setAutoHideEnabled)
                        .build());
            });
            general.addEntry(autoHideComponents.build());

            builder.setSavingRunnable(() -> {
                Path configPath = getPath();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                try {
                    Files.createDirectories(configPath.getParent());
                    BufferedWriter writer = Files.newBufferedWriter(configPath);
                    gson.toJson(config, writer);
                    writer.close();
                } catch (IOException e) {
                    OBSOverlay.LOGGER.error("Failed to save config", e);
                }
            });

            return builder.build();
        };
    }
}
