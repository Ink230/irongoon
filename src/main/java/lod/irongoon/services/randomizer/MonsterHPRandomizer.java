package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.MonstersTable;
import lod.irongoon.models.DivineFruit;

public class MonsterHPRandomizer {
    private static final MonsterHPRandomizer INSTANCE = new MonsterHPRandomizer();
    public static MonsterHPRandomizer getInstance() { return INSTANCE; }

    private MonsterHPRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final MonstersTable monters = Tables.getMonsterTable();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(int hp) {
        return new DivineFruit(hp, 0);
    }

    public DivineFruit randomizeMaintainStock(int monsterId) {
        var hp = this.monters.getMonsterStats(monsterId).hp();
        return createDivineFruit(hp);
    }

    public DivineFruit randomizeStockWithBounds(int monsterId) {
        var monsterHp = this.monters.getMonsterStats(monsterId).hp();

        var hp = statRandomizer.calculatePercentModifiedBoundedStat(config.hpStatMonstersLowerPercentBound, config.hpStatMonstersUpperPercentBound, monsterHp, 959 + monsterId);

        return createDivineFruit(hp);
    }
}
