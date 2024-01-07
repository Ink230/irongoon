package lod.irongoon.models;

import com.opencsv.bean.CsvBindByName;

public class Dragoon {
    private final String name;
    private final StatsPerLevel[] statsPerLevel;

    public Dragoon(String name, StatsPerLevel[] statsPerLevel) {
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
        @CsvBindByName(column = "Max Mp", required = true)
        private int maxMp;
        @CsvBindByName(column = "Spell Learned", required = true)
        private int spellLearned;
        @CsvBindByName(column = "UK1", required = true)
        private int unknown;
        @CsvBindByName(column = "AT", required = true)
        private int attack;
        @CsvBindByName(column = "MAT", required = true)
        private int magicAttack;
        @CsvBindByName(column = "DF", required = true)
        private int defense;
        @CsvBindByName(column = "MDF", required = true)
        private int magicDefense;
        @CsvBindByName(column = "Total Stats", required = true)
        private int totalStatPoints;
        @CsvBindByName(column = "Name", required = true)
        private String name;

        public StatsPerLevel() {
        }

        public int getMaxMp() {
            return maxMp;
        }

        public int getSpellLearned() {
            return spellLearned;
        }

        public int getUnknown() {
            return unknown;
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

        public int getTotalStatPoints() {
            return totalStatPoints;
        }

        public String getName() {
            return name;
        }
    }
}
