package lod.irongoon.data;

public enum DragoonStatsData {
    MAX_MP(0),
    SPELL_LEVEL(1),
    UK1(2),
    DRAGOON_ATTACK(3),
    DRAGOON_MAGIC_ATTACK(4),
    DRAGOON_DEFENSE(5),
    DRAGOON_MAGIC_DEFENSE(6),
    TOTAL_STATS(7);

    private final int dragoonStatsData;

    DragoonStatsData(int dragoonStatsData) {
        this.dragoonStatsData = dragoonStatsData;
    }
    public Integer getValue() {
        return dragoonStatsData;
    }

    public static Integer getValue(DragoonStatsData data) {
        return data.dragoonStatsData;
    }
}
