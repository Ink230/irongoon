package lod.irongoon.services.randomizer;


import lod.irongoon.config.Config;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.MonstersTable;
import lod.irongoon.models.DivineFruit;

public class MonsterHPRandomizer {
    private MonsterHPRandomizer() {}

    private static final MonstersTable monters = Tables.getMonsterTable();

    private static DivineFruit createDivineFruit(int hp) {
        return new DivineFruit(hp, 0);
    }

    public static DivineFruit randomizeMaintainStock(int monsterId) {
        var hp = monters.getMonsterStats(monsterId).getHp();
        return createDivineFruit(hp);
    }

    public static DivineFruit randomizeStockWithBounds(int monsterId) {
        var monsterHp = monters.getMonsterStats(monsterId).getHp();

        var hp = StatsRandomizer.calculatePercentModifiedBoundedStat(Config.hpStatMonstersLowerPercentBound, Config.hpStatMonstersUpperPercentBound, monsterHp, 959 + monsterId);

        return createDivineFruit(hp);
    }
}
