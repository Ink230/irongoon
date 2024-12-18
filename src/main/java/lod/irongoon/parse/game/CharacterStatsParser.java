package lod.irongoon.parse.game;

import lod.irongoon.data.CharacterData;
import lod.irongoon.data.CharacterStatsData;
import lod.irongoon.data.ExternalData;

import java.util.Arrays;
import java.util.HashMap;

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

        for (CharacterData value : CharacterData.values()) {
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

    public int[] getHPOfAllCharactersByLevel(int level) {
        var hpOfAllCharactersByLevel = new int[CharacterData.values().length];

        for (CharacterData value : CharacterData.values()) {
            hpOfAllCharactersByLevel[value.getValue()] = getHPOfCharacterByLevel(value.getValue(), level);
        }

        return hpOfAllCharactersByLevel;
    }

    public int getSpeedOfCharacterByLevel(int character, int level) {
        return dataTableAccessor.getValueFromDataTable((character * chunkSize) + level, CharacterStatsData.getValue(CharacterStatsData.SPEED), dataTableKey);
    }

    public int[] getCharactersSpeedStats(int level) {
        var allCharactersSpeedStats = new int[CharacterData.values().length];

        for (CharacterData value : CharacterData.values()) {
            allCharactersSpeedStats[value.getValue()] = getSpeedOfCharacterByLevel(value.getValue(), level);
        }

        return allCharactersSpeedStats;
    }

    public HashMap<String, Integer> getCharacterStatsByLevel(int characterId, int level) {
        var statsArray = dataTableAccessor.getRowFromDataTable((characterId * chunkSize) + level, dataTableKey);

        var characterStatsMap = new HashMap<String, Integer>();

        for (CharacterStatsData stat : CharacterStatsData.values()) {
            int statValue = Integer.parseInt(String.valueOf(statsArray[stat.getValue()]));
            characterStatsMap.put(stat.name(), statValue);
        }

        return characterStatsMap;
    }
}
