package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.EnemyStatsData;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.MonsterStatsParser;

public class MonsterSpeedRandomizer {
    private static final MonsterSpeedRandomizer INSTANCE = new MonsterSpeedRandomizer();
    public static MonsterSpeedRandomizer getInstance() { return INSTANCE; }

    private MonsterSpeedRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final MonsterStatsParser parser = MonsterStatsParser.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(int speed) {
        return new DivineFruit(speed);
    }

    public DivineFruit randomizeMaintainStock(int monsterId) {
        var speed = parser.getMonsterStatsById(monsterId).get(EnemyStatsData.SPEED.name());
        return createDivineFruit(speed);
    }

    public DivineFruit randomizeWithBounds(int monsterId) {
        var speed = statRandomizer.calculateRandomNumberWithBounds(config.speedStatMonstersLowerBound, config.speedStatMonstersUpperBound, 989 + monsterId);

        return createDivineFruit(speed);
    }

    public DivineFruit randomizeRandomWithBounds() {
        var speed = statRandomizer.calculateRandomNumberWithBoundsNoSeed(config.speedStatMonstersLowerBound, config.speedStatMonstersUpperBound);

        return createDivineFruit(speed);
    }
}
