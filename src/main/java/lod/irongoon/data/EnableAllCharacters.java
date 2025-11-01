package lod.irongoon.data;

public enum EnableAllCharacters implements Data<Integer> {
    STOCK(0),
    PERMANENTLY(1),
    STORY_CONTROLLED(2);

    private final int value;

    EnableAllCharacters(final int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
