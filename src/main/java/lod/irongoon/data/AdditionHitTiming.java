package lod.irongoon.data;

public enum AdditionHitTiming implements Data<Integer> {
    STOCK(0),
    RANDOMIZE_BOUNDS(1),
    OVERRIDES(2),
    RANDOMIZE_WITH_OVERRIDES(3);

    private final int value;

    AdditionHitTiming(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
