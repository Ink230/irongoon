package lod.irongoon.data;

public enum ElementsMonsters {
    MAINTAIN_STOCK(0),
    RANDOMIZE(1),
    RANDOMIZE_RANDOM(2),
    RANDOMIZE_AND_TYPINGS(3),
    RANDOMIZE_RANDOM_AND_TYPINGS(4);

    private final int elementsMonsters;

    ElementsMonsters(int elementsMonsters) {
        this.elementsMonsters = elementsMonsters;
    }

    public Integer getValue() {
        return elementsMonsters;
    }
}
