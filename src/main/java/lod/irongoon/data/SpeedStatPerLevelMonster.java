package lod.irongoon.data;

public enum SpeedStatPerLevelMonster implements Data<Integer> {
    MAINTAIN_STOCK(0),
    RANDOMIZE_BOUNDS(1),
    RANDOMIZE_RANDOM_BOUNDS(2);

    private final int speedStatPerLevelMonster;

    SpeedStatPerLevelMonster(int speedStatPerLevelMonster) {
        this.speedStatPerLevelMonster = speedStatPerLevelMonster;
    }

    public Integer getValue() {
        return speedStatPerLevelMonster;
    }
}
