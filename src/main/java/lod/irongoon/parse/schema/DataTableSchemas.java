package lod.irongoon.parse.schema;

import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DataTableSchemas {
    private static final Map<ExternalData, DataTableSchema> SCHEMAS = new EnumMap<>(ExternalData.class);

    static {
        SCHEMAS.put(ExternalData.ADDITION_STATS, new TabularSchema(
            ExternalData.ADDITION_STATS,
            new String[] {"Master Addition Flag", "Blue Square Time", "Next Action Time", "Grey Hit Time", "Damage", "SP", "ID", "Final Hit Flag", "Pan Distance", "Lock on Camera Distance", "Lock On Camera Distance 2", "Monster Distance", "Vertical Distance", "Unknown", "Fail Animation", "Start Time", "Name"},
            345,
            range(0, 16),
            -1,
            -1
        ));
        SCHEMAS.put(ExternalData.CHARACTER_STATS, new TabularSchema(
            ExternalData.CHARACTER_STATS,
            new String[] {"Speed", "AT", "MAT", "DF", "MDF", "HP", "Unused Addition Unlock Level", "Total Stat Points", "Total Stat Points No Speed", "Name"},
            550,
            range(0, 9),
            -1,
            9
        ));
        SCHEMAS.put(ExternalData.DRAGOON_STATS, new TabularSchema(
            ExternalData.DRAGOON_STATS,
            new String[] {"Max MP", "Spell Learned", "UK1", "AT", "MAT", "DF", "MDF", "Total Stats", "Name"},
            55,
            range(0, 8),
            -1,
            8
        ));
        SCHEMAS.put(ExternalData.MONSTER_STATS, new TabularSchema(
            ExternalData.MONSTER_STATS,
            new String[] {"ID", "HP", "MP", "AT", "MAT", "SPD", "DF", "MDF", "A-AV", "M-AV", "SPECIAL", "UK", "Element", "Element Null", "Status Resist", "Target Arrow X", "Target Arrow Y", "Target Arrow Z", "UK2", "UK3", "UK4", "UK5", "UK6", "UK7", "UK8", "TotalStats", "Name"},
            401,
            range(0, 26),
            0,
            26
        ));
        SCHEMAS.put(ExternalData.ADDITION_UNLOCK_LEVELS, new TabularSchema(
            ExternalData.ADDITION_UNLOCK_LEVELS,
            new String[] {"ID", "Name", "Unlock Level"},
            44,
            new int[] {0, 2},
            0,
            -1
        ));
    }

    private DataTableSchemas() { }

    public static DataTableSchema get(final ExternalData data) {
        final DataTableSchema schema = SCHEMAS.get(data);
        if (schema == null) {
            throw new IllegalStateException("No data-table schema is registered for " + data);
        }

        return schema;
    }

    private static int[] range(final int startInclusive, final int endExclusive) {
        final int[] values = new int[endExclusive - startInclusive];
        for (int index = 0; index < values.length; index++) {
            values[index] = startInclusive + index;
        }
        return values;
    }

    private static final class TabularSchema implements DataTableSchema {
        private final ExternalData data;
        private final String[] header;
        private final int minimumRows;
        private final int[] numericColumns;
        private final int uniqueIdColumn;
        private final int requiredTextColumn;

        private TabularSchema(
            final ExternalData data,
            final String[] header,
            final int minimumRows,
            final int[] numericColumns,
            final int uniqueIdColumn,
            final int requiredTextColumn
        ) {
            this.data = data;
            this.header = header;
            this.minimumRows = minimumRows;
            this.numericColumns = numericColumns;
            this.uniqueIdColumn = uniqueIdColumn;
            this.requiredTextColumn = requiredTextColumn;
        }

        @Override
        public void validateTable(final DataTable table, final String sourceDescription) {
            if (table == null || table.data.size() < this.minimumRows) {
                final int actualRows = table == null ? 0 : table.data.size();
                throw this.error(sourceDescription + " has " + actualRows + " rows; at least " + this.minimumRows + " are required");
            }

            final String[] actualHeader = table.data.getFirst();
            if (actualHeader.length != this.header.length) {
                throw this.error(sourceDescription + " header has " + actualHeader.length + " columns; expected " + this.header.length);
            }

            for (int column = 0; column < this.header.length; column++) {
                if (!this.header[column].equals(actualHeader[column])) {
                    throw this.error(sourceDescription + " header column " + (column + 1) + " is '" + actualHeader[column] + "'; expected '" + this.header[column] + "'");
                }
            }

            final Set<String> ids = new HashSet<>();
            for (int row = 1; row < table.data.size(); row++) {
                final String[] values = table.data.get(row);
                this.validateDataRow(values, row - 1);

                if (this.uniqueIdColumn >= 0 && !ids.add(values[this.uniqueIdColumn])) {
                    throw this.error(sourceDescription + " contains duplicate ID '" + values[this.uniqueIdColumn] + "' at data row " + (row - 1));
                }

                if (this.data == ExternalData.MONSTER_STATS && Integer.parseInt(values[0]) != row - 1) {
                    throw this.error(sourceDescription + " data row " + (row - 1) + " has ID " + values[0] + "; monster IDs must match their row positions");
                }
            }
        }

        @Override
        public void validateDataRow(final String[] row, final int dataRowIndex) {
            if (row == null || row.length != this.header.length) {
                final int columns = row == null ? 0 : row.length;
                throw this.error("data row " + dataRowIndex + " has " + columns + " columns; expected " + this.header.length);
            }

            for (final int column : this.numericColumns) {
                try {
                    Integer.parseInt(row[column]);
                } catch (final NumberFormatException exception) {
                    throw new IllegalStateException(this.data + " data row " + dataRowIndex + ", column " + (column + 1) + " must be an integer but was '" + row[column] + "'", exception);
                }
            }

            if (this.requiredTextColumn >= 0 && row[this.requiredTextColumn].isBlank()) {
                throw this.error("data row " + dataRowIndex + " requires a value in column " + (this.requiredTextColumn + 1));
            }
        }

        private IllegalStateException error(final String detail) {
            return new IllegalStateException(this.data + " validation failed: " + detail);
        }
    }
}
