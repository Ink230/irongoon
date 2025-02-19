package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.TotalStatsDistributionPerLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StatsRandomizer {
    private static final StatsRandomizer instance = new StatsRandomizer();
    public static StatsRandomizer getInstance() { return instance; }

    private StatsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public int[] calculateDistributionOfTotalStats(int level, int fruitId, TotalStatsDistributionPerLevel distribution, int statAmount, int uniqueModifier) {
        return switch(distribution) {
            case RANDOM -> calculateDistribution(calculateFixedSeedByLevel(level, fruitId, uniqueModifier), fruitId, statAmount, uniqueModifier);
            case DABAS_FIXED -> calculateDistribution(config.seed, fruitId, statAmount, uniqueModifier);
            case DABAS_PER_LEVEL -> shuffleDistributionFixedPerLevel(calculateDistribution(config.seed, fruitId, statAmount, uniqueModifier), level, fruitId, uniqueModifier);
            case DABAS_FIXED_CUSTOM -> calculateDistribution(config.seed, fruitId, statAmount, uniqueModifier);
            case DABAS_PER_LEVEL_CUSTOM -> shuffleDistributionRandomEverytime(calculateDistribution(config.seed, fruitId, statAmount, uniqueModifier));
        };
    }

    private int[] calculateDistribution(long seed, long fruitId, int statAmount, int uniqueModifier) {
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

    private int[] shuffleDistributionRandomEverytime(int[] distribution) {
        List<Integer> order = new ArrayList<>();

        for (int j : distribution) {
            order.add(j);
        }

        Collections.shuffle(order, new Random());

        return order.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] shuffleDistributionFixedPerLevel(int[] distribution, int level, int fruitId, int uniqueModifier) {
        List<Integer> order = new ArrayList<>();

        for (int j : distribution) {
            order.add(j);
        }

        Collections.shuffle(order, new Random(calculateFixedSeedByLevel(level, fruitId, uniqueModifier)));

        return order.stream().mapToInt(Integer::intValue).toArray();
    }

    public int calculateFinalStat(int distribution, int totalStats) {
        return (int) Math.round(((double) distribution / 100) * totalStats);
    }

    private int calculateFixedSeedByLevel(int level, int fruitId, int uniqueModifier) {
        Random random = new Random(config.seed + 1 + fruitId + uniqueModifier);

        var value = 0;
        for (int i = 0; i <= level; i++) {
            value = random.nextInt();
        }

        return value;
    }

    public int calculatePercentModifiedBoundedStat(int percentPointsLower, int percentPointsUpper, int stat, int uniqueModifier) {
        Random random = new Random(config.seed + uniqueModifier + 24);
        percentPointsLower += 20;
        percentPointsUpper += 20;

        var modifier = stat >= 10 ? (random.nextInt(percentPointsUpper - percentPointsLower + 1) + percentPointsLower) / 100.0 : random.nextInt(3);

        return (stat >= 10) ? ((int) Math.max(1, Math.round((stat * modifier)))) : ((int) (stat + (random.nextBoolean() ? 1 : -1) * modifier));
    }

    public int calculateRandomNumberWithLimit(int limit, int uniqueModifier) {
        Random random = new Random(config.seed + uniqueModifier + 27);
        return random.nextInt(1, limit + 1);
    }

    public int calculateRandomNumberWithBounds(int lowerBound, int upperBound, long uniqueModifier) {
        Random random = new Random(config.seed + uniqueModifier + 94);
        return random.nextInt(lowerBound, upperBound + 1);
    }

    public int calculateRandomNumberWithBoundsNoSeed(int lowerBound, int upperBound) {
        Random random = new Random();
        return random.nextInt(lowerBound, upperBound + 1);
    }

    public int calculateVarianceOfStat(int stat) {
        Random random = new Random();
        return calculatePercentModifiedBoundedStat(60, 100, stat, random.nextInt());
    }

    public boolean calculateNextBoolean(int uniqueModifier) {
        Random random = new Random(config.seed + uniqueModifier);
        return random.nextBoolean();
    }
}
