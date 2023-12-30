package lod.irongoon.parse.game;

import lod.irongoon.data.ExternalData;
import lod.irongoon.services.DataTables;

public class DataTableParser {
    private static final DataTableParser INSTANCE = new DataTableParser();
    public static DataTableParser getInstance() { return INSTANCE; }

    private DataTableParser() {}

    private final DataTables dataTables = DataTables.getInstance();

    protected int getValueFromDataTable(int index, int column, ExternalData dataTableKey) {
        var table = dataTables.getDataTable(dataTableKey);
        return Integer.parseInt((table.data.get(index + 1)[column]));
    }

    protected int[] getRowFromDataTable(int index, ExternalData dataTableKey) {
        var table = dataTables.getDataTable(dataTableKey);
        var values = table.data.get(index + 1);

        int[] result = new int[values.length];
        for (int i = 0; i < values.length - 1; i++) {
            result[i] = Integer.parseInt(values[i]);
        }

        return result;
    }
}
