package lod.irongoon.data;

public enum ShopDuplicates implements Data<Integer> {
    NONE(0),
    ANY(1);

    private final int value;

    ShopDuplicates(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
