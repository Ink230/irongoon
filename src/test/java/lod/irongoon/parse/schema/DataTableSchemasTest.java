package lod.irongoon.parse.schema;

import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;
import lod.irongoon.parse.external.CSVParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataTableSchemasTest {
    @Test
    void validatesEveryBundledLogicalDataTable() {
        for (final ExternalData data : ExternalData.values()) {
            final Path path = Path.of("mods", "irongoon", "irongoon-data", data.getValue() + ".csv");
            final DataTable table = new DataTable(CSVParser.getInstance().load(path.toString()));

            assertDoesNotThrow(() -> DataTableSchemas.get(data).validateTable(table, path.toString()));
        }
    }

    @Test
    void rejectsInvalidRuntimeRows() {
        final String[] invalid = new String[] {"0", "not-an-integer", "0", "0", "0", "0", "0", "0", "0", "0"};

        assertThrows(
            IllegalStateException.class,
            () -> DataTableSchemas.get(ExternalData.CHARACTER_STATS).validateDataRow(invalid, 0)
        );
    }
}
