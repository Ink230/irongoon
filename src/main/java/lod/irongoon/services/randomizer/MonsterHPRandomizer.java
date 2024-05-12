package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.EnemyStatsData;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.MonsterStatsParser;

public class MonsterHPRandomizer {
    private static final MonsterHPRandomizer INSTANCE = new MonsterHPRandomizer();
    public static MonsterHPRandomizer getInstance() { return INSTANCE; }

    private MonsterHPRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final MonsterStatsParser parser = MonsterStatsParser.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(int hp) {
        return new DivineFruit(hp, 0);
    }

    public DivineFruit randomizeMaintainStock(int monsterId) {
        var hp = parser.getMonsterStatsById(monsterId).get(EnemyStatsData.HP.name());
        return createDivineFruit(hp);
    }

    public DivineFruit randomizeStockWithBounds(int monsterId) {
        var monsterHp = parser.getMonsterStatsById(monsterId).get(EnemyStatsData.HP.name());

        var hp = statRandomizer.calculatePercentModifiedBoundedStat(config.hpStatMonstersLowerPercentBound, config.hpStatMonstersUpperPercentBound, monsterHp, 959 + monsterId);

        return createDivineFruit(hp);
    }
}