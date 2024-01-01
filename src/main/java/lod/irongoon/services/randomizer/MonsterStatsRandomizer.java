package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.EnemyStatsData;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.MonsterStatsParser;

public class MonsterStatsRandomizer {
    private static final MonsterStatsRandomizer INSTANCE = new MonsterStatsRandomizer();
    public static MonsterStatsRandomizer getInstance() { return INSTANCE; }

    private MonsterStatsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final MonsterStatsParser parser = MonsterStatsParser.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(int monsterId) {
        var monsterValues = parser.getMonsterStatsById(monsterId);

        return new DivineFruit(monsterValues.get(EnemyStatsData.ATTACK.name()), monsterValues.get(EnemyStatsData.DEFENSE.name()), monsterValues.get(EnemyStatsData.MAGIC_ATTACK.name()), monsterValues.get(EnemyStatsData.MAGIC_DEFENSE.name()));
    }

    public DivineFruit randomizeWithBounds(int monsterId) {
        var divineFruit = createDivineFruit(monsterId);

        var resultAttacks = statRandomizer.calculatePercentModifiedBoundedStat(config.TotalStatsMonstersLowerPercentBound, config.TotalStatsMonstersUpperPercentBound, divineFruit.bodyAttack + divineFruit.bodyMagicAttack, monsterId);
        var resultAttackSplit = statRandomizer.calculateRandomNumberWithLimit(resultAttacks, monsterId);

        var resultDefenses = statRandomizer.calculatePercentModifiedBoundedStat(config.TotalStatsMonstersLowerPercentBound, config.TotalStatsMonstersUpperPercentBound, divineFruit.bodyDefense + divineFruit.bodyMagicDefense, monsterId);
        var resultDefenseSplit = statRandomizer.calculateRandomNumberWithLimit(resultDefenses, monsterId);

        divineFruit.bodyAttack = resultAttackSplit;
        divineFruit.bodyDefense = resultDefenseSplit;

        divineFruit.bodyMagicAttack = Math.max(1, resultAttacks - resultAttackSplit);
        divineFruit.bodyMagicDefense = Math.max(1, resultDefenses - resultDefenseSplit);

        return new DivineFruit(divineFruit);
    }

    public DivineFruit randomizeMaintainStock(int monsterId) {
        return createDivineFruit(monsterId);
    }
}