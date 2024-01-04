package lod.irongoon.data;

public enum StatsVarianceMonsters {
    STOCK(0),
    RANDOM_PERCENT_BOUNDS(1);

    private final int statsVarianceMonsters;

    StatsVarianceMonsters(int statsVarianceMonsters) {
        this.statsVarianceMonsters = statsVarianceMonsters;
    }

    public Integer getValue() {
        return statsVarianceMonsters;
    }
}
