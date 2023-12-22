package lod.irongoon.data;

public enum TotalStatsDistributionPerLevel implements Data<Integer> {
    RANDOM(0),
    DABAS_FIXED(1),
    DABAS_PER_LEVEL(2),
    DABAS_FIXED_CUSTOM(3),
    DABAS_PER_LEVEL_CUSTOM(4);

    private final int totalStatsDistributionPerLevel;

    TotalStatsDistributionPerLevel(int totalStatsDistributionPerLevel) {
        this.totalStatsDistributionPerLevel = totalStatsDistributionPerLevel;
    }

    public Integer getValue() {
        return totalStatsDistributionPerLevel;
    }
}
