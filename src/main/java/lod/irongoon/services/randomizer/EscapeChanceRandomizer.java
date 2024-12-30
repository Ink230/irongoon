package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;

public class EscapeChanceRandomizer {
    private static final EscapeChanceRandomizer INSTANCE = new EscapeChanceRandomizer();
    public static EscapeChanceRandomizer getInstance() { return INSTANCE; }

    private EscapeChanceRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public int maintainStock(final int escapeChance) {
        return escapeChance;
    }

    public int randomizeBounds() {
        return this.statRandomizer.calculateRandomNumberWithBoundsNoSeed(config.escapeChanceLowerBound, config.escapeChanceUpperBound);
    }

    public int randomizeBoundsFixed(final int uniqueModifier) {
        return this.statRandomizer.calculateRandomNumberWithBounds(config.escapeChanceLowerBound, config.escapeChanceUpperBound, uniqueModifier);
    }
}
