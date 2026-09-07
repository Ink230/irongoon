package lod.irongoon.data;

public enum DragoonSpellElements implements Data<Integer> {
    STOCK(0),
    SHUFFLE(1),
    RANDOMIZE(2);

    private final int value;

    DragoonSpellElements(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
