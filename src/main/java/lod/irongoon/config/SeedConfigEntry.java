package lod.irongoon.config;

import legend.core.IoHelper;
import legend.core.MathHelper;
import legend.game.inventory.screens.controls.Textbox;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import legend.core.GameEngine;
import lod.irongoon.Irongoon;

public class SeedConfigEntry extends ConfigEntry<String> {
    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public SeedConfigEntry(String seed) {
        super(seed, ConfigStorageLocation.CAMPAIGN, ConfigCategory.OTHER, SeedConfigEntry::serializer, bytes -> deserializer(bytes, ""));

        this.setEditControl((current, gameState) -> {
            var text = new Textbox();

            text.setSize(165, 16);
            text.setPos(25, 28);
            text.setZ(1);
            text.setText(seed);
            text.setMaxLength(15);

            return text;
        });
    }

    private static byte[] serializer(final String val) {
        return IoHelper.stringToBytes(val, 2);
    }

    private static String deserializer(final byte[] bytes, final String defaultValue) {
        return IoHelper.stringFromBytes(bytes, 2, defaultValue);
    }
}
