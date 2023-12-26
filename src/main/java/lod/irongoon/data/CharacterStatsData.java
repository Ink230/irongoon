package lod.irongoon.data;

public enum CharacterStatsData implements Data<Integer>{
    SPEED(0),
    ATTACK(1),
    MAGICATTACK(2),
    DEFENSE(3),
    MAGICDEFENSE(4),
    HP(5),
    TOTALSTATS(7),
    TOTALSTATSNOSPEED(8);

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
