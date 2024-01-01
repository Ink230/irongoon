package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.EnemyStatsData;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.MonsterStatsParser;

import java.util.*;

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
        return null;
    }
}
