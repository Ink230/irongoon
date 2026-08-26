package lod.irongoon.data;

public enum EnableAllDragoons implements Data<Integer> {
    STOCK(0),
    PERMANENTLY(1),
    STORY_CONTROLLED(2);

    private final int value;

    EnableAllDragoons(final int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
