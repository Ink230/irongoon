package lod.irongoon.services.randomizer;

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

    public DivineFruit doCharacterStats(int level) {
        switch(config.totalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL:
                return characterStatsRandomizer.randomizeWithBounds();
            case MAINTAIN_STOCK:
                return characterStatsRandomizer.randomizeMaintainStock();
            case AVERAGE_ALL_CHARACTERS:
                return characterStatsRandomizer.randomizeAverage();
            default:
                return null;
        }
    }

    public DivineFruit doDragoonStats(int dragoonLevel) {
        switch(config.totalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL:
                return dragoonStatsRandomizer.randomizeWithBounds();
            case MAINTAIN_STOCK:
                return dragoonStatsRandomizer.randomizeMaintainStock();
            case AVERAGE_ALL_CHARACTERS:
                return dragoonStatsRandomizer.randomizeAverage();
            default:
                return null;
        }
    }
}
