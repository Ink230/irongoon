package lod.irongoon.data;

public enum DragoonSpellStats implements Data<Integer> {
    STOCK(0),
    SHUFFLE(1),
    RANDOMIZE_BOUNDS(2),
    RANDOMIZE_RANDOM(3);

    private final int value;

    DragoonSpellStats(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
