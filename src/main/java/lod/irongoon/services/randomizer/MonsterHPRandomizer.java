package lod.irongoon.services.randomizer;

import lod.irongoon.models.DivineFruit;

public class MonsterHPRandomizer {
    private static final MonsterHPRandomizer INSTANCE = new MonsterHPRandomizer();
    public static MonsterHPRandomizer getInstance() { return INSTANCE; }

    private MonsterHPRandomizer() {}

    private DivineFruit growDivineFruit(int HP, DivineFruit previousFruit) {
        int hp = HP + previousFruit.maxHP;
        return new DivineFruit(hp, 0);
    }

    public DivineFruit randomizeMaintainStock(int monsterId) {
        return null;
    }

    public DivineFruit randomizeStockWithBounds(int monsterId) {
        return null;
    }
}
