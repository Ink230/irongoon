package lod.irongoon.data;

public enum BattleStage implements Data<Integer> {
    STOCK(0),
    RANDOM(1),
    RANDOM_FIXED_ENCOUNTER(2),
    RANDOM_FIXED_SUBMAP(3);

    private final int value;

    BattleStage(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
