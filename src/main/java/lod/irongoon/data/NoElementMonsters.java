package lod.irongoon.data;

public enum NoElementMonsters {
    EXCLUDE(0),
    INCLUDE(1),
    ELEMENTS_ONLY(2),
    IMMUNITIES_ONLY(3);

    private final int noElementMonsters;

    NoElementMonsters(int noElementMonsters) {
        this.noElementMonsters = noElementMonsters;
    }

    public Integer getValue() {
        return noElementMonsters;
    }
}
