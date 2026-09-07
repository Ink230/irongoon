package lod.irongoon.data;

public enum DragoonSpellMpCosts implements Data<Integer> {
    STOCK(0),
    RANDOM_CAMPAIGN_UNLOCK(1),
    RANDOM_CAMPAIGN_CHARACTER(2),
    RANDOM_BATTLE(3),
    RANDOM_TRANSFORM(4);

    private final int value;

    DragoonSpellMpCosts(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
