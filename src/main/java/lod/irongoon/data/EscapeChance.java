package lod.irongoon.data;

public enum EscapeChance implements Data<Integer> {
    STOCK(0),
    RANDOMIZE_BOUNDS(1),
    RANDOMIZE_BOUNDS_FIXED_ENCOUNTER(2),
    RANDOMIZE_BOUNDS_FIXED_SUBMAP(3),
    NO_ESCAPE(4),
    COWARD(5);

    private final int value;

    EscapeChance(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
