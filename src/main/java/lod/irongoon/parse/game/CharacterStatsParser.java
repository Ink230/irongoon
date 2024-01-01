package lod.irongoon.parse.game;

import lod.irongoon.data.CharacterData;
import lod.irongoon.data.CharacterStatsData;
import lod.irongoon.data.ExternalData;

import java.util.Arrays;

public class CharacterStatsParser {
    private static final CharacterStatsParser INSTANCE = new CharacterStatsParser();
    public static CharacterStatsParser getInstance() {
        return INSTANCE;
    }

    private CharacterStatsParser() {}

    private final DataTableParser dataTableAccessor = DataTableParser.getInstance();
    private final int chunkSize = 61;

    private final ExternalData dataTableKey = ExternalData.CHARACTER_STATS;

    public int getTotalStatsOfCharacterByLevel(int character, int level) {
        return dataTableAccessor.getValueFromDataTable((character * chunkSize) + level, CharacterStatsData.getValue(CharacterStatsData.TOTAL_STATS_NO_SPEED), dataTableKey);
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

    public int getHPOfCharacterByLevel(int character, int level) {
        return dataTableAccessor.getValueFromDataTable((character * chunkSize) + level, CharacterStatsData.getValue(CharacterStatsData.MAX_HP), dataTableKey);
    }

    public int getSpeedOfCharacterByLevel(int character, int level) {
        return dataTableAccessor.getValueFromDataTable((character * chunkSize) + level, CharacterStatsData.getValue(CharacterStatsData.SPEED), dataTableKey);
    }

    public int[] getCharactersSpeedStats(int character, int level) {
        var allCharactersSpeedStats = new int[CharacterData.values().length];

        for(CharacterData value : CharacterData.values()) {
            allCharactersSpeedStats[value.getValue()] = getSpeedOfCharacterByLevel(character, level);
        }

        return allCharactersSpeedStats;
    }
}
