package lod.irongoon.config.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Optional platform leaf for opening the profile import/export folder. */
public final class IrongoonConfigFolder {
    private IrongoonConfigFolder() {}

    public static boolean canOpen() {
        try {
            return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN);
        } catch(final UnsupportedOperationException | SecurityException exception) {
            return false;
        }
    }

    public static void open(final Path directory) {
        if(!canOpen()) throw new IllegalStateException("Opening folders is unavailable on this platform");

        try {
            Files.createDirectories(directory);
            Desktop.getDesktop().open(directory.toFile());
        } catch(final IOException | SecurityException exception) {
            throw new IllegalStateException("Unable to open Irongoon config folder: " + directory, exception);
        }
    }
}
