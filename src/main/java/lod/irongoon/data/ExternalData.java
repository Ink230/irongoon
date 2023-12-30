package lod.irongoon.data;

public enum ExternalData implements Data<String> {
    ADDITION_STATS("scdk-addition-stats"),
    CHARACTER_STATS("scdk-character-stats"),
    DRAGOON_STATS("scdk-dragoon-stats"),
    MONSTER_STATS("scdk-monster-stats");

    private final String externalData;

    ExternalData(String externalData) {
        this.externalData = externalData;
    }

    public String getValue() {
        return externalData;
    }
}
