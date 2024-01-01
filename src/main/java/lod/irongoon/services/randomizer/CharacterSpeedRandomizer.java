package lod.irongoon.services.randomizer;

import lod.irongoon.models.DivineFruit;

public class CharacterSpeedRandomizer {
    private static final CharacterSpeedRandomizer INSTANCE = new CharacterSpeedRandomizer();
    public static CharacterSpeedRandomizer getInstance() { return INSTANCE; }

    private CharacterSpeedRandomizer() {}

    private DivineFruit createDivineFruit(int speed) {
        return new DivineFruit(speed);
    }

    public DivineFruit randomizeMaintainStock(int character, int level) {
        return null;
    }

    public DivineFruit randomizeWithBounds(int character, int level) {
        return null;
    }

    public DivineFruit randomizeStockWithBounds(int character, int level) {
        return null;
    }
}
