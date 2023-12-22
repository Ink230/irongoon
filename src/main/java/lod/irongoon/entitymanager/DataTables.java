package lod.irongoon.entitymanager;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.ExternalData;
import lod.irongoon.parse.DataParser;

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

    public void initialize(ExternalData[] values, DataParser dataParser, IrongoonConfig config) {
        for(ExternalData data : values) {
            var list = dataParser.load(config.ExternalDataLoadPath + data.getValue() + config.ExternalDataLoadExtension);
            addDataTable(data, list);
        }
    }

    private void addDataTable(ExternalData name, List<String[]> list) {
        dataTables.put(String.valueOf(name), new DataTable(list));
    }

    public DataTable getDataTable(ExternalData name) {
        return dataTables.get(name);
    }
}
