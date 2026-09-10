package lod.irongoon.config.presets;

import legend.core.lang.I18nText;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigPreset;
import legend.game.saves.ConfigPresetEntry;
import lod.irongoon.config.IrongoonConfigCodec;
import lod.irongoon.config.IrongoonConfigPayload;
import lod.irongoon.config.IrongoonConfigProfile;
import lod.irongoon.config.IrongoonConfigSchema;
import lod.irongoon.config.IrongoonConfigSnapshot;

import java.util.concurrent.CompletableFuture;

/** Bundled presets use the shipped schema without reading or changing local profiles. */
public final class IrongoonConfigPresets {
    private static final IrongoonConfigPresets INSTANCE = new IrongoonConfigPresets();

    private IrongoonConfigPresets() {}

    public static IrongoonConfigPresets getInstance() {
        return INSTANCE;
    }

    public ConfigPresetEntry blueprint(final ConfigEntry<String> snapshotEntry) {
        final IrongoonConfigSnapshot snapshot = IrongoonConfigCodec.fromValues("Blueprint", IrongoonConfigSchema.blueprintValues());
        final ConfigCollection config = new ConfigCollection();
        config.setConfig(snapshotEntry, IrongoonConfigPayload.fromSnapshot(IrongoonConfigProfile.blueprint(), snapshot).encode());

        // Leave the campaign seed unset so new campaigns use the current seed entry default.
        final ConfigPreset preset = new ConfigPreset(new I18nText("irongoon.config_presets.blueprint"), config);
        return new ConfigPresetEntry(null, preset.name, CompletableFuture.completedFuture(preset), false);
    }
}
