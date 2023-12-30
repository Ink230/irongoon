package lod.irongoon.data;

public enum EnemyStatsData  implements Data<Integer>{
    Id(0),
    HP(1),
    MP(2),
    ATTACK(3),
    MAGIC_ATTACK(4),
    SPEED(5),
    DEFENSE(6),
    MAGIC_DEFENSE(7);

    private final int enemyStatsData;

    EnemyStatsData (int enemyStatsData) {
        this.enemyStatsData = enemyStatsData ;
    }
    public Integer getValue() {
        return enemyStatsData;
    }

    public static Integer getValue(EnemyStatsData  data) {
        return data.enemyStatsData;
    }
}
