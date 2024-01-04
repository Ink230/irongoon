package lod.irongoon.data;

public enum TotalStatsMonsters {
    RANDOMIZE_BOUNDS(0),
    MAINTAIN_STOCK(1),
    RANDOMIZE_STOCK_BOUNDS(2);

    private final int totalStatsMonsters;

    TotalStatsMonsters(int totalStatsMonsters) {
        this.totalStatsMonsters = totalStatsMonsters;
    }

    public Integer getValue() {
        return totalStatsMonsters;
    }
}
