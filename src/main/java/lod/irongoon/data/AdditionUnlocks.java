package lod.irongoon.data;

public enum AdditionUnlocks implements Data<Integer> {
    STOCK(0),
    RANDOMIZE_SEQUENCE(1);

    private final int value;

    AdditionUnlocks(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
