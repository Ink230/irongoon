package lod.irongoon.services;

import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;
import lod.irongoon.parse.schema.DataTableSchemas;
import lod.irongoon.services.data.DataTableSourceResolver;
import lod.irongoon.services.data.LoadedDataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class DataTables {
    private static final Logger LOGGER = LogManager.getFormatterLogger(DataTables.class);
    private static final DataTables instance = new DataTables();

    public static DataTables getInstance() {
        return instance;
    }

    private DataTables() { }

    private final Map<ExternalData, DataTable> dataTables = new EnumMap<>(ExternalData.class);
    private final Map<ExternalData, LoadedDataTable> loadedSources = new EnumMap<>(ExternalData.class);
    private final DataTableSourceResolver sourceResolver = new DataTableSourceResolver();

    public void initialize() {
        this.dataTables.clear();
        this.loadedSources.clear();

        for (final ExternalData data : ExternalData.values()) {
            final LoadedDataTable loaded = this.sourceResolver.load(data);
            this.dataTables.put(data, new DataTable(loaded.table().data));
            this.loadedSources.put(data, loaded);
            LOGGER.info(
                "Loaded %s from %s because %s; live SC updates %s",
                data,
                loaded.source(),
                loaded.reason(),
                loaded.liveUpdatesEnabled() ? "enabled" : "disabled"
            );
        }
    }

    public DataTable getDataTable(final ExternalData data) {
        final DataTable table = this.dataTables.get(data);
        if (table == null) {
            throw new IllegalStateException("Data table " + data + " is unavailable; initialize data sources before using it");
        }

        return new DataTable(table.data);
    }

    public void updateDataRow(
        final ExternalData data,
        final int dataRowIndex,
        final UnaryOperator<String[]> update
    ) {
        final DataTable table = this.dataTables.get(data);
        if (table == null) {
            throw new IllegalStateException("Cannot update " + data + " data row " + dataRowIndex + ": table is not initialized");
        }

        final int tableRowIndex = dataRowIndex + 1;
        if (dataRowIndex < 0 || tableRowIndex >= table.data.size()) {
            throw new IllegalStateException("Cannot update " + data + " data row " + dataRowIndex + ": valid range is 0 to " + (table.data.size() - 2));
        }

        final String[] current = table.data.get(tableRowIndex).clone();
        final String[] replacement = update.apply(current);
        DataTableSchemas.get(data).validateDataRow(replacement, dataRowIndex);
        table.data.set(tableRowIndex, replacement.clone());
        LOGGER.debug("Updated %s data row %d from live Severed Chains data", data, dataRowIndex);
    }

    public LoadedDataTable getLoadedSource(final ExternalData data) {
        final LoadedDataTable loaded = this.loadedSources.get(data);
        if (loaded == null) {
            throw new IllegalStateException("Data source metadata for " + data + " is unavailable; initialize data sources before using it");
        }

        return loaded;
    }

    public boolean allowsLiveUpdates(final ExternalData data) {
        return this.getLoadedSource(data).liveUpdatesEnabled();
    }
}
