package lod.irongoon.data;

public enum DragoonSpellEffects implements Data<Integer> {
    STOCK(0),
    SHUFFLE_PACKAGES(1),
    RANDOMIZE_ARCHETYPE(2),
    RANDOMIZE_INDEPENDENT(3),
    RANDOMIZE_RAW(4);

    private final int value;

    DragoonSpellEffects(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
