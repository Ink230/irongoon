package lod.irongoon.services;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.ExternalData;
import lod.irongoon.services.data.DataSourceKind;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataTablesTest {
    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final DataTables dataTables = DataTables.getInstance();

    @AfterEach
    void restoreDefaultSources() {
        this.config.csvDataOverrides = false;
        this.dataTables.initialize();
    }

    @Test
    void selectsScAndCompatibilitySourcesByDefault() {
        this.config.csvDataOverrides = false;
        this.dataTables.initialize();

        assertEquals(DataSourceKind.SEVERED_CHAINS, this.dataTables.getLoadedSource(ExternalData.MONSTER_STATS).source());
        assertEquals(DataSourceKind.CSV_COMPATIBILITY, this.dataTables.getLoadedSource(ExternalData.CHARACTER_STATS).source());
        assertEquals(DataSourceKind.CSV_COMPATIBILITY, this.dataTables.getLoadedSource(ExternalData.ADDITION_STATS).source());
    }

    @Test
    void selectsFixedCsvOverridesWhenEnabled() {
        this.config.csvDataOverrides = true;
        this.dataTables.initialize();

        for (final ExternalData data : ExternalData.values()) {
            assertEquals(DataSourceKind.CSV_OVERRIDE, this.dataTables.getLoadedSource(data).source());
            assertEquals(false, this.dataTables.allowsLiveUpdates(data));
        }
    }

    @Test
    void updatesRowsDefensivelyAndValidatesBounds() {
        this.config.csvDataOverrides = false;
        this.dataTables.initialize();
        final String original = this.dataTables.getDataTable(ExternalData.MONSTER_STATS).data.get(1)[1];

        this.dataTables.updateDataRow(ExternalData.MONSTER_STATS, 0, row -> {
            row[1] = "999";
            return row;
        });

        assertNotEquals(original, this.dataTables.getDataTable(ExternalData.MONSTER_STATS).data.get(1)[1]);
        assertThrows(
            IllegalStateException.class,
            () -> this.dataTables.updateDataRow(ExternalData.MONSTER_STATS, -1, row -> row)
        );
    }
}
