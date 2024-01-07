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

    public static class StatsPerLevel {
        @CsvBindByName(column = "Speed", required = true)
        private int speed;
        @CsvBindByName(column = "AT", required = true)
        private int attack;
        @CsvBindByName(column = "MAT", required = true)
        private int magicAttack;
        @CsvBindByName(column = "DF", required = true)
        private int defense;
        @CsvBindByName(column = "MDF", required = true)
        private int magicDefense;
        @CsvBindByName(column = "HP", required = true)
        private int maxHp;
        @CsvBindByName(column = "Total Stat Points", required = true)
        private int totalStatPoints;
        @CsvBindByName(column = "Total Stat Points No Speed", required = true)
        private int totalStatPointsNoSpeed;
        @CsvBindByName(column = "Name", required = true)
        private String name;

        public StatsPerLevel() {
        }

        public int getSpeed() {
            return speed;
        }

        public int getAttack() {
            return attack;
        }

        public int getMagicAttack() {
            return magicAttack;
        }

        public int getDefense() {
            return defense;
        }

        public int getMagicDefense() {
            return magicDefense;
        }

        public int getMaxHp() {
            return maxHp;
        }

        public int getTotalStatPoints() {
            return totalStatPoints;
        }

        public int getTotalStatPointsNoSpeed() {
            return totalStatPointsNoSpeed;
        }

        public String getName() {
            return name;
        }
    }
    
    public enum Name {
        DART("Dart"),
        LAVITZ("Lavitz"),
        SHANA("Shana"),
        ROSE("Rose"),
        HASCHEL("Haschel"),
        ALBERT("Albert"),
        MERU("Meru"),
        KONGOL("Kongol"),
        MIRANDA("Miranda");;
        public final String name;
        Name(String name) {
            this.name = name;
        }
    }
}
