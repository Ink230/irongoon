package lod.irongoon.services.randomizer;

import lod.irongoon.models.DivineFruit;

public class CharacterHPRandomizer {
    private static final CharacterHPRandomizer INSTANCE = new CharacterHPRandomizer();
    public static CharacterHPRandomizer getInstance() { return INSTANCE; }

    private CharacterHPRandomizer() {}

    private DivineFruit growDivineFruit(int HP, DivineFruit previousFruit) {
        int hp = HP + previousFruit.maxHP;
        return new DivineFruit(hp, 0);
    }

    public DivineFruit randomizeMaintainStock(int characterId, int level) {
        return null;
    }

    public DivineFruit randomizeWithBounds(int characterId, int level) {
        return null;
    }

    public DivineFruit randomizeStockWithBounds(int characterId, int level) {
        return null;
    }
}
