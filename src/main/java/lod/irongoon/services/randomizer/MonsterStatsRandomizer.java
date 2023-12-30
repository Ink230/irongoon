package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;

public class MonsterStatsRandomizer {
    private static final MonsterStatsRandomizer INSTANCE = new MonsterStatsRandomizer();
    public static MonsterStatsRandomizer getInstance() { return INSTANCE; }

    private MonsterStatsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

}
