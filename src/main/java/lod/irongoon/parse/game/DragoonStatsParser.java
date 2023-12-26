package lod.irongoon.parse.game;

import lod.irongoon.data.CharacterData;
import lod.irongoon.data.DragoonStatsData;
import lod.irongoon.data.ExternalData;
import lod.irongoon.services.DataTables;

import java.util.Arrays;

public class DragoonStatsParser {
    private static final DragoonStatsParser INSTANCE = new DragoonStatsParser();

    public static DragoonStatsParser getInstance() {
        return INSTANCE;
    }

    private DragoonStatsParser() {}

    private final DataTables dataTables = DataTables.getInstance();
    private final int chunkSize = 6;

    public final ExternalData dataTableKey = ExternalData.DRAGOON_STATS;

    private int getValueFromDataTable(int index, int column) {
        var table = dataTables.getDataTable(dataTableKey);
        return Integer.parseInt((table.data.get(index + 1)[column]));
    }

    public int getTotalStatsOfDragoonByLevel(int character, int level) {
        return getValueFromDataTable((character * chunkSize) + level, DragoonStatsData.getValue(DragoonStatsData.TOTAL_STATS));
    }

    public int[] getTotalStatsOfAllDragoonsByLevel(int level) {
        var totalStatsOfAllDragoonsByLevel = new int[CharacterData.values().length];

        for(CharacterData value : CharacterData.values()) {
            totalStatsOfAllDragoonsByLevel[value.getValue()] = getTotalStatsOfDragoonByLevel(value.getValue(), level);
        }

        return totalStatsOfAllDragoonsByLevel;
    }

    public int getAverageTotalStatsOfAllDragoonsByLevel(int level) {
        var totalStatsOfAllDragoonsByLevel = getTotalStatsOfAllDragoonsByLevel(level);
        double average = Arrays.stream(totalStatsOfAllDragoonsByLevel).average().orElse(0.0);

        return (int) Math.ceil(average);
    }
}
