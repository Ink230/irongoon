package lod.irongoon.data;

public enum BattleMusic implements Data<Integer> {
    STOCK(0),
    RANDOM(1);

    private final int value;

    BattleMusic(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
