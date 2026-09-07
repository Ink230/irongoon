package lod.irongoon.data;

public enum AdditionStatuses implements Data<Integer> {
    STOCK(0),
    RANDOMIZE(1);

    private final int value;

    AdditionStatuses(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
