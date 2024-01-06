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

    public record StatsPerLevel(
            int maxMp,
            int spellLearned,
            int attack,
            int magicAttack,
            int defense,
            int magicDefense,
            int totalStatPoints
    ) {
        public StatsPerLevel(CsvStatsPerLevel csvStatsPerLevel) {
            this(
                    csvStatsPerLevel.maxMp,
                    csvStatsPerLevel.spellLearned,
                    csvStatsPerLevel.attack,
                    csvStatsPerLevel.magicAttack,
                    csvStatsPerLevel.defense,
                    csvStatsPerLevel.magicDefense,
                    csvStatsPerLevel.totalStatPoints
            );
        }
    }

    public static class CsvStatsPerLevel {
        @CsvBindByName(column = "Max Mp", required = true)
        public int maxMp;
        @CsvBindByName(column = "Spell Learned", required = true)
        public int spellLearned;
        @CsvBindByName(column = "UK1", required = true)
        public int unknown;
        @CsvBindByName(column = "AT", required = true)
        public int attack;
        @CsvBindByName(column = "MAT", required = true)
        public int magicAttack;
        @CsvBindByName(column = "DF", required = true)
        public int defense;
        @CsvBindByName(column = "MDF", required = true)
        public int magicDefense;
        @CsvBindByName(column = "Total Stats", required = true)
        public int totalStatPoints;
        @CsvBindByName(column = "Name", required = true)
        public String name;

        public CsvStatsPerLevel() {
        }
    }
}
