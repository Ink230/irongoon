package lod.irongoon.entitymanager;

import lod.irongoon.data.ExternalData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTables {
    private final Map<String, DataTable> dataTables;

    public DataTables() {
        this.dataTables = new HashMap<>();
    }

    public void addDataTable(ExternalData name, DataTable dataTable) {
        dataTables.put(String.valueOf(name), dataTable);
    }

    public void addDataTable(ExternalData name, List<String[]> list) {
        addDataTable(name, mapDataListToTable(name, list));
    }

    public DataTable getDataTable(ExternalData name) {
        return dataTables.get(name);
    }

    public DataTable mapDataListToTable(ExternalData name, List<String[]> list) {
        return new DataTable();
    }
}
