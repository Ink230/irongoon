package lod.irongoon.config.ui;

import lod.irongoon.config.IrongoonConfigCodec;
import lod.irongoon.config.IrongoonConfigPayload;
import lod.irongoon.config.IrongoonConfigSchema;
import lod.irongoon.config.IrongoonConfigSnapshot;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;

import java.util.LinkedHashMap;
import java.util.Map;

/** Campaign-local draft; this class never writes ConfigCollection or runtime configuration. */
public final class IrongoonConfigEditorSession {
    private final ConfigCollection config;
    private final ConfigEntry<String> seedEntry;
    private final ConfigEntry<String> snapshotEntry;
    private final ConfigEntry<String> rememberedProfileEntry;
    private final String sourceProfileId;
    private final IrongoonConfigSnapshot startingSnapshot;
    private final String startingSeed;
    private IrongoonConfigSnapshot draftSnapshot;
    private String draftSeed;
    private String validationError;

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
            this.sourceProfileId = "Blueprint";
            this.startingSnapshot = IrongoonConfigCodec.fromValues("editor blueprint", Map.of());
        } else {
            final IrongoonConfigPayload decoded = IrongoonConfigPayload.decode(payload);
            this.sourceProfileId = decoded.sourceProfileId();
            this.startingSnapshot = decoded.snapshot();
        }

        this.draftSnapshot = this.startingSnapshot;
        this.startingSeed = config.getConfig(seedEntry);
        this.draftSeed = this.startingSeed;
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
        return true;
    }
}
