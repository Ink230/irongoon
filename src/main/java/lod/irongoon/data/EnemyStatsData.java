package lod.irongoon.data;

public enum EnemyStatsData  implements Data<Integer>{
    Id(0),
    HP(1),
    MP(2),
    ATTACK(3),
    MAGIC_ATTACK(4),
    SPEED(5),
    DEFENSE(6),
    MAGIC_DEFENSE(7),
    ATTACK_AVOID(8),
    MAGIC_ATTACK_AVOID(9),
    SPECIAL(10),
    UNKNOWN(11),
    ELEMENT(12),
    ELEMENT_IMMUNITY(13);

    private final int enemyStatsData;

    EnemyStatsData (int enemyStatsData) {
        this.enemyStatsData = enemyStatsData ;
    }
    public Integer getValue() {
        return enemyStatsData;
    }

    public static Integer getValue(EnemyStatsData data) {
        return data.enemyStatsData;
    }
}
