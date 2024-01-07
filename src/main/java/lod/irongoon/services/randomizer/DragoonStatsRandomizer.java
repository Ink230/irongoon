package lod.irongoon.services.randomizer;


import lod.irongoon.config.Config;
import lod.irongoon.data.Tables;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.models.Dragoon;

import java.util.ArrayList;
import java.util.Arrays;

public class DragoonStatsRandomizer {
    private DragoonStatsRandomizer() {}

    private static DivineFruit growDivineFruit(int[] distribution, int totalStats, DivineFruit previousFruit) {
        int attack = StatsRandomizer.calculateFinalStat(distribution[0], totalStats) + previousFruit.dragoonAttack;
        int defense = StatsRandomizer.calculateFinalStat(distribution[1], totalStats) + previousFruit.dragoonDefense;
        int magicAttack = StatsRandomizer.calculateFinalStat(distribution[2], totalStats) + previousFruit.dragoonMagicAttack;
        int magicDefense = StatsRandomizer.calculateFinalStat(distribution[3], totalStats) + previousFruit.dragoonMagicDefense;

        return new DivineFruit(attack, defense, magicAttack, magicDefense, true);
    }

    public static DivineFruit randomizeMaintainStock(int dragoonId, int dLevel) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0, true));

        final var dragoons = Tables.getDragoonTable();
        for(int subDLevel = 1; subDLevel <= dLevel; subDLevel++) {
            var totalStatsOfDragoonByLevel = dragoons.getStats(dragoonId, subDLevel).getTotalStatPoints() - dragoons.getStats(dragoonId, subDLevel - 1).getTotalStatPoints();

            var distribution = StatsRandomizer.calculateDistributionOfTotalStats(subDLevel, dragoonId, Config.dragoonTotalStatsDistributionPerLevel, Config.dragoonNumberOfStatsAmount, 999 + dragoonId);

            divineTree.add(growDivineFruit(distribution, totalStatsOfDragoonByLevel, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public static DivineFruit randomizeWithBounds(int dragoonId, int dLevel) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0, true));

        for (int subDLevel = 1; subDLevel <= dLevel; subDLevel++) {
            var totalStatsOfAllDragoonsByLevel = getAllTotalStats(subDLevel);
            var totalStatsOfAllDragoonsByLevelMinusOne = getAllTotalStats(subDLevel - 1);

            for (int i = 0; i < totalStatsOfAllDragoonsByLevel.length; i++) {
                totalStatsOfAllDragoonsByLevel[i] -= totalStatsOfAllDragoonsByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(totalStatsOfAllDragoonsByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(totalStatsOfAllDragoonsByLevel).max().orElseThrow();

            var totalStats = StatsRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, 404 + dragoonId);

            var distribution = StatsRandomizer.calculateDistributionOfTotalStats(subDLevel, dragoonId, Config.dragoonTotalStatsDistributionPerLevel, Config.dragoonNumberOfStatsAmount, 999 + dragoonId);

            divineTree.add(growDivineFruit(distribution, totalStats, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public static DivineFruit randomizeAverage(int dragoonId, int dLevel) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0, true));

        for (int subDLevel = 1; subDLevel <= dLevel; subDLevel++) {
            var totalStatsPerDragoonByLevel = getAverageTotalStats(subDLevel) - getAverageTotalStats(subDLevel - 1);

            var distribution = StatsRandomizer.calculateDistributionOfTotalStats(subDLevel, dragoonId, Config.dragoonTotalStatsDistributionPerLevel, Config.dragoonNumberOfStatsAmount, 999 + dragoonId);

            divineTree.add(growDivineFruit(distribution, totalStatsPerDragoonByLevel, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    private static int[] getAllTotalStats(int level) {
        return StatValueCompiler.Dragoons(level, Dragoon.StatsPerLevel::getTotalStatPoints);
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
