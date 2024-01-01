package lod.irongoon.data;

public enum HPStatMonsters implements Data<Integer> {
    MAINTAIN_STOCK(0),
    RANDOMIZE_BOUNDS(1);

    private final int hpStatPerLevelMonster;

    HPStatMonsters(int hpStatPerLevelMonster) {
        this.hpStatPerLevelMonster = hpStatPerLevelMonster;
    }

    public Integer getValue() {
        return hpStatPerLevelMonster;
    }
}
