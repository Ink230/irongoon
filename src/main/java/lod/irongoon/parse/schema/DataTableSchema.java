package lod.irongoon.parse.schema;

import lod.irongoon.models.DataTable;

public interface DataTableSchema {
    void validateTable(DataTable table, String sourceDescription);

    void validateDataRow(String[] row, int dataRowIndex);
}
