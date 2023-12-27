package lod.irongoon.services.randomizer;

import lod.irongoon.models.DivineFruit;

public class DragoonStatsRandomizer {
    private static final DragoonStatsRandomizer instance = new DragoonStatsRandomizer();
    public static DragoonStatsRandomizer getInstance() { return instance; }

    private DragoonStatsRandomizer() {}

    public DivineFruit randomizeMaintainStock(int dragoonId, int level) {
        return null;
    }

    public DivineFruit randomizeWithBounds(int dragoonId, int level) {
        return null;
    }

    public DivineFruit randomizeAverage(int dragoonId, int level) {
        return null;
    }
}
