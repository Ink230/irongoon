package lod.irongoon.data;

public enum ExternalData implements Data<String> {
    CHARACTER_STATS("scdk-character-stats");

    private final String externalData;

    ExternalData(String externalData) {
        this.externalData = externalData;
    }

    public String getValue() {
        return externalData;
    }
}
