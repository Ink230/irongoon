package lod.irongoon.config;

import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.StringConfigEntry;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.core.lang.I18nText;
import legend.game.inventory.screens.controls.Button;
import lod.irongoon.config.ui.IrongoonConfigEditorSession;
import lod.irongoon.config.ui.IrongoonConfigScreen;

/** Hidden String-only campaign snapshot entry safe for SC config serialization. */
public final class IrongoonSnapshotConfigEntry extends StringConfigEntry {
    private final ConfigEntry<String> seedEntry;
    private final ConfigEntry<String> rememberedProfileEntry;

    public IrongoonSnapshotConfigEntry(final ConfigEntry<String> seedEntry, final ConfigEntry<String> rememberedProfileEntry) {
        super("", 2, ConfigStorageLocation.CAMPAIGN, ConfigCategory.OTHER);
        this.seedEntry = seedEntry;
        this.rememberedProfileEntry = rememberedProfileEntry;
        this.setEditControl((current, config) -> this.createConfigureButton(config));
    }

    private Button createConfigureButton(final ConfigCollection config) {
        final Button button = new Button(new I18nText("irongoon.ui.config.configure"));
        button.onPressed(() -> button.getScreen().getStack().pushScreen(new IrongoonConfigScreen(new IrongoonConfigEditorSession(config, this.seedEntry, this, this.rememberedProfileEntry))));
        return button;
    }

    @Override
    public boolean availableInBattle() {
        return false;
    }
}
