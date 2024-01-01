package lod.irongoon.data;

public enum SpeedStatPerLevel implements Data<Integer> {
    MAINTAIN_STOCK(0),
    RANDOMIZE_BOUNDS(1),
    RANDOMIZE_RANDOM_BOUNDS(2);

    private final int speedStatPerLevel;

    SpeedStatPerLevel(int speedStatPerLevel) {
        this.speedStatPerLevel = speedStatPerLevel;
    }

    public Integer getValue() {
        return speedStatPerLevel;
    }
}
