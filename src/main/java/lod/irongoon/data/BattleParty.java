package lod.irongoon.data;

public enum BattleParty implements Data<Integer> {
    STOCK(0),
    RANDOM_CAMPAIGN(1),
    RANDOM_BATTLE(2);

    private final int value;

    BattleParty(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
