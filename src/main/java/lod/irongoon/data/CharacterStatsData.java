package lod.irongoon.data;

public enum CharacterStatsData implements Data<Integer>{
    SPEED(0),
    ATTACK(1),
    MAGIC_ATTACK(2),
    DEFENSE(3),
    MAGIC_DEFENSE(4),
    MAX_HP(5),
    TOTAL_STATS(7),
    TOTAL_STAT_SNOSPEED(8);

    private final int characterStatsData;

    CharacterStatsData(int characterStatsData) {
        this.characterStatsData = characterStatsData;
    }
    public Integer getValue() {
        return characterStatsData;
    }

    public static Integer getValue(CharacterStatsData data) {
        return data.characterStatsData;
    }
}
