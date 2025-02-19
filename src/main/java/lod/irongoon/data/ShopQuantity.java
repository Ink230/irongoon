package lod.irongoon.data;

public enum ShopQuantity implements Data<Integer> {
    STOCK(0),
    RANDOMIZE_BOUNDS(1);

    private final int value;

    ShopQuantity(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
