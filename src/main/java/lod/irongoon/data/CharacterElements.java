package lod.irongoon.data;

public enum CharacterElements implements Data<Integer> {
    STOCK(0),
    RANDOM_CAMPAIGN(1),
    RANDOM_BATTLE(2);

    private final int value;

    CharacterElements(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
