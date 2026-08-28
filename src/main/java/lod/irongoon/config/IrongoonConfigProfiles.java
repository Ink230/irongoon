package lod.irongoon.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Config-profile discovery and atomic file persistence leaf. */
public final class IrongoonConfigProfiles {
    private static final IrongoonConfigProfiles INSTANCE = new IrongoonConfigProfiles(Path.of("./mods/irongoon"));
    private static final String DEFAULT_FILENAME = "default-ig.yaml";
    private static final String PROFILE_SUFFIX = "-ig.yaml";

    private final Path configsDirectory;
    private final Path legacyPath;
    private List<IrongoonConfigProfile> profiles = List.of();

    private IrongoonConfigProfiles(final Path modDirectory) {
        this.configsDirectory = modDirectory.resolve("configs");
        this.legacyPath = modDirectory.resolve("config.yaml");
    }

    public static IrongoonConfigProfiles getInstance() {
        return INSTANCE;
    }

    /** Creates an isolated repository for validation and non-runtime tooling. */
    public static IrongoonConfigProfiles forDirectory(final Path modDirectory) {
        return new IrongoonConfigProfiles(modDirectory);
    }

    public synchronized List<IrongoonConfigProfile> rescan() {
        final List<IrongoonConfigProfile> discovered = new ArrayList<>();
        if(Files.isDirectory(this.configsDirectory)) {
            try (var paths = Files.list(this.configsDirectory)) {
                paths.filter(Files::isRegularFile)
                    .filter(this::isProfileFile)
                    .map(IrongoonConfigProfile::profile)
                    .sorted(Comparator.comparing(IrongoonConfigProfile::filename, String.CASE_INSENSITIVE_ORDER).thenComparing(IrongoonConfigProfile::filename))
                    .forEach(discovered::add);
            } catch(final IOException exception) {
                throw new IllegalStateException("Unable to discover Irongoon config profiles in " + this.configsDirectory, exception);
            }
        }
        this.profiles = List.copyOf(discovered);
        return this.availableProfiles();
    }

    public synchronized List<IrongoonConfigProfile> availableProfiles() {
        if(!this.profiles.isEmpty()) return this.profiles;
        if(Files.isRegularFile(this.legacyPath)) return List.of(IrongoonConfigProfile.legacy(this.legacyPath));
        return List.of(IrongoonConfigProfile.blueprint());
    }

    public synchronized Selection select(final String rememberedFilename, final boolean rememberEnabled) {
        this.rescan();
        final List<String> warnings = new ArrayList<>();
        final List<IrongoonConfigProfile> candidates = new ArrayList<>();
        if(rememberEnabled && rememberedFilename != null && !rememberedFilename.isBlank()) {
            this.profiles.stream().filter(profile -> profile.filename().equalsIgnoreCase(rememberedFilename)).findFirst().ifPresentOrElse(candidates::add,
                () -> warnings.add("Remembered Irongoon profile is unavailable: " + rememberedFilename));
        }
        this.profiles.stream().filter(profile -> profile.filename().equalsIgnoreCase(DEFAULT_FILENAME)).findFirst().ifPresent(candidates::add);
        this.profiles.stream().filter(profile -> !candidates.contains(profile)).forEach(candidates::add);
        if(candidates.isEmpty()) candidates.addAll(this.availableProfiles());

        for(final IrongoonConfigProfile candidate : candidates) {
            try {
                final IrongoonConfigSnapshot snapshot = this.load(candidate);
                final List<String> resultWarnings = new ArrayList<>(warnings);
                resultWarnings.addAll(snapshot.warnings());
                return new Selection(candidate, snapshot, resultWarnings);
            } catch(final IllegalStateException exception) {
                warnings.add("Skipped invalid Irongoon profile " + candidate.filename() + ": " + exception.getMessage());
            }
        }

        final IrongoonConfigProfile blueprint = IrongoonConfigProfile.blueprint();
        return new Selection(blueprint, this.load(blueprint), warnings);
    }

    /** Explicit loads fail without changing selection or a source file. */
    public IrongoonConfigSnapshot load(final IrongoonConfigProfile profile) {
        return switch(profile.kind()) {
            case PROFILE, LEGACY -> IrongoonConfigCodec.read(profile.path());
            case BLUEPRINT -> IrongoonConfigCodec.fromValues("Blueprint", IrongoonConfigSchema.blueprintValues());
        };
    }

    public synchronized IrongoonConfigProfile saveExisting(final IrongoonConfigProfile profile, final IrongoonConfigSnapshot snapshot) {
        if(profile.kind() == IrongoonConfigProfile.Kind.BLUEPRINT) return this.saveAsGenerated(snapshot);
        final IrongoonConfigProfile target = profile.kind() == IrongoonConfigProfile.Kind.LEGACY
            ? this.profileForFilename("legacy-ig.yaml")
            : profile;
        this.write(target.path(), snapshot);
        this.rescan();
        return target;
    }

    public synchronized IrongoonConfigProfile saveAs(final String name, final IrongoonConfigSnapshot snapshot) {
        final String filename = IrongoonConfigProfile.filenameFor(name);
        this.rescan();
        if(this.hasFilename(filename)) throw new IllegalArgumentException("Irongoon profile already exists: " + filename);
        final IrongoonConfigProfile profile = this.profileForFilename(filename);
        this.write(profile.path(), snapshot);
        this.rescan();
        return profile;
    }

    public synchronized IrongoonConfigProfile saveAsGenerated(final IrongoonConfigSnapshot snapshot) {
        this.rescan();
        for(int number = 1; ; number++) {
            final String filename = "config-" + number + PROFILE_SUFFIX;
            if(this.hasFilename(filename)) continue;
            final IrongoonConfigProfile profile = this.profileForFilename(filename);
            this.write(profile.path(), snapshot);
            this.rescan();
            return profile;
        }
    }

    public synchronized IrongoonConfigProfile rename(final IrongoonConfigProfile profile, final String name) {
        if(profile.kind() == IrongoonConfigProfile.Kind.LEGACY) return this.saveAs(name, this.load(profile));
        if(profile.kind() == IrongoonConfigProfile.Kind.BLUEPRINT) throw new IllegalArgumentException("Only saved Irongoon profiles can be renamed");
        final String filename = IrongoonConfigProfile.filenameFor(name);
        if(profile.filename().equals(filename)) return profile;
        this.rescan();
        if(!profile.filename().equalsIgnoreCase(filename) && this.hasFilename(filename)) throw new IllegalArgumentException("Irongoon profile already exists: " + filename);
        final IrongoonConfigProfile target = this.profileForFilename(filename);
        if(profile.filename().equalsIgnoreCase(filename)) {
            this.renameCaseOnly(profile.path(), target.path());
        } else {
            this.move(profile.path(), target.path(), false);
        }
        this.rescan();
        return target;
    }

    private boolean isProfileFile(final Path path) {
        try {
            final String filename = path.getFileName().toString().toLowerCase(Locale.ROOT);
            return filename.length() > PROFILE_SUFFIX.length() && filename.endsWith(PROFILE_SUFFIX) && Files.size(path) > 0;
        } catch(final IOException exception) {
            return false;
        }
    }

    private boolean hasFilename(final String filename) {
        if(!Files.isDirectory(this.configsDirectory)) return false;
        try (var paths = Files.list(this.configsDirectory)) {
            return paths.anyMatch(path -> path.getFileName().toString().equalsIgnoreCase(filename));
        } catch(final IOException exception) {
            throw new IllegalStateException("Unable to inspect Irongoon profile names in " + this.configsDirectory, exception);
        }
    }

    private IrongoonConfigProfile profileForFilename(final String filename) {
        return IrongoonConfigProfile.profile(this.configsDirectory.resolve(filename));
    }

    private void write(final Path path, final IrongoonConfigSnapshot snapshot) {
        final String yaml = IrongoonConfigCodec.serializeCanonical(snapshot);
        try {
            Files.createDirectories(this.configsDirectory);
            final Path temporary = Files.createTempFile(this.configsDirectory, path.getFileName().toString(), ".tmp");
            try {
                Files.writeString(temporary, yaml, StandardCharsets.UTF_8);
                this.move(temporary, path, true);
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch(final IOException exception) {
            throw new IllegalStateException("Unable to save Irongoon profile " + path, exception);
        }
    }

    private void move(final Path source, final Path target, final boolean replace) {
        try {
            if(replace) {
                try {
                    Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch(final AtomicMoveNotSupportedException exception) {
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                try {
                    Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
                } catch(final AtomicMoveNotSupportedException exception) {
                    Files.move(source, target);
                }
            }
        } catch(final FileAlreadyExistsException exception) {
            throw new IllegalArgumentException("Irongoon profile already exists: " + target.getFileName(), exception);
        } catch(final IOException exception) {
            throw new IllegalStateException("Unable to move Irongoon profile to " + target, exception);
        }
    }

    private void renameCaseOnly(final Path source, final Path target) {
        Path temporary = null;
        try {
            temporary = Files.createTempFile(this.configsDirectory, ".rename-", ".tmp");
            Files.delete(temporary);
            this.move(source, temporary, false);
            try {
                this.move(temporary, target, false);
            } catch(final RuntimeException exception) {
                this.move(temporary, source, false);
                throw exception;
            }
        } catch(final IOException exception) {
            throw new IllegalStateException("Unable to rename Irongoon profile to " + target, exception);
        } finally {
            if(temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch(final IOException ignored) {
                    // The profile move already succeeded or reported its own failure.
                }
            }
        }
    }

    public record Selection(IrongoonConfigProfile profile, IrongoonConfigSnapshot snapshot, List<String> warnings) {
        public Selection {
            warnings = List.copyOf(warnings);
        }
    }
}
