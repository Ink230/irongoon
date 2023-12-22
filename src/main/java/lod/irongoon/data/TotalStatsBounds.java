package lod.irongoon.data;

public enum TotalStatsBounds implements Data<Integer> {
    STOCK(0),
    RANDOM_MODIFIER(1),
    RANDOM_MODIFIER_CUSTOM_UPPER_BOUND(2);

    private final int totalStatsBounds;

    TotalStatsBounds(int totalStatsBounds) {
        this.totalStatsBounds = totalStatsBounds;
    }

    public Integer getValue() {
        return totalStatsBounds;
    }
}
