package lod.irongoon.data;

public enum ShopContents implements Data<Integer> {
    STOCK(0),
    RANDOMIZE_ITEMS(1),
    RANDOMIZE_EQUIPMENT(2),
    RANDOMIZE_ALL(3),
    RANDOMIZE_ALL_MIXED(4);

    private final int value;

    ShopContents(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
