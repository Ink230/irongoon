package lod.irongoon.config.ui;

import lod.irongoon.config.IrongoonConfigCodec;
import lod.irongoon.config.IrongoonCampaignConfig;
import lod.irongoon.config.IrongoonConfigPayload;
import lod.irongoon.config.IrongoonConfigProfile;
import lod.irongoon.config.IrongoonConfigProfiles;
import lod.irongoon.config.IrongoonConfigSchema;
import lod.irongoon.config.IrongoonConfigSnapshot;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Campaign-local draft that writes ConfigCollection only during explicit profile staging operations. */
public final class IrongoonConfigEditorSession {
    private final ConfigCollection config;
    private final ConfigEntry<String> seedEntry;
    private final ConfigEntry<String> snapshotEntry;
    private final ConfigEntry<String> rememberedProfileEntry;
    private final IrongoonCampaignConfig campaignConfig = IrongoonCampaignConfig.getInstance();
    private final IrongoonConfigProfiles profiles = IrongoonConfigProfiles.getInstance();
    private String sourceProfileId;
    private IrongoonConfigProfile selectedProfile;
    private IrongoonConfigSnapshot startingSnapshot;
    private String startingSeed;
    private IrongoonConfigSnapshot draftSnapshot;
    private String draftSeed;
    private String validationError;
    private String operationError;
    private boolean stagedForReload;

    public IrongoonConfigEditorSession(
        final ConfigCollection config,
        final ConfigEntry<String> seedEntry,
        final ConfigEntry<String> snapshotEntry,
        final ConfigEntry<String> rememberedProfileEntry
    ) {
        this.config = config;
        this.seedEntry = seedEntry;
        this.snapshotEntry = snapshotEntry;
        this.rememberedProfileEntry = rememberedProfileEntry;

        final String payload = config.getConfig(snapshotEntry);
        if(payload == null || payload.isBlank()) {
            this.sourceProfileId = IrongoonConfigProfile.blueprint().filename();
            this.startingSnapshot = IrongoonConfigCodec.fromValues("editor blueprint", Map.of());
        } else {
            final IrongoonConfigPayload decoded = IrongoonConfigPayload.decode(payload);
            this.sourceProfileId = decoded.sourceProfileId();
            this.startingSnapshot = decoded.snapshot();
        }

        this.draftSnapshot = this.startingSnapshot;
        this.startingSeed = config.getConfig(seedEntry);
        this.draftSeed = this.startingSeed;
        this.rescan();
    }

    public ConfigCollection config() {
        return this.config;
    }

    public ConfigEntry<String> seedEntry() {
        return this.seedEntry;
    }

    public ConfigEntry<String> snapshotEntry() {
        return this.snapshotEntry;
    }

    public ConfigEntry<String> rememberedProfileEntry() {
        return this.rememberedProfileEntry;
    }

    public String sourceProfileId() {
        return this.sourceProfileId;
    }

    public String selectedProfileDisplayName() {
        if(this.selectedProfile != null) return this.selectedProfile.displayName();
        return this.sourceProfileId;
    }

    public IrongoonConfigProfile.Kind selectedProfileKind() {
        return this.selectedProfile == null ? IrongoonConfigProfile.Kind.BLUEPRINT : this.selectedProfile.kind();
    }

    public List<IrongoonConfigProfile> availableProfiles() {
        return this.profiles.availableProfiles();
    }

    public IrongoonConfigSnapshot draft() {
        return this.draftSnapshot;
    }

    public String draftSeed() {
        return this.draftSeed;
    }

    public boolean dirty() {
        return !this.startingSnapshot.values().equals(this.draftSnapshot.values()) || !this.startingSeed.equals(this.draftSeed);
    }

    public String validationError() {
        return this.validationError;
    }

    public String operationError() {
        return this.operationError;
    }

    public boolean stagedForReload() {
        return this.stagedForReload;
    }

    public void setOperationError(final String error) {
        this.operationError = error == null || error.isBlank() ? "Unable to update Irongoon profile" : error;
    }

    public boolean update(final String key, final Object value) {
        return this.update(Map.of(IrongoonConfigSchema.canonicalKey(key), value));
    }

    public boolean update(final Map<String, Object> updates) {
        final Map<String, Object> values = new LinkedHashMap<>(this.draftSnapshot.values());
        for(final var update : updates.entrySet()) {
            values.put(IrongoonConfigSchema.canonicalKey(update.getKey()), update.getValue());
        }

        try {
            this.draftSnapshot = IrongoonConfigCodec.fromValues("editor draft", values);
            this.validationError = null;
            this.operationError = null;
            return true;
        } catch(final IllegalStateException exception) {
            this.validationError = exception.getMessage();
            return false;
        }
    }

    public boolean updateSeed(final String value) {
        if(value == null || value.isBlank() || value.length() > 15 || !value.matches("[0-9a-fA-F]+")) {
            this.validationError = "Campaign seed must be 1 to 15 hexadecimal digits";
            return false;
        }

        this.draftSeed = value;
        this.validationError = null;
        this.operationError = null;
        return true;
    }

    public boolean selectProfile(final String filename) {
        try {
            this.rescan();
            final IrongoonConfigProfile profile = this.profile(filename)
                .orElseThrow(() -> new IllegalArgumentException("Irongoon profile is unavailable: " + filename));
            final IrongoonConfigSnapshot snapshot = this.profiles.load(profile);
            this.stage(profile, snapshot);
            return true;
        } catch(final RuntimeException exception) {
            this.operationError = this.message(exception);
            return false;
        }
    }

    public boolean saveExisting() {
        if(this.selectedProfile == null || this.selectedProfile.kind() == IrongoonConfigProfile.Kind.BLUEPRINT) {
            this.operationError = "Select a saved profile before saving";
            return false;
        }

        if(!this.canPersistDraft()) return false;

        try {
            this.campaignConfig.validate(this.draftSnapshot);
            final IrongoonConfigProfile saved = this.profiles.saveExisting(this.selectedProfile, this.draftSnapshot);
            this.stage(saved, this.draftSnapshot);
            return true;
        } catch(final RuntimeException exception) {
            this.operationError = this.message(exception);
            return false;
        }
    }

    public boolean saveAs(final String name) {
        if(!this.canPersistDraft()) return false;

        try {
            this.campaignConfig.validate(this.draftSnapshot);
            final IrongoonConfigProfile saved = name == null || name.isBlank()
                ? this.profiles.saveAsGenerated(this.draftSnapshot)
                : this.profiles.saveAs(name, this.draftSnapshot);
            this.stage(saved, this.draftSnapshot);
            return true;
        } catch(final RuntimeException exception) {
            this.operationError = this.message(exception);
            return false;
        }
    }

    public boolean rename(final String name) {
        if(this.selectedProfile == null || this.selectedProfile.kind() == IrongoonConfigProfile.Kind.BLUEPRINT) {
            this.operationError = "Only saved profiles can be renamed";
            return false;
        }

        if(!this.canPersistDraft()) return false;

        try {
            this.campaignConfig.validate(this.draftSnapshot);
            final IrongoonConfigProfile renamed = this.profiles.rename(this.selectedProfile, name);
            this.stage(renamed, this.draftSnapshot);
            return true;
        } catch(final RuntimeException exception) {
            this.operationError = this.message(exception);
            return false;
        }
    }

    public void rescan() {
        try {
            this.profiles.rescan();
            this.selectedProfile = this.profile(this.sourceProfileId).orElse(null);
            this.operationError = null;
        } catch(final RuntimeException exception) {
            this.operationError = this.message(exception);
        }
    }

    private Optional<IrongoonConfigProfile> profile(final String filename) {
        return this.profiles.availableProfiles().stream()
            .filter(profile -> profile.filename().equalsIgnoreCase(filename))
            .findFirst();
    }

    private boolean canPersistDraft() {
        if(this.validationError == null) return true;

        this.operationError = "Resolve the validation error before saving";
        return false;
    }

    private void stage(final IrongoonConfigProfile profile, final IrongoonConfigSnapshot snapshot) {
        this.campaignConfig.validate(snapshot);
        this.campaignConfig.stageSnapshot(this.config, this.snapshotEntry, this.rememberedProfileEntry, profile, snapshot);
        this.config.setConfig(this.seedEntry, this.draftSeed);
        this.sourceProfileId = profile.filename();
        this.selectedProfile = profile;
        this.startingSnapshot = snapshot;
        this.draftSnapshot = snapshot;
        this.startingSeed = this.draftSeed;
        this.validationError = null;
        this.operationError = null;
        this.stagedForReload = true;
    }

    private String message(final RuntimeException exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
            ? "Unable to update Irongoon profile"
            : exception.getMessage();
    }
}
