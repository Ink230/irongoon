package lod.irongoon.config;

import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.StringConfigEntry;

/** Hidden String-only campaign snapshot entry safe for SC config serialization. */
public final class IrongoonSnapshotConfigEntry extends StringConfigEntry {
    public IrongoonSnapshotConfigEntry() {
        super("", 2, ConfigStorageLocation.CAMPAIGN, ConfigCategory.OTHER);
    }

    @Override
    public boolean availableInBattle() {
        return false;
    }
}
