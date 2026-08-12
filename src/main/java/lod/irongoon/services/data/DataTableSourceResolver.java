package lod.irongoon.services.data;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;
import lod.irongoon.parse.schema.DataTableSchemas;

public final class DataTableSourceResolver {
    private final IrongoonConfig config;
    private final CSVDataTableSource csvSource;
    private final DataTableSource severedChainsSource;

    public DataTableSourceResolver() {
        this(IrongoonConfig.getInstance(), new CSVDataTableSource(), new SeveredChainsDataTableSource());
    }

    DataTableSourceResolver(
        final IrongoonConfig config,
        final CSVDataTableSource csvSource,
        final DataTableSource severedChainsSource
    ) {
        this.config = config;
        this.csvSource = csvSource;
        this.severedChainsSource = severedChainsSource;
    }

    public LoadedDataTable load(final ExternalData data) {
        final boolean csvAvailable = this.csvSource.supports(data);

        if (this.config.csvDataOverrides && csvAvailable) {
            return this.loadAndValidate(
                data,
                this.csvSource,
                DataSourceKind.CSV_OVERRIDE,
                "csvDataOverrides=true and matching file exists at " + this.csvSource.path(data),
                false
            );
        }

        if (this.severedChainsSource.supports(data)) {
            return this.loadAndValidate(
                data,
                this.severedChainsSource,
                DataSourceKind.SEVERED_CHAINS,
                this.config.csvDataOverrides
                    ? "csvDataOverrides=true but no matching file exists at " + this.csvSource.path(data)
                    : "csvDataOverrides=false",
                this.supportsLiveUpdates(data)
            );
        }

        if (csvAvailable) {
            return this.loadAndValidate(
                data,
                this.csvSource,
                DataSourceKind.CSV_COMPATIBILITY,
                "current SC has no side-effect-free complete table for " + data,
                this.supportsLiveUpdates(data)
            );
        }

        throw new IllegalStateException(
            "Required data " + data + " is unavailable: expected CSV " + this.csvSource.path(data)
                + "; " + this.severedChainsSource.name() + " does not provide a complete table"
                + "; csvDataOverrides=" + this.config.csvDataOverrides
        );
    }

    private LoadedDataTable loadAndValidate(
        final ExternalData data,
        final DataTableSource source,
        final DataSourceKind sourceKind,
        final String reason,
        final boolean liveUpdatesEnabled
    ) {
        final DataTable table = source.load(data);
        DataTableSchemas.get(data).validateTable(table, source.name());
        return new LoadedDataTable(data, table, sourceKind, reason, liveUpdatesEnabled);
    }

    private boolean supportsLiveUpdates(final ExternalData data) {
        return data == ExternalData.MONSTER_STATS
            || data == ExternalData.CHARACTER_STATS
            || data == ExternalData.DRAGOON_STATS;
    }
}
