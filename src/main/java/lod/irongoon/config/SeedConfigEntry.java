package lod.irongoon.config;

import legend.core.IoHelper;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

/** Hidden legacy-compatible campaign seed entry edited through the Irongoon screen. */
public final class SeedConfigEntry extends ConfigEntry<String> {
    public SeedConfigEntry(final String seed) {
        super(seed, ConfigStorageLocation.CAMPAIGN, ConfigCategory.OTHER, SeedConfigEntry::serializer, bytes -> deserializer(bytes, ""));
    }

    private static byte[] serializer(final String value) {
        return IoHelper.stringToBytes(value, 2);
    }

    private static String deserializer(final byte[] bytes, final String defaultValue) {
        return IoHelper.stringFromBytes(bytes, 2, defaultValue);
    }
}
