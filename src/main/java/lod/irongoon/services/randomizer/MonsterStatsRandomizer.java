package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.MonstersTable;
import lod.irongoon.models.DivineFruit;

public class MonsterStatsRandomizer {
    private static final MonsterStatsRandomizer INSTANCE = new MonsterStatsRandomizer();
    public static MonsterStatsRandomizer getInstance() { return INSTANCE; }

    private MonsterStatsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final MonstersTable mosters = Tables.getInstance().getMonsterTable();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(int monsterId) {
        var stats = this.mosters.getMonsterStats(monsterId);

        return new DivineFruit(stats.attack(), stats.defense(), stats.magicAttack(), stats.magicDefense());
    }

    public DivineFruit randomizeWithBounds(int monsterId) {
        var divineFruit = createDivineFruit(monsterId);

        var resultAttacks = statRandomizer.calculatePercentModifiedBoundedStat(config.totalStatsMonstersLowerPercentBound, config.totalStatsMonstersUpperPercentBound, divineFruit.bodyAttack + divineFruit.bodyMagicAttack, monsterId);
        var resultAttackSplit = statRandomizer.calculateRandomNumberWithLimit(resultAttacks, monsterId);

        var resultDefenses = statRandomizer.calculatePercentModifiedBoundedStat(config.totalStatsMonstersLowerPercentBound, config.totalStatsMonstersUpperPercentBound, divineFruit.bodyDefense + divineFruit.bodyMagicDefense, monsterId);
        var resultDefenseSplit = statRandomizer.calculateRandomNumberWithLimit(resultDefenses, monsterId);

        divineFruit.bodyAttack = resultAttackSplit;
        divineFruit.bodyDefense = Math.max(config.monsterDefenseFloor, resultDefenseSplit);

        divineFruit.bodyMagicAttack = Math.max(1, resultAttacks - resultAttackSplit);
        divineFruit.bodyMagicDefense = Math.max(config.monsterMagicDefenseFloor, resultDefenses - resultDefenseSplit);

        return new DivineFruit(divineFruit);
    }

    public DivineFruit randomizeMaintainStock(int monsterId) {
        return createDivineFruit(monsterId);
    }

    public DivineFruit randomizeStockWithPercentBounds(int monsterId) {
        var divineFruit = createDivineFruit(monsterId);

        divineFruit.bodyAttack = statRandomizer.calculatePercentModifiedBoundedStat(config.totalStatsMonstersLowerPercentBound, config.totalStatsMonstersUpperPercentBound, divineFruit.bodyAttack, monsterId);
        divineFruit.bodyDefense = Math.max(config.monsterDefenseFloor, statRandomizer.calculatePercentModifiedBoundedStat(config.totalStatsMonstersLowerPercentBound, config.totalStatsMonstersUpperPercentBound, divineFruit.bodyDefense, monsterId));
        divineFruit.bodyMagicAttack = statRandomizer.calculatePercentModifiedBoundedStat(config.totalStatsMonstersLowerPercentBound, config.totalStatsMonstersUpperPercentBound, divineFruit.bodyMagicAttack, monsterId);
        divineFruit.bodyMagicDefense = Math.max(config.monsterMagicDefenseFloor, statRandomizer.calculatePercentModifiedBoundedStat(config.totalStatsMonstersLowerPercentBound, config.totalStatsMonstersUpperPercentBound, divineFruit.bodyMagicDefense, monsterId));

        return new DivineFruit(divineFruit);
    }

    public void varianceStats(DivineFruit monsterStats, DivineFruit monsterHP, DivineFruit monsterSpeed) {
        monsterStats.bodyAttack = statRandomizer.calculateVarianceOfStat(monsterStats.bodyAttack);
        monsterStats.bodyDefense = statRandomizer.calculateVarianceOfStat(monsterStats.bodyDefense);
        monsterStats.bodyMagicAttack = statRandomizer.calculateVarianceOfStat(monsterStats.bodyMagicAttack);
        monsterStats.bodyMagicDefense = statRandomizer.calculateVarianceOfStat(monsterStats.bodyMagicDefense);

        monsterHP.maxHP = statRandomizer.calculateVarianceOfStat(monsterHP.maxHP);
        monsterSpeed.bodySpeed = statRandomizer.calculateVarianceOfStat(monsterSpeed.bodySpeed);
    }
}
