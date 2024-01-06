package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.MonstersTable;
import lod.irongoon.models.DivineFruit;

public class MonsterSpeedRandomizer {
    private static final MonsterSpeedRandomizer INSTANCE = new MonsterSpeedRandomizer();
    public static MonsterSpeedRandomizer getInstance() { return INSTANCE; }

    private MonsterSpeedRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final MonstersTable monsters = Tables.getMonsterTable();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(int speed) {
        return new DivineFruit(speed);
    }

    public DivineFruit randomizeMaintainStock(int monsterId) {
        var speed = this.monsters.getMonsterStats(monsterId).speed();
        return createDivineFruit(speed);
    }

    public DivineFruit randomizeWithBounds(int monsterId) {
        var speed = statRandomizer.calculateRandomNumberBetweenBounds(config.speedStatMonstersLowerBound, config.speedStatMonstersUpperBound, 989 + monsterId);

        return createDivineFruit(speed);
    }

    public DivineFruit randomizeRandomWithBounds() {
        var speed = statRandomizer.calculateRandomNumberBetweenBoundsNoSeed(config.speedStatMonstersLowerBound, config.speedStatMonstersUpperBound);

        return createDivineFruit(speed);
    }
}
