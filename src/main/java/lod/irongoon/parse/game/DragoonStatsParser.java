package lod.irongoon.parse.game;

import lod.irongoon.data.*;
import lod.irongoon.services.DataTables;

import java.util.Arrays;
import java.util.HashMap;

public class DragoonStatsParser {
    private static final DragoonStatsParser INSTANCE = new DragoonStatsParser();

    public static DragoonStatsParser getInstance() {
        return INSTANCE;
    }

    private DragoonStatsParser() {}

    private final DataTableParser dataTableAccessor = DataTableParser.getInstance();
    private final int chunkSize = 6;

    public final ExternalData dataTableKey = ExternalData.DRAGOON_STATS;

    public int getTotalStatsOfDragoonByLevel(int character, int level) {
        return dataTableAccessor.getValueFromDataTable((character * chunkSize) + level, DragoonStatsData.getValue(DragoonStatsData.TOTAL_STATS), dataTableKey);
    }

    public int[] getTotalStatsOfAllDragoonsByLevel(int level) {
        var totalStatsOfAllDragoonsByLevel = new int[CharacterData.values().length];

        for (CharacterData value : CharacterData.values()) {
            totalStatsOfAllDragoonsByLevel[value.getValue()] = getTotalStatsOfDragoonByLevel(value.getValue(), level);
        }

        return totalStatsOfAllDragoonsByLevel;
    }

    public int getAverageTotalStatsOfAllDragoonsByLevel(int level) {
        var totalStatsOfAllDragoonsByLevel = getTotalStatsOfAllDragoonsByLevel(level);
        double average = Arrays.stream(totalStatsOfAllDragoonsByLevel).average().orElse(0.0);

        return (int) Math.ceil(average);
    }

    public HashMap<String, Integer> getDragoonStatsByLevel(int dragoonId, int level) {
        var statsArray = dataTableAccessor.getRowFromDataTable((dragoonId * chunkSize) + level, dataTableKey);

        var dragoonStatsMap = new HashMap<String, Integer>();

        for (DragoonStatsData stat : DragoonStatsData.values()) {
            int statValue = Integer.parseInt(String.valueOf(statsArray[stat.getValue()]));
            dragoonStatsMap.put(stat.name(), statValue);
        }

        return dragoonStatsMap;
    }
}
