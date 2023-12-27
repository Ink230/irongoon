package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.DragoonStatsParser;

import java.util.Arrays;
import java.util.Random;

public class DragoonStatsRandomizer {
    private static final DragoonStatsRandomizer instance = new DragoonStatsRandomizer();
    public static DragoonStatsRandomizer getInstance() { return instance; }

    private DragoonStatsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final DragoonStatsParser parser = DragoonStatsParser.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public DivineFruit randomizeMaintainStock(int dragoonId, int dLevel) {
        var totalStatsOfCharacterByLevel = parser.getTotalStatsOfDragoonByLevel(dragoonId, dLevel);

        return null;
    }

    public DivineFruit randomizeWithBounds(int dragoonId, int dLevel) {
        var totalStatsOfAllDragoonsByLevel = parser.getTotalStatsOfAllDragoonsByLevel(dLevel);
        var minValue = Arrays.stream(totalStatsOfAllDragoonsByLevel).min().orElseThrow();
        var maxValue = Arrays.stream(totalStatsOfAllDragoonsByLevel).max().orElseThrow();

        Random random = new Random(config.seed);

        int randomNumber = minValue + random.nextInt(maxValue - minValue + 1);
        return null;
    }

    public DivineFruit randomizeAverage(int dragoonId, int dLevel) {
        var totalStatsPerCharacterByLevel = parser.getAverageTotalStatsOfAllDragoonsByLevel(dLevel);

        return null;
    }
}
