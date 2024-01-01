package lod.irongoon.services.randomizer;

import lod.irongoon.models.DivineFruit;

public class MonsterSpeedRandomizer {
    private static final MonsterSpeedRandomizer INSTANCE = new MonsterSpeedRandomizer();
    public static MonsterSpeedRandomizer getInstance() { return INSTANCE; }

    private MonsterSpeedRandomizer() {}

    private DivineFruit createDivineFruit(int speed) {
        return new DivineFruit(speed);
    }

    public DivineFruit randomizeMaintainStock(int monsterId) {
        return null;
    }

    public DivineFruit randomizeWithBounds(int monsterId) {
        return null;
    }

    public DivineFruit randomizeRandomWithBounds(int monsterId) {
        return null;
    }
}
