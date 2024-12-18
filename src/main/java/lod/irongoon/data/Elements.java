package lod.irongoon.data;

public enum Elements {
    NO_ELEMENT(0),
    WATER(1),
    EARTH(2),
    DARK(4),
    DIVINE(8),
    THUNDER(16),
    LIGHT(32),
    WIND(64),
    FIRE(128);

    private final int elements;

    Elements(int elements) {
        this.elements = elements;
    }

    public Integer getValue() {
        return elements;
    }

    public static Elements getEnumByIndex(int index) {
        for (Elements element : Elements.values()) {
            if (element.getValue() == index) {
                return element;
            }
        }
        throw new IllegalArgumentException("No index for character found");
    }
}
