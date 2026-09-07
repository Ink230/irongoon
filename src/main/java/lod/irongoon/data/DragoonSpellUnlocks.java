package lod.irongoon.data;

public enum DragoonSpellUnlocks implements Data<Integer> {
    STOCK(0),
    RANDOMIZE_SEQUENCE(1);

    private final int value;

    DragoonSpellUnlocks(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
