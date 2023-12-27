package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StatsRandomizer {
    private static final StatsRandomizer instance = new StatsRandomizer();
    public static StatsRandomizer getInstance() { return instance; }

    private StatsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public int[] calculateDistributionOfTotalStats(int level, int fruitId) {
        return switch(config.totalStatsDistributionPerLevel) {
            case RANDOM -> calculateDistribution(calculateFixedSeedByLevel(level, fruitId), fruitId);
            case DABAS_FIXED -> calculateDistribution(config.seed, fruitId);
            case DABAS_PER_LEVEL -> shuffleDistributionFixedPerLevel(calculateDistribution(config.seed, fruitId), level, fruitId);
            case DABAS_FIXED_CUSTOM -> calculateDistribution(config.seed, fruitId);
            case DABAS_PER_LEVEL_CUSTOM -> shuffleDistributionRandomEverytime(calculateDistribution(config.seed, fruitId));
        };
    }

    private int[] calculateDistribution(long seed, long fruitId) {
        Random random = new Random(seed + fruitId);

        var distribution = new int[config.divineFruitStatsAmount];
        var sum = 0;
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = random.nextInt(101);
            sum += distribution[i];
        }

        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = (int) Math.ceil((double) distribution[i] / sum * 100) ;
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

    private int[] shuffleDistributionFixedPerLevel(int[] distribution, int level, int fruitId) {
        List<Integer> order = new ArrayList<>();

        for (int j : distribution) {
            order.add(j);
        }

        Collections.shuffle(order, new Random(calculateFixedSeedByLevel(level, fruitId)));

        return order.stream().mapToInt(Integer::intValue).toArray();
    }

    public int calculateFinalStat(int distribution, int totalStats) {
        return (int) Math.ceil(((double) distribution / 100) * totalStats);
    }

    private int calculateFixedSeedByLevel(int level, int fruitId) {
        Random random = new Random(config.seed + 1 + fruitId);

        var value = 0;
        for (int i = 0; i <= level; i++) {
            value = random.nextInt();
        }

        return value;
    }
}
