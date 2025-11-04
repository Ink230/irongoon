package lod.irongoon.data;

public enum AdditionUnlockData {
    ID(0),
    NAME(1),
    UNLOCK_LEVEL(2);

    private final int value;

    AdditionUnlockData(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int getValue(AdditionUnlockData data) {
        return data.getValue();
    }
}