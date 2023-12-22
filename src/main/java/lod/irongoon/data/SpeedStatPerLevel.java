package lod.irongoon.data;

public enum SpeedStatPerLevel implements Data<Integer> {
    RANDOM(0),
    RANDOM_CUSTOM(1),
    STOCK(2),
    STOCK_SHUFFLE(3),
    CHAOS_STOCK_SHUFFLE(4),
    CHAOS_RANDOM_SHUFFLE(5);

    private final int speedStatPerLevel;

    SpeedStatPerLevel(int speedStatPerLevel) {
        this.speedStatPerLevel = speedStatPerLevel;
    }

    public Integer getValue() {
        return speedStatPerLevel;
    }
}
