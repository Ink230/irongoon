package lod.irongoon.config;

import legend.core.GameEngine;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.lodmod.Legacy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.List;
import java.util.function.Predicate;

/** Campaign snapshot selection, validation, staging and application leaf. */
public final class IrongoonCampaignConfig {
    private static final IrongoonCampaignConfig INSTANCE = new IrongoonCampaignConfig();
    private static final Logger LOGGER = LogManager.getFormatterLogger(IrongoonCampaignConfig.class);

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final IrongoonConfigProfiles profiles = IrongoonConfigProfiles.getInstance();

    private IrongoonCampaignConfig() {}

    public static IrongoonCampaignConfig getInstance() {
        return INSTANCE;
    }

    public SelectionResult stageNewCampaign(
        final ConfigCollection configCollection,
        final ConfigEntry<String> snapshotEntry,
        final ConfigEntry<String> rememberedProfileEntry,
        final boolean rememberEnabled
    ) {
        final IrongoonConfigProfiles.Selection selection = this.profiles.select(
            configCollection.getConfig(rememberedProfileEntry),
            rememberEnabled
        );
        this.stageSelection(configCollection, snapshotEntry, rememberedProfileEntry, selection);
        return SelectionResult.selected(selection);
    }

    public void applyNewCampaign(
        final ConfigCollection configCollection,
        final ConfigEntry<String> snapshotEntry,
        final ConfigEntry<String> rememberedProfileEntry,
        final boolean rememberEnabled
    ) {
        if(this.requiresMigration(configCollection, snapshotEntry)) {
            this.stageNewCampaign(configCollection, snapshotEntry, rememberedProfileEntry, rememberEnabled);
        }
        this.applyPayload(configCollection.getConfig(snapshotEntry));
    }

    public SelectionResult applyLoadedCampaign(
        final ConfigCollection configCollection,
        final ConfigEntry<String> snapshotEntry,
        final ConfigEntry<String> rememberedProfileEntry,
        final boolean rememberEnabled
    ) {
        SelectionResult result;
        if(this.requiresMigration(configCollection, snapshotEntry)) {
            final IrongoonConfigProfiles.Selection selection = this.profiles.selectForMigration(
                configCollection.getConfig(rememberedProfileEntry),
                rememberEnabled
            );
            this.stageSelection(configCollection, snapshotEntry, rememberedProfileEntry, selection);
            result = SelectionResult.migrated(selection);
        } else {
            result = SelectionResult.existing(this.payload(configCollection, snapshotEntry).sourceProfileId());
        }
        this.applyPayload(configCollection.getConfig(snapshotEntry));
        return result;
    }

    public void stageSnapshot(
        final ConfigCollection configCollection,
        final ConfigEntry<String> snapshotEntry,
        final ConfigEntry<String> rememberedProfileEntry,
        final IrongoonConfigProfile profile,
        final IrongoonConfigSnapshot snapshot
    ) {
        this.validate(snapshot);
        final String payload = IrongoonConfigPayload.fromSnapshot(profile, snapshot).encode();
        configCollection.setConfig(snapshotEntry, payload);
        configCollection.setConfig(rememberedProfileEntry, profile.filename());
    }

    public IrongoonConfigPayload payload(final ConfigCollection configCollection, final ConfigEntry<String> snapshotEntry) {
        final String value = configCollection.getConfig(snapshotEntry);
        if(value.isBlank()) throw new IllegalStateException("Irongoon campaign config snapshot is empty");
        return IrongoonConfigPayload.decode(value);
    }

    public void validate(final IrongoonConfigSnapshot snapshot) {
        IrongoonConfigCodec.validateCharacterReferences(snapshot, Legacy.CHAR_IDS.length);
        IrongoonConfigCodec.validateDeferredRegistryReferences(
            snapshot,
            IrongoonCampaignConfig::hasElement,
            IrongoonCampaignConfig::hasItem,
            IrongoonCampaignConfig::hasEquipment
        );
    }

    private void stageSelection(
        final ConfigCollection configCollection,
        final ConfigEntry<String> snapshotEntry,
        final ConfigEntry<String> rememberedProfileEntry,
        final IrongoonConfigProfiles.Selection selection
    ) {
        this.stageSnapshot(configCollection, snapshotEntry, rememberedProfileEntry, selection.profile(), selection.snapshot());
        for(final String warning : selection.warnings()) {
            LOGGER.warn("%s", warning);
        }
    }

    private void applyPayload(final String value) {
        final IrongoonConfigSnapshot snapshot = IrongoonConfigPayload.decode(value).snapshot();
        this.validate(snapshot);
        this.config.apply(snapshot);
    }

    private boolean requiresMigration(final ConfigCollection configCollection, final ConfigEntry<String> snapshotEntry) {
        return !configCollection.hasConfig(snapshotEntry) || configCollection.getConfig(snapshotEntry).isBlank();
    }

    private static boolean hasElement(final String id) {
        return hasRegistryEntry(id, GameEngine.REGISTRIES.elements::hasEntry);
    }

    private static boolean hasItem(final String id) {
        return hasRegistryEntry(id, GameEngine.REGISTRIES.items::hasEntry);
    }

    private static boolean hasEquipment(final String id) {
        return hasRegistryEntry(id, GameEngine.REGISTRIES.equipment::hasEntry);
    }

    private static boolean hasRegistryEntry(final String id, final Predicate<RegistryId> exists) {
        try {
            return exists.test(new RegistryId(id));
        } catch(final IllegalArgumentException exception) {
            return false;
        }
    }

    public record SelectionResult(boolean migrated, String sourceProfileId, List<String> warnings) {
        private static SelectionResult existing(final String sourceProfileId) {
            return new SelectionResult(false, sourceProfileId, List.of());
        }

        private static SelectionResult selected(final IrongoonConfigProfiles.Selection selection) {
            return new SelectionResult(false, selection.profile().filename(), selection.warnings());
        }

        private static SelectionResult migrated(final IrongoonConfigProfiles.Selection selection) {
            return new SelectionResult(true, selection.profile().filename(), selection.warnings());
        }

        public SelectionResult {
            warnings = List.copyOf(warnings);
        }
    }
}
