package lod.irongoon.config;

import java.nio.file.Path;
import java.util.Locale;

/** Immutable identity for a discoverable Irongoon configuration source. */
public record IrongoonConfigProfile(String filename, String displayName, Path path, Kind kind) {
    public enum Kind {
        PROFILE,
        LEGACY,
        BLUEPRINT
    }

    private static final String PROFILE_SUFFIX = "-ig.yaml";

    public static IrongoonConfigProfile profile(final Path path) {
        final String filename = path.getFileName().toString();
        final String basename = filename.substring(0, filename.length() - PROFILE_SUFFIX.length());
        return new IrongoonConfigProfile(filename, displayName(basename), path, Kind.PROFILE);
    }

    public static IrongoonConfigProfile legacy(final Path path) {
        return new IrongoonConfigProfile("config.yaml", "Legacy", path, Kind.LEGACY);
    }

    public static IrongoonConfigProfile blueprint() {
        return new IrongoonConfigProfile("blueprint", "Blueprint", null, Kind.BLUEPRINT);
    }

    public static String filenameFor(final String input) {
        if(input == null || input.isBlank()) throw new IllegalArgumentException("Irongoon profile name cannot be empty");
        final String trimmed = input.trim();
        if(trimmed.contains("/") || trimmed.contains("\\") || trimmed.contains("..")) throw new IllegalArgumentException("Irongoon profile name cannot contain a path");

        String basename = trimmed.toLowerCase(Locale.ROOT);
        if(basename.endsWith(PROFILE_SUFFIX)) basename = basename.substring(0, basename.length() - PROFILE_SUFFIX.length());
        basename = basename.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        if(basename.isEmpty()) throw new IllegalArgumentException("Irongoon profile name must contain letters or numbers");
        if(isReserved(basename)) throw new IllegalArgumentException("Irongoon profile name is reserved: " + basename);
        return basename + PROFILE_SUFFIX;
    }

    private static String displayName(final String basename) {
        if(basename.isEmpty()) return basename;
        return Character.toUpperCase(basename.charAt(0)) + basename.substring(1);
    }

    private static boolean isReserved(final String basename) {
        if(basename.equals("con") || basename.equals("prn") || basename.equals("aux") || basename.equals("nul")) return true;
        return basename.matches("com[1-9]|lpt[1-9]");
    }
}
