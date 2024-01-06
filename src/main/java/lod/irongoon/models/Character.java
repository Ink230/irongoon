package lod.irongoon.models;

import com.opencsv.bean.CsvBindByName;

public class Character {

    private final String name;
    private final StatsPerLevel[] statsPerLevel;

    public Character(String name, StatsPerLevel[] statsPerLevel) {
        this.name = name;
        this.statsPerLevel = statsPerLevel;
    }

    public String getName() {
        return name;
    }

    public StatsPerLevel getStats(int level) {
        return statsPerLevel[level];
    }

    public record StatsPerLevel(
            int speed,
            int attack,
            int defense,
            int magicAttack,
            int magicDefence,
            int hp,
            int totalStatPoints,
            int totalStatPointsNoSpeed
    ) {
        public StatsPerLevel(CsvStatsPerLevel csvStatsPerLevel) {
            this(
                    csvStatsPerLevel.speed,
                    csvStatsPerLevel.attack,
                    csvStatsPerLevel.defense,
                    csvStatsPerLevel.magicAttack,
                    csvStatsPerLevel.magicDefense,
                    csvStatsPerLevel.maxHp,
                    csvStatsPerLevel.totalStatPoints,
                    csvStatsPerLevel.totalStatPointsNoSpeed
            );
        }
    }

    public static class CsvStatsPerLevel {
        @CsvBindByName(column = "Speed", required = true)
        public int speed;
        @CsvBindByName(column = "AT", required = true)
        public int attack;
        @CsvBindByName(column = "MAT", required = true)
        public int magicAttack;
        @CsvBindByName(column = "DF", required = true)
        public int defense;
        @CsvBindByName(column = "MDF", required = true)
        public int magicDefense;
        @CsvBindByName(column = "HP", required = true)
        public int maxHp;
        @CsvBindByName(column = "Total Stat Points", required = true)
        public int totalStatPoints;
        @CsvBindByName(column = "Total Stat Points No Speed", required = true)
        public int totalStatPointsNoSpeed;
        @CsvBindByName(column = "Name", required = true)
        public String name;

        public CsvStatsPerLevel() {
        }
    }
}
