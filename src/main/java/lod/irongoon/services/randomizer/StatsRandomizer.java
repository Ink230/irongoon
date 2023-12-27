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

    public int[] calculateDistributionOfTotalStats(int level, int characterId) {
        return switch(config.totalStatsDistributionPerLevel) {
            case RANDOM -> calculateDistribution(calculateFixedSeedByLevel(level, characterId), characterId);
            case DABAS_FIXED -> calculateDistribution(config.seed, characterId);
            case DABAS_PER_LEVEL -> shuffleDistributionFixedPerLevel(calculateDistribution(config.seed, characterId), level, characterId);
            case DABAS_FIXED_CUSTOM -> calculateDistribution(config.seed, characterId);
            case DABAS_PER_LEVEL_CUSTOM -> shuffleDistributionRandomEverytime(calculateDistribution(config.seed, characterId));
        };
    }

    private int[] calculateDistribution(long seed, long characterId) {
        Random random = new Random(seed + characterId);

        var distribution = new int[config.bodyStatsAmount];
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

    private int[] shuffleDistributionFixedPerLevel(int[] distribution, int level, int characterId) {
        List<Integer> order = new ArrayList<>();

        for (int j : distribution) {
            order.add(j);
        }

        Collections.shuffle(order, new Random(calculateFixedSeedByLevel(level, characterId)));

        return order.stream().mapToInt(Integer::intValue).toArray();
    }

    public int calculateFinalStat(int distribution, int totalStats) {
        return (int) Math.ceil(((double) distribution / 100) * totalStats);
    }

    private int calculateFixedSeedByLevel(int level, int characterId) {
        Random random = new Random(config.seed + 1 + characterId);

        var value = 0;
        for (int i = 0; i <= level; i++) {
            value = random.nextInt();
        }

        return value;
    }
}
