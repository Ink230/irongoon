package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;

public class BattleStageRandomizer {
    private static final BattleStageRandomizer INSTANCE = new BattleStageRandomizer();
    public static BattleStageRandomizer getInstance() { return INSTANCE; }

    private BattleStageRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public int maintainStock(final int battleStageId) {
        return battleStageId;
    }

    public int randomRandom() {
        final var result = statRandomizer.calculateRandomNumberWithBoundsNoSeed(0, this.getBattleUpperBound());
        return this.finalizedBattleStageId(result);
    }

    public int randomFixed(final int uniqueModifier) {
        final var result = statRandomizer.calculateRandomNumberWithBounds(0, this.getBattleUpperBound(), uniqueModifier);
        return this.finalizedBattleStageId(result);
    }

    private int getBattleUpperBound() {
        return this.usingCustomBattleStageList() ? config.battleStageList.size() - 1 : config.battleStageSize;
    }

    private boolean usingCustomBattleStageList() {
        return !config.battleStageList.isEmpty();
    }

    private int finalizedBattleStageId(final int result) {
        return this.usingCustomBattleStageList() ? config.battleStageList.get(result) : result;
    }
}
