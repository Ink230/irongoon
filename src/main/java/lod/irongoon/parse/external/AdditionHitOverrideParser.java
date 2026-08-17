package lod.irongoon.parse.external;

import lod.irongoon.models.AdditionHitOverride;
import org.legendofdragoon.modloader.registries.RegistryId;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AdditionHitOverrideParser {
    private static final AdditionHitOverrideParser INSTANCE = new AdditionHitOverrideParser();
    private static final Path OVERRIDE_PATH = Path.of("./mods/irongoon/addition-hit-overrides.csv");
    private static final List<String> HEADER = List.of(
        "additionId",
        "hitNumber",
        "totalFrames",
        "overlayHitFrameOffset",
        "totalSuccessFrames",
        "overlayStartingFrameOffset"
    );

    public static AdditionHitOverrideParser getInstance() {
        return INSTANCE;
    }

    private final CSVParser csvParser = CSVParser.getInstance();

    private AdditionHitOverrideParser() {
    }

    public List<AdditionHitOverride> load() {
        if(!Files.exists(OVERRIDE_PATH)) {
            throw new IllegalStateException("Addition timing overrides file does not exist: " + OVERRIDE_PATH);
        }

        final List<String[]> rows;
        try {
            rows = this.csvParser.load(OVERRIDE_PATH.toString());
        } catch(final RuntimeException exception) {
            throw new IllegalStateException("Could not read addition timing overrides from " + OVERRIDE_PATH, exception);
        }
        if(rows.isEmpty()) throw new IllegalStateException("Addition timing overrides file is empty: " + OVERRIDE_PATH);
        this.validateHeader(rows.getFirst());

        final List<AdditionHitOverride> overrides = new ArrayList<>();
        final Set<OverrideKey> seen = new HashSet<>();
        for(int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
            final String[] row = rows.get(rowIndex);
            if(row.length == 1 && row[0].isBlank()) continue;
            if(row.length != HEADER.size()) {
                throw new IllegalStateException("Invalid addition timing override row " + (rowIndex + 1)
                    + ": expected " + HEADER.size() + " columns, got " + row.length);
            }

            final RegistryId additionId;
            final int hitNumber;
            try {
                additionId = new RegistryId(required(row[0], rowIndex, "additionId"));
                hitNumber = Integer.parseInt(required(row[1], rowIndex, "hitNumber"));
            } catch(final RuntimeException exception) {
                throw new IllegalStateException("Invalid addition timing override row " + (rowIndex + 1), exception);
            }
            if(hitNumber < 1) {
                throw new IllegalStateException("Invalid addition timing override for " + additionId + " hit " + hitNumber
                    + ": hit numbers are one-based");
            }

            final OverrideKey key = new OverrideKey(additionId, hitNumber);
            if(!seen.add(key)) {
                throw new IllegalStateException("Duplicate addition timing override for " + additionId + " hit " + hitNumber);
            }

            overrides.add(new AdditionHitOverride(
                additionId,
                hitNumber,
                optionalInt(row[2], rowIndex, "totalFrames"),
                optionalInt(row[3], rowIndex, "overlayHitFrameOffset"),
                optionalInt(row[4], rowIndex, "totalSuccessFrames"),
                optionalInt(row[5], rowIndex, "overlayStartingFrameOffset")
            ));
        }

        return List.copyOf(overrides);
    }

    private void validateHeader(final String[] header) {
        if(header.length != HEADER.size()) {
            throw new IllegalStateException("Invalid addition timing override header in " + OVERRIDE_PATH);
        }
        for(int column = 0; column < header.length; column++) {
            if(!HEADER.get(column).equals(header[column].trim())) {
                throw new IllegalStateException("Invalid addition timing override header column " + (column + 1)
                    + ": expected " + HEADER.get(column) + ", got " + header[column]);
            }
        }
    }

    private static String required(final String value, final int rowIndex, final String column) {
        final String trimmed = value.trim();
        if(trimmed.isEmpty()) {
            throw new IllegalStateException("Missing " + column + " at addition timing override row " + (rowIndex + 1));
        }
        return trimmed;
    }

    @Nullable
    private static Integer optionalInt(final String value, final int rowIndex, final String column) {
        if(value.isBlank()) return null;
        try {
            return Integer.valueOf(value.trim());
        } catch(final NumberFormatException exception) {
            throw new IllegalStateException("Invalid " + column + " at addition timing override row " + (rowIndex + 1), exception);
        }
    }

    private record OverrideKey(RegistryId additionId, int hitNumber) {
    }
}
