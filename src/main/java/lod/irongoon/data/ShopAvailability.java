package lod.irongoon.data;

public enum ShopAvailability implements Data<Integer> {
    STOCK(0),
    RANDOM(1),
    NO_SHOPS(2),
    NO_ITEMS(3),
    NO_EQUIPMENT(4);

    private final int value;

    ShopAvailability(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
