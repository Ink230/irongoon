package lod.irongoon.config;

import java.util.Objects;

/** Versioned UTF-8 campaign value containing a profile identifier and canonical YAML. */
public record IrongoonConfigPayload(int version, String sourceProfileId, String yaml) {
    private static final String VERSION_PREFIX = "v1";

    public IrongoonConfigPayload {
        if(version != 1) throw new IllegalArgumentException("Unsupported Irongoon config payload version " + version);
        sourceProfileId = requireLine(sourceProfileId, "source profile ID");
        yaml = Objects.requireNonNull(yaml, "yaml");
    }

    public static IrongoonConfigPayload fromSnapshot(final IrongoonConfigProfile profile, final IrongoonConfigSnapshot snapshot) {
        return new IrongoonConfigPayload(1, profile.filename(), IrongoonConfigCodec.serializeCanonical(snapshot));
    }

    public static IrongoonConfigPayload decode(final String value) {
        final String payload = Objects.requireNonNull(value, "value");
        final int sourceEnd = payload.indexOf('\n');
        final int yamlStart = sourceEnd < 0 ? -1 : payload.indexOf('\n', sourceEnd + 1);
        if(sourceEnd < 0 || yamlStart < 0 || !VERSION_PREFIX.equals(payload.substring(0, sourceEnd))) {
            throw new IllegalStateException("Invalid Irongoon campaign config payload");
        }
        return new IrongoonConfigPayload(1, payload.substring(sourceEnd + 1, yamlStart), payload.substring(yamlStart + 1));
    }

    public IrongoonConfigSnapshot snapshot() {
        return IrongoonConfigCodec.read("campaign profile " + this.sourceProfileId, this.yaml);
    }

    public String encode() {
        return VERSION_PREFIX + '\n' + this.sourceProfileId + '\n' + this.yaml;
    }

    private static String requireLine(final String value, final String description) {
        final String line = Objects.requireNonNull(value, description);
        if(line.isBlank() || line.indexOf('\n') >= 0 || line.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Irongoon " + description + " must be a non-blank line");
        }
        return line;
    }
}
