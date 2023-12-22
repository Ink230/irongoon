package lod.irongoon.config;

import lod.irongoon.data.TotalStatsPerLevel;

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
    public final TotalStatsPerLevel totalStatsPerLevel = TotalStatsPerLevel.MAINTAIN_STOCK;
}
