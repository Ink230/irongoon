package lod.irongoon.data;

public enum HPStatPerLevelMonsters implements Data<Integer> {
    MAINTAIN_STOCK(0),
    RANDOMIZE_BOUNDS(1);

    private final int hpStatPerLevelMonster;

    HPStatPerLevelMonsters (int hpStatPerLevelMonster) {
        this.hpStatPerLevelMonster = hpStatPerLevelMonster;
    }

    public Integer getValue() {
        return hpStatPerLevelMonster;
    }
}
