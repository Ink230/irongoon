package lod.irongoon.data;

public enum AdditionElements implements Data<Integer> {
    STOCK(0),
    RANDOMIZE(1);

    private final int value;

    AdditionElements(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
