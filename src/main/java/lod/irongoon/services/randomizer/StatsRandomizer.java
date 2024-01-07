package lod.irongoon.services.randomizer;


import lod.irongoon.config.Config;
import lod.irongoon.config.modifiers.TotalStatsDistributionPerLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StatsRandomizer {
    private StatsRandomizer() {}

    public static int[] calculateDistributionOfTotalStats(int level, int fruitId, TotalStatsDistributionPerLevel distribution, int statAmount, int uniqueModifier) {
        return switch(distribution) {
            case RANDOM -> calculateDistribution(calculateFixedSeedByLevel(level, fruitId, uniqueModifier), fruitId, statAmount, uniqueModifier);
            case DABAS_FIXED -> calculateDistribution(Config.seed, fruitId, statAmount, uniqueModifier);
            case DABAS_PER_LEVEL -> shuffleDistributionFixedPerLevel(calculateDistribution(Config.seed, fruitId, statAmount, uniqueModifier), level, fruitId, uniqueModifier);
            case DABAS_FIXED_CUSTOM -> calculateDistribution(Config.seed, fruitId, statAmount, uniqueModifier);
            case DABAS_PER_LEVEL_CUSTOM -> shuffleDistributionRandomEverytime(calculateDistribution(Config.seed, fruitId, statAmount, uniqueModifier));
        };
    }

    private static int[] calculateDistribution(long seed, long fruitId, int statAmount, int uniqueModifier) {
        Random random = new Random(seed + fruitId + uniqueModifier);

        var distribution = new int[statAmount];
        var sum = 0;
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = random.nextInt(101);
            sum += distribution[i];
        }

        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = (int) Math.round((double) distribution[i] / sum * 100) ;
        }

        return distribution;
    }

    private static int[] shuffleDistributionRandomEverytime(int[] distribution) {
        List<Integer> order = new ArrayList<>();

        for (int j : distribution) {
            order.add(j);
        }

        Collections.shuffle(order, new Random());

        return order.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int[] shuffleDistributionFixedPerLevel(int[] distribution, int level, int fruitId, int uniqueModifier) {
        List<Integer> order = new ArrayList<>();

        for (int j : distribution) {
            order.add(j);
        }

        Collections.shuffle(order, new Random(calculateFixedSeedByLevel(level, fruitId, uniqueModifier)));

        return order.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int calculateFinalStat(int distribution, int totalStats) {
        return (int) Math.round(((double) distribution / 100) * totalStats);
    }

    private static int calculateFixedSeedByLevel(int level, int fruitId, int uniqueModifier) {
        Random random = new Random(Config.seed + 1 + fruitId + uniqueModifier);

        var value = 0;
        for (int i = 0; i <= level; i++) {
            value = random.nextInt();
        }

        return value;
    }

    public static int calculatePercentModifiedBoundedStat(int percentPointsLower, int percentPointsUpper, int stat, int uniqueModifier) {
        Random random = new Random(Config.seed + uniqueModifier + 24);
        percentPointsLower += 20;
        percentPointsUpper += 20;

        var modifier = stat >= 10 ? (random.nextInt(percentPointsUpper - percentPointsLower + 1) + percentPointsLower) / 100.0 : random.nextInt(4);

        return (stat >= 10) ? ((int) Math.max(1, Math.round((stat * modifier)))) : ((int) (stat + (random.nextBoolean() ? 1 : -1) * modifier));
    }

    public static int calculateRandomNumberWithLimit(int limit, int uniqueModifier) {
        Random random = new Random(Config.seed + uniqueModifier + 24);
        return random.nextInt(1, limit + 1);
    }

    public static int calculateRandomNumberBetweenBounds(int lowerBound, int upperBound, int uniqueModifier) {
        return lowerBound + new Random().nextInt(upperBound - lowerBound + 1);
    }

    public static int calculateRandomNumberBetweenBoundsNoSeed(int lowerBound, int upperBound) {
        return lowerBound + new Random().nextInt(upperBound - lowerBound + 1);
    }

    public static int calculateVarianceOfStat(int stat) {
        return calculatePercentModifiedBoundedStat(60, 100, stat, new Random().nextInt());
    }
}
