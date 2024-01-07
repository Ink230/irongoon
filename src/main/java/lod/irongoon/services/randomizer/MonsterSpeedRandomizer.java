package lod.irongoon.services.randomizer;


import lod.irongoon.config.Config;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.MonstersTable;
import lod.irongoon.models.DivineFruit;

public class MonsterSpeedRandomizer {
    private MonsterSpeedRandomizer() {}

    private static DivineFruit createDivineFruit(int speed) {
        return new DivineFruit(speed);
    }

    public static DivineFruit randomizeMaintainStock(int monsterId) {
        var speed = Tables.getMonsterTable().getMonsterStats(monsterId).getSpeed();
        return createDivineFruit(speed);
    }

    public static DivineFruit randomizeWithBounds(int monsterId) {
        var speed = StatsRandomizer.calculateRandomNumberBetweenBounds(Config.speedStatMonstersLowerBound, Config.speedStatMonstersUpperBound, 989 + monsterId);

        return createDivineFruit(speed);
    }

    public static DivineFruit randomizeRandomWithBounds() {
        var speed = StatsRandomizer.calculateRandomNumberBetweenBoundsNoSeed(Config.speedStatMonstersLowerBound, Config.speedStatMonstersUpperBound);

        return createDivineFruit(speed);
    }
}
