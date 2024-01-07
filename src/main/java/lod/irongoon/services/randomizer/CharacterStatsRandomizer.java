package lod.irongoon.services.randomizer;

import lod.irongoon.config.Config;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.CharactersTable;
import lod.irongoon.models.Character;
import lod.irongoon.models.DivineFruit;

import java.util.*;

public class CharacterStatsRandomizer {
    private CharacterStatsRandomizer() {}

    private static DivineFruit growDivineFruit(int[] distribution, int totalStats, DivineFruit previousFruit) {
        int attack = StatsRandomizer.calculateFinalStat(distribution[0], totalStats) + previousFruit.bodyAttack;
        int defense = StatsRandomizer.calculateFinalStat(distribution[1], totalStats) + previousFruit.bodyDefense;
        int magicAttack = StatsRandomizer.calculateFinalStat(distribution[2], totalStats) + previousFruit.bodyMagicAttack;
        int magicDefense = StatsRandomizer.calculateFinalStat(distribution[3], totalStats) + previousFruit.bodyMagicDefense;

        return new DivineFruit(attack, defense, magicAttack, magicDefense);
    }

    public static DivineFruit randomizeMaintainStock(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0));

        for (int subLevel = 1; subLevel <= level; subLevel++) {
            final var characters = Tables.getCharacterTable();
            final var totalStatsLevel = characters.getCharacterStats(characterId, level).getTotalStatPoints();
            final var totalStatsLevelMinusOne = characters.getCharacterStats(characterId, level - 1).getTotalStatPoints();
            final var distribution = StatsRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, Config.bodyTotalStatsDistributionPerLevel, Config.bodyNumberOfStatsAmount, characterId);

            divineTree.add(growDivineFruit(distribution, totalStatsLevel - totalStatsLevelMinusOne, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public static DivineFruit randomizeWithBounds(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0));

        for (int subLevel = 1; subLevel <= level; subLevel++) {
            var totalStatsPerCharacterByLevel = getAllTotalStats(subLevel);
            var totalStatsPerCharacterByLevelMinusOne = getAllTotalStats(subLevel - 1);

            for (int i = 0; i < totalStatsPerCharacterByLevel.length; i++) {
                totalStatsPerCharacterByLevel[i] -= totalStatsPerCharacterByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(totalStatsPerCharacterByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(totalStatsPerCharacterByLevel).max().orElseThrow();

            var totalStats = StatsRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, characterId);

            var distribution = StatsRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, Config.bodyTotalStatsDistributionPerLevel, Config.bodyNumberOfStatsAmount, characterId);

            divineTree.add(growDivineFruit(distribution, totalStats, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public static DivineFruit randomizeAverage(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0));

        for (int subLevel = 1; subLevel <= level; subLevel++) {
            var totalStatsPerCharacterByLevel = getAverageTotalStats(subLevel) - getAverageTotalStats(subLevel - 1);
            var distribution = StatsRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, Config.bodyTotalStatsDistributionPerLevel, Config.bodyNumberOfStatsAmount, characterId);

            divineTree.add(growDivineFruit(distribution, totalStatsPerCharacterByLevel, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    private static int[] getAllTotalStats(int level) {
        return SumStats.Characters(level, Character.StatsPerLevel::getTotalStatPoints);
    }

    private static int getAverageTotalStats(int level) {
        final var totalStats = getAllTotalStats(level);
        int sum = 0;
        for (int i : totalStats) {
            sum += i;
        }
        return sum / totalStats.length;
    }
}
