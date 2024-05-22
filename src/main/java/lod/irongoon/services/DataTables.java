package lod.irongoon.services;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;
import lod.irongoon.parse.external.CSVParser;
import lod.irongoon.parse.external.DataParser;

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

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final Map<String, DataTable> dataTables;
    private final DataParser dataParser = CSVParser.getInstance();

    public void initialize() {
        dataTables.clear();
        for(ExternalData data : ExternalData.values()) {
            var list = dataParser.load(config.externalDataLoadPath + data.getValue() + config.externalDataLoadExtension);
            addDataTable(data, list);
        }
    }

    private void addDataTable(ExternalData name, List<String[]> list) {
        dataTables.put(String.valueOf(name), new DataTable(list));
    }

    public DataTable getDataTable(ExternalData name) {
        return new DataTable(dataTables.get(String.valueOf(name)).data);
    }
}
