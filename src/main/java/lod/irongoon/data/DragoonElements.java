package lod.irongoon.data;

public enum DragoonElements implements Data<Integer> {
    STOCK(0),
    RANDOM_CAMPAIGN(1),
    RANDOM_BATTLE(2),
    RANDOM_TRANSFORM(3);

    private final int value;

    DragoonElements(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
