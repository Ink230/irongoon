package lod.irongoon.services.randomizer;


import lod.irongoon.config.Config;
import lod.irongoon.data.Tables;
import lod.irongoon.models.DivineFruit;

public class MonsterStatsRandomizer {
    private MonsterStatsRandomizer() {}

    private static DivineFruit createDivineFruit(int monsterId) {
        var stats = Tables.getMonsterTable().getMonsterStats(monsterId);

        return new DivineFruit(stats.getAttack(), stats.getDefense(), stats.getMagicAttack(), stats.getMagicDefense());
    }

    public static DivineFruit randomizeWithBounds(int monsterId) {
        var divineFruit = createDivineFruit(monsterId);

        var resultAttacks = StatsRandomizer.calculatePercentModifiedBoundedStat(Config.totalStatsMonstersLowerPercentBound, Config.totalStatsMonstersUpperPercentBound, divineFruit.bodyAttack + divineFruit.bodyMagicAttack, monsterId);
        var resultAttackSplit = StatsRandomizer.calculateRandomNumberWithLimit(resultAttacks, monsterId);

        var resultDefenses = StatsRandomizer.calculatePercentModifiedBoundedStat(Config.totalStatsMonstersLowerPercentBound, Config.totalStatsMonstersUpperPercentBound, divineFruit.bodyDefense + divineFruit.bodyMagicDefense, monsterId);
        var resultDefenseSplit = StatsRandomizer.calculateRandomNumberWithLimit(resultDefenses, monsterId);

        divineFruit.bodyAttack = resultAttackSplit;
        divineFruit.bodyDefense = Math.max(Config.monsterDefenseFloor, resultDefenseSplit);

        divineFruit.bodyMagicAttack = Math.max(1, resultAttacks - resultAttackSplit);
        divineFruit.bodyMagicDefense = Math.max(Config.monsterMagicDefenseFloor, resultDefenses - resultDefenseSplit);

        return new DivineFruit(divineFruit);
    }

    public static DivineFruit randomizeMaintainStock(int monsterId) {
        return createDivineFruit(monsterId);
    }

    public static DivineFruit randomizeStockWithPercentBounds(int monsterId) {
        var divineFruit = createDivineFruit(monsterId);

        divineFruit.bodyAttack = StatsRandomizer.calculatePercentModifiedBoundedStat(Config.totalStatsMonstersLowerPercentBound, Config.totalStatsMonstersUpperPercentBound, divineFruit.bodyAttack, monsterId);
        divineFruit.bodyDefense = Math.max(Config.monsterDefenseFloor, StatsRandomizer.calculatePercentModifiedBoundedStat(Config.totalStatsMonstersLowerPercentBound, Config.totalStatsMonstersUpperPercentBound, divineFruit.bodyDefense, monsterId));
        divineFruit.bodyMagicAttack = StatsRandomizer.calculatePercentModifiedBoundedStat(Config.totalStatsMonstersLowerPercentBound, Config.totalStatsMonstersUpperPercentBound, divineFruit.bodyMagicAttack, monsterId);
        divineFruit.bodyMagicDefense = Math.max(Config.monsterMagicDefenseFloor, StatsRandomizer.calculatePercentModifiedBoundedStat(Config.totalStatsMonstersLowerPercentBound, Config.totalStatsMonstersUpperPercentBound, divineFruit.bodyMagicDefense, monsterId));

        return new DivineFruit(divineFruit);
    }

    public static void varianceStats(DivineFruit monsterStats, DivineFruit monsterHP, DivineFruit monsterSpeed) {
        monsterStats.bodyAttack = StatsRandomizer.calculateVarianceOfStat(monsterStats.bodyAttack);
        monsterStats.bodyDefense = StatsRandomizer.calculateVarianceOfStat(monsterStats.bodyDefense);
        monsterStats.bodyMagicAttack = StatsRandomizer.calculateVarianceOfStat(monsterStats.bodyMagicAttack);
        monsterStats.bodyMagicDefense = StatsRandomizer.calculateVarianceOfStat(monsterStats.bodyMagicDefense);

        monsterHP.maxHP = StatsRandomizer.calculateVarianceOfStat(monsterHP.maxHP);
        monsterSpeed.bodySpeed = StatsRandomizer.calculateVarianceOfStat(monsterSpeed.bodySpeed);
    }
}
