package lod.irongoon.data;

public enum ShopQuantityLogic implements Data<Integer> {
    RESPECT_SHOP_CONTENTS(0),
    FILL_ALL(1);

    private final int value;

    ShopQuantityLogic(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
