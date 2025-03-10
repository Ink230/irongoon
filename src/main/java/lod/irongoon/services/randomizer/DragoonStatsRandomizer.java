package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.DragoonStatsParser;

import java.util.ArrayList;
import java.util.Arrays;

public class DragoonStatsRandomizer {
    private static final DragoonStatsRandomizer INSTANCE = new DragoonStatsRandomizer();
    public static DragoonStatsRandomizer getInstance() { return INSTANCE; }

    private DragoonStatsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final DragoonStatsParser parser = DragoonStatsParser.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit growDivineFruit(int[] distribution, int totalStats, DivineFruit previousFruit) {
        int attack = statRandomizer.calculateFinalStat(distribution[0], totalStats) + previousFruit.dragoonAttack;
        int defense = statRandomizer.calculateFinalStat(distribution[1], totalStats) + previousFruit.dragoonDefense;
        int magicAttack = statRandomizer.calculateFinalStat(distribution[2], totalStats) + previousFruit.dragoonMagicAttack;
        int magicDefense = statRandomizer.calculateFinalStat(distribution[3], totalStats) + previousFruit.dragoonMagicDefense;

        return new DivineFruit(attack, defense, magicAttack, magicDefense, true);
    }

    public DivineFruit randomizeMaintainStock(int dragoonId, int dLevel) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0, true));

        for (int subDLevel = 1; subDLevel <= dLevel; subDLevel++) {
            var totalStatsOfDragoonByLevel = parser.getTotalStatsOfDragoonByLevel(dragoonId, subDLevel) - parser.getTotalStatsOfDragoonByLevel(dragoonId, subDLevel - 1);

            var distribution = statRandomizer.calculateDistributionOfTotalStats(subDLevel, dragoonId, config.dragoonTotalStatsDistributionPerLevel, config.dragoonNumberOfStatsAmount, 999 + dragoonId);

            divineTree.add(growDivineFruit(distribution, totalStatsOfDragoonByLevel, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit randomizeWithBounds(int dragoonId, int dLevel) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0, true));

        for (int subDLevel = 1; subDLevel <= dLevel; subDLevel++) {
            var totalStatsOfAllDragoonsByLevel = parser.getTotalStatsOfAllDragoonsByLevel(subDLevel);
            var totalStatsOfAllDragoonsByLevelMinusOne = parser.getTotalStatsOfAllDragoonsByLevel(subDLevel - 1);

            for (int i = 0; i < totalStatsOfAllDragoonsByLevel.length; i++) {
                totalStatsOfAllDragoonsByLevel[i] -= totalStatsOfAllDragoonsByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(totalStatsOfAllDragoonsByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(totalStatsOfAllDragoonsByLevel).max().orElseThrow();

            var totalStats = statRandomizer.calculateRandomNumberWithBounds(minValue, maxValue, 404 + dragoonId);

            var distribution = statRandomizer.calculateDistributionOfTotalStats(subDLevel, dragoonId, config.dragoonTotalStatsDistributionPerLevel, config.dragoonNumberOfStatsAmount, 999 + dragoonId);

            divineTree.add(growDivineFruit(distribution, totalStats, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit randomizeAverage(int dragoonId, int dLevel) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0, true));

        for (int subDLevel = 1; subDLevel <= dLevel; subDLevel++) {
            var totalStatsPerDragoonByLevel = parser.getAverageTotalStatsOfAllDragoonsByLevel(subDLevel) - parser.getAverageTotalStatsOfAllDragoonsByLevel(subDLevel - 1);

            var distribution = statRandomizer.calculateDistributionOfTotalStats(subDLevel, dragoonId, config.dragoonTotalStatsDistributionPerLevel, config.dragoonNumberOfStatsAmount, 999 + dragoonId);

            divineTree.add(growDivineFruit(distribution, totalStatsPerDragoonByLevel, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit stock(int dragoonId, int level) {
        var results = parser.getDragoonStatsByLevel(dragoonId, level);
        return new DivineFruit(results, true);
    }
}
