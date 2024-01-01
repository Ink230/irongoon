package lod.irongoon.data;

public enum SpeedStatMonsters implements Data<Integer> {
    MAINTAIN_STOCK(0),
    RANDOMIZE_BOUNDS(1),
    RANDOMIZE_RANDOM_BOUNDS(2);

    private final int speedStatPerLevelMonster;

    SpeedStatMonsters(int speedStatPerLevelMonster) {
        this.speedStatPerLevelMonster = speedStatPerLevelMonster;
    }

    public Integer getValue() {
        return speedStatPerLevelMonster;
    }
}
