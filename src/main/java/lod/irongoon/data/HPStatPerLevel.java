package lod.irongoon.data;

public enum HPStatPerLevel implements Data<Integer> {
    MAINTAIN_STOCK(0),
    RANDOMIZE_BOUNDS_PER_LEVEL(1),
    RANDOMIZE_STOCK_BOUNDS(2),
    RANDOMIZE_BOUNDS_PERCENT_MODIFIED_PER_LEVEL(3);

    private final int hpStatPerLevel;

    HPStatPerLevel(int hpStatPerLevel) {
        this.hpStatPerLevel = hpStatPerLevel;
    }

    public Integer getValue() {
        return hpStatPerLevel;
    }
}
