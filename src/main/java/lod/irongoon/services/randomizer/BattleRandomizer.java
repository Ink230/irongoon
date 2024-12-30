package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;

public class BattleRandomizer {
    private static final BattleRandomizer INSTANCE = new BattleRandomizer();
    public static BattleRandomizer getInstance() { return INSTANCE; }

    private BattleRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public int maintainStock(int battleStageId) {
        return battleStageId;
    }

    public int randomRandom() {
        final var result = statRandomizer.calculateRandomNumberWithBoundsNoSeed(0, this.getBattleUpperBound());
        return this.finalizedBattleStageId(result);
    }

    public int randomFixedEncounter(final int encounterId) {
        final var result = statRandomizer.calculateRandomNumberWithBounds(0, this.getBattleUpperBound(), encounterId);
        return this.finalizedBattleStageId(result);
    }

    public int randomFixedSubmap(final int submapId) {;
        final var result = statRandomizer.calculateRandomNumberWithBounds(0, this.getBattleUpperBound(), submapId);
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
