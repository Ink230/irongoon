package lod.irongoon.parse.game;

import lod.irongoon.data.CharacterData;
import lod.irongoon.data.CharacterStatsData;
import lod.irongoon.data.ExternalData;
import lod.irongoon.services.DataTables;

import java.util.Arrays;

public class CharacterStatsParser {
    private static final CharacterStatsParser INSTANCE = new CharacterStatsParser();
    public static CharacterStatsParser getInstance() {
        return INSTANCE;
    }

    private CharacterStatsParser() {}

    private final DataTables dataTables = DataTables.getInstance();
    private final int chunkSize = 61;

    private final ExternalData dataTableKey = ExternalData.CHARACTER_STATS;

    private int getValueFromDataTable(int index, int column) {
        var table = dataTables.getDataTable(dataTableKey);
        return Integer.parseInt((table.data.get(index + 1)[column]));
    }

    public int getTotalStatsOfCharacterByLevel(int character, int level) {
        return getValueFromDataTable((character * chunkSize) + level, CharacterStatsData.getValue(CharacterStatsData.TOTAL_STATS_NO_SPEED));
    }

    public int[] getTotalStatsOfAllCharactersByLevel(int level) {
        var totalStatsOfAllCharactersByLevel = new int[CharacterData.values().length];

        for(CharacterData value : CharacterData.values()) {
            totalStatsOfAllCharactersByLevel[value.getValue()] = getTotalStatsOfCharacterByLevel(value.getValue(), level);
        }

        return totalStatsOfAllCharactersByLevel;
    }

    public int getAverageTotalStatsOfAllCharactersByLevel(int level) {
        var totalStatsOfAllCharactersByLevel = getTotalStatsOfAllCharactersByLevel(level);
        double average = Arrays.stream(totalStatsOfAllCharactersByLevel).average().orElse(0.0);

        return (int) Math.ceil(average);
    }
}
