package lod.irongoon.services.randomizer;

import legend.game.modding.events.characters.CharacterStatsEvent;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.data.*;

public class Randomizer {
    private static final Randomizer instance = new Randomizer();
    public static Randomizer getInstance() { return instance; }

    private Randomizer() {}

    private final CharacterStatsRandomizer characterStatsRandomizer = CharacterStatsRandomizer.getInstance();
    private final DragoonStatsRandomizer dragoonStatsRandomizer = DragoonStatsRandomizer.getInstance();
    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public DivineFruit doCharacterStats(CharacterStatsEvent character) {
        switch(config.totalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL:
                return characterStatsRandomizer.randomizeWithBounds(character.characterId, character.level);
            case MAINTAIN_STOCK:
                return characterStatsRandomizer.randomizeMaintainStock(character.characterId, character.level);
            case AVERAGE_ALL_CHARACTERS:
                return characterStatsRandomizer.randomizeAverage(character.characterId, character.level);
            default:
                return null;
        }
    }

    public DivineFruit doDragoonStats(CharacterStatsEvent dragoon) {
        switch(config.totalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL:
                return dragoonStatsRandomizer.randomizeWithBounds(dragoon.characterId, dragoon.level);
            case MAINTAIN_STOCK:
                return dragoonStatsRandomizer.randomizeMaintainStock(dragoon.characterId, dragoon.level);
            case AVERAGE_ALL_CHARACTERS:
                return dragoonStatsRandomizer.randomizeAverage(dragoon.characterId, dragoon.level);
            default:
                return null;
        }
    }
}
