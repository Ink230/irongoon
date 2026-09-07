package lod.irongoon.data;

public enum DragoonSpellRandomizationPool implements Data<Integer> {
    PER_CHARACTER(0),
    GLOBAL(1);

    private final int value;

    DragoonSpellRandomizationPool(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
