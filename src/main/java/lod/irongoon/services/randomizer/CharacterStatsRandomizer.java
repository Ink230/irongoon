package lod.irongoon.services.randomizer;

import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.CharacterStatsParser;

public class CharacterStatsRandomizer {
    private static final CharacterStatsRandomizer instance = new CharacterStatsRandomizer();
    public static CharacterStatsRandomizer getInstance() { return instance; }

    private CharacterStatsRandomizer() {}

    private final CharacterStatsParser parser = CharacterStatsParser.getInstance();

    public DivineFruit randomizeMaintainStock(int characterId, int level) {
        DivineFruit character = new DivineFruit();
        parser.getTotalStatsOfCharacterByLevel(characterId, level);

        return null;
    }

    public DivineFruit randomizeWithBounds(int characterId, int level) {
        return null;
    }

    public DivineFruit randomizeAverage(int characterId, int level) {
        return null;
    }
}
