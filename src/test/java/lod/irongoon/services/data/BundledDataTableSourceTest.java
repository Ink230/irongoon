package lod.irongoon.services.data;

import lod.irongoon.data.ExternalData;
import lod.irongoon.parse.schema.DataTableSchemas;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BundledDataTableSourceTest {
    private final BundledDataTableSource source = BundledDataTableSource.getInstance();

    @Test
    void loadsAndValidatesEveryBundledLogicalDataTable() {
        for (final ExternalData data : ExternalData.values()) {
            assertTrue(this.source.supports(data));
            final var table = this.source.load(data);
            assertDoesNotThrow(() -> DataTableSchemas.get(data).validateTable(table, this.source.name()));
        }
    }

    @Test
    void returnsIndependentTableCopies() {
        final var first = this.source.load(ExternalData.MONSTER_STATS);
        final var second = this.source.load(ExternalData.MONSTER_STATS);

        assertNotSame(first.data, second.data);
        assertNotSame(first.data.get(1), second.data.get(1));
        first.data.get(1)[1] = "999";
        assertEquals("20", second.data.get(1)[1]);
    }
}
