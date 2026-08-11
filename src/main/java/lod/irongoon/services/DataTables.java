package lod.irongoon.services;

import legend.game.types.GameState52c;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;
import lod.irongoon.parse.external.CSVParser;
import lod.irongoon.parse.external.DataParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static legend.game.combat.Monsters.monsterStats_8010ba98;

public class DataTables {
    private static final Logger LOGGER = LogManager.getFormatterLogger(DataTables.class);
    private static final DataTables instance = new DataTables();

    public static DataTables getInstance() {
        return instance;
    }

    private DataTables() {
        this.dataTables = new EnumMap<>(ExternalData.class);
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final Map<ExternalData, DataTable> dataTables;
    private final Set<ExternalData> csvOverrides = EnumSet.noneOf(ExternalData.class);
    private final DataParser dataParser = CSVParser.getInstance();

    public void initialize(final GameState52c gameState) {
        dataTables.clear();
        csvOverrides.clear();

        final Map<ExternalData, List<String[]>> severedChainsTables = SeveredChainsDataTables.load(gameState);
        severedChainsTables.forEach(this::addDataTable);

        for (final ExternalData data : ExternalData.values()) {
            final Path overridePath = Path.of(config.externalDataLoadPath + data.getValue() + config.externalDataLoadExtension);

            if (config.csvDataOverrides && Files.isRegularFile(overridePath)) {
                final List<String[]> rows;
                try {
                    rows = dataParser.load(overridePath.toString());
                } catch (final RuntimeException exception) {
                    throw new IllegalStateException("Failed to load CSV override for " + data + " from " + overridePath, exception);
                }

                validate(data, rows, "CSV override " + overridePath);
                addDataTable(data, rows);
                csvOverrides.add(data);
                LOGGER.info("Loaded %s from CSV override %s because csvDataOverrides=true and the file exists", data, overridePath);
                continue;
            }

            if (data == ExternalData.ADDITION_UNLOCK_LEVELS) {
                final String reason = config.csvDataOverrides
                    ? "csvDataOverrides=true but no override file was found at " + overridePath
                    : "csvDataOverrides=false";
                LOGGER.info("Using Severed Chains character-template unlock criteria for %s because %s", data, reason);
                continue;
            }

            final DataTable table = dataTables.get(data);
            if (table == null) {
                throw new IllegalStateException("Required data " + data + " was not available from Severed Chains and no CSV override was loaded from " + overridePath);
            }

            validate(data, table.data, "Severed Chains");
            final String reason = config.csvDataOverrides
                ? "csvDataOverrides=true but no override file was found at " + overridePath
                : "csvDataOverrides=false";
            LOGGER.info("Loaded %s from Severed Chains because %s", data, reason);
        }
    }

    private void addDataTable(final ExternalData name, final List<String[]> list) {
        dataTables.put(name, new DataTable(list));
    }

    private void validate(final ExternalData data, final List<String[]> rows, final String source) {
        final int minimumColumns = switch (data) {
            case CHARACTER_STATS -> 9;
            case DRAGOON_STATS -> 8;
            case MONSTER_STATS -> 14;
            case ADDITION_UNLOCK_LEVELS -> 3;
        };
        final int minimumRows = switch (data) {
            case CHARACTER_STATS -> 1 + 9 * 61;
            case DRAGOON_STATS -> 1 + 9 * 6;
            case MONSTER_STATS -> 1 + monsterStats_8010ba98.length;
            case ADDITION_UNLOCK_LEVELS -> 2;
        };

        if (rows == null || rows.size() < minimumRows) {
            final int actualRows = rows == null ? 0 : rows.size();
            throw new IllegalStateException(source + " provided " + actualRows + " rows for " + data + "; at least " + minimumRows + " are required");
        }

        for (int index = 0; index < rows.size(); index++) {
            if (rows.get(index) == null || rows.get(index).length < minimumColumns) {
                throw new IllegalStateException(source + " provided fewer than " + minimumColumns + " columns for " + data + " at row " + (index + 1));
            }
        }

        for (int row = 1; row < rows.size(); row++) {
            final int numericColumns = data == ExternalData.ADDITION_UNLOCK_LEVELS ? 1 : minimumColumns;
            for (int column = 0; column < numericColumns; column++) {
                requireInteger(data, rows.get(row)[column], row, column, source);
            }

            if (data == ExternalData.ADDITION_UNLOCK_LEVELS) {
                requireInteger(data, rows.get(row)[2], row, 2, source);
            }
        }
    }

    private void requireInteger(final ExternalData data, final String value, final int row, final int column, final String source) {
        try {
            Integer.parseInt(value);
        } catch (final NumberFormatException exception) {
            throw new IllegalStateException(source + " provided a non-integer value for " + data + " at row " + (row + 1) + ", column " + (column + 1), exception);
        }
    }

    public DataTable getDataTable(final ExternalData name) {
        final DataTable table = dataTables.get(name);
        if (table == null) {
            throw new IllegalStateException("Data table " + name + " is unavailable; initialize data sources before using it");
        }

        return new DataTable(table.data);
    }

    public boolean hasCsvOverride(final ExternalData data) {
        return csvOverrides.contains(data);
    }
}
