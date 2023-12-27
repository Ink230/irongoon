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
    public final int bodyStatsAmount = 4;
    public final int dragoonStatsAmount = 4;
    public final TotalStatsPerLevel totalStatsPerLevel = TotalStatsPerLevel.RANDOMIZE_BOUNDS_PER_LEVEL;
    public final TotalStatsBounds totalStatsBounds = TotalStatsBounds.STOCK;
    public final TotalStatsDistributionPerLevel totalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.RANDOM;
    public final HPStatPerLevel hpStatPerLevel = HPStatPerLevel.RANDOM;
    public final SpeedStatPerLevel speedStatPerLevel = SpeedStatPerLevel.RANDOM;
}
