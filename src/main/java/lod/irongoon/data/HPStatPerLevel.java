package lod.irongoon.data;

public enum HPStatPerLevel implements Data<Integer> {
    RANDOM(0),
    RPG(1),
    RPG_RANDOM(2),
    RPG_RANDOM_RANDOM(3),
    RPG_RANDOM_ULTRA(4),
    STOCK(5);

    private final int hpStatPerLevel;

    HPStatPerLevel(int hpStatPerLevel) {
        this.hpStatPerLevel = hpStatPerLevel;
    }

    public Integer getValue() {
        return hpStatPerLevel;
    }
}
