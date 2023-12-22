package lod.irongoon.entitymanager;

import lod.irongoon.data.ExternalData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTables {
    private static final DataTables instance = new DataTables();

    public static DataTables getInstance() {
        return instance;
    }

    private DataTables() {
        this.dataTables = new HashMap<>();
    }

    private final Map<String, DataTable> dataTables;

    private void addDataTable(ExternalData name, DataTable dataTable) {
        dataTables.put(String.valueOf(name), dataTable);
    }

    public void addDataTable(ExternalData name, List<String[]> list) {
        addDataTable(name, new DataTable(list));
    }

    public DataTable getDataTable(ExternalData name) {
        return dataTables.get(name);
    }
}
