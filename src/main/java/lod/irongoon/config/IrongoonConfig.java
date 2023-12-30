package lod.irongoon.config;

import lod.irongoon.data.*;

public class IrongoonConfig {
    private static final IrongoonConfig INSTANCE = new IrongoonConfig();
    public static IrongoonConfig getInstance() {
        return INSTANCE;
    }

    private IrongoonConfig() {}

    public final String externalDataLoadPath = "./mods/data/US/";
    public final String externalDataLoadExtension = ".csv";
    public final String publicSeed = "7963AC95E13873B6";
    public final long seed = Long.parseUnsignedLong(publicSeed, 16);
    public final int bodyNumberOfStatsAmount = 4;
    public final int dragoonNumberOfStatsAmount = 4;
    public final TotalStatsPerLevel bodyTotalStatsPerLevel = TotalStatsPerLevel.RANDOMIZE_BOUNDS_PER_LEVEL;
    public final TotalStatsPerLevel dragoonTotalStatsPerLevel = TotalStatsPerLevel.RANDOMIZE_BOUNDS_PER_LEVEL;
    public final TotalStatsMonsters monsterTotalStatsPerLevel = TotalStatsMonsters.RANDOMIZE_BOUNDS;
    public final int TotalStatsMonstersUpperPercentBound = 150;
    public final int TotalStatsMonstersLowerPercentBound = 50;
    public final TotalStatsBounds bodyTotalStatsBounds = TotalStatsBounds.STOCK;
    public final TotalStatsBounds dragoonStatsBounds = TotalStatsBounds.STOCK;
    public final TotalStatsDistributionPerLevel bodyTotalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.RANDOM;
    public final TotalStatsDistributionPerLevel dragoonTotalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.RANDOM;
    public final HPStatPerLevel hpStatPerLevel = HPStatPerLevel.RANDOM;
    public final SpeedStatPerLevel speedStatPerLevel = SpeedStatPerLevel.RANDOM;
}
