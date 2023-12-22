package lod.irongoon.data;

public enum TotalStatsPerLevel implements Data<Integer> {
    RANDOMIZE_BOUNDS_PER_LEVEL(0),
    MAINTAIN_STOCK(1),
    AVERAGE_ALL_CHARACTERS(2);

    private final int totalStatsPerLevel;

    TotalStatsPerLevel(int totalStatsPerLevel) {
        this.totalStatsPerLevel = totalStatsPerLevel;
    }

    public Integer getValue() {
        return totalStatsPerLevel;
    }
}
