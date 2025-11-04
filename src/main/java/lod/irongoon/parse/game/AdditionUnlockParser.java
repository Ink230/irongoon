package lod.irongoon.parse.game;

import lod.irongoon.data.AdditionUnlockData;
import lod.irongoon.data.ExternalData;
import lod.irongoon.services.DataTables;

public class AdditionUnlockParser {
    private static final AdditionUnlockParser INSTANCE = new AdditionUnlockParser();
    public static AdditionUnlockParser getInstance() {
        return INSTANCE;
    }

    private AdditionUnlockParser() {}

    private final DataTableParser dataTableAccessor = DataTableParser.getInstance();
    private final DataTables dataTables = DataTables.getInstance();
    private final ExternalData dataTableKey = ExternalData.ADDITION_UNLOCK_LEVELS;

    public int getAdditionId(int index) {
        return dataTableAccessor.getValueFromDataTable(index, AdditionUnlockData.getValue(AdditionUnlockData.ID), dataTableKey);
    }

    public String getAdditionName(int index) {
        var table = dataTables.getDataTable(dataTableKey);
        return table.data.get(index + 1)[AdditionUnlockData.getValue(AdditionUnlockData.NAME)];
    }

    public int getAdditionUnlockLevel(int index) {
        return dataTableAccessor.getValueFromDataTable(index, AdditionUnlockData.getValue(AdditionUnlockData.UNLOCK_LEVEL), dataTableKey);
    }

    public int getTotalAdditions() {
        var table = dataTables.getDataTable(dataTableKey);
        return table.data.size() - 1;
    }
}