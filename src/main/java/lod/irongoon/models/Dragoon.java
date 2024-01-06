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
                    csvStatsPerLevel.maxMp(),
                    csvStatsPerLevel.spellLearned(),
                    csvStatsPerLevel.attack(),
                    csvStatsPerLevel.magicAttack(),
                    csvStatsPerLevel.defense(),
                    csvStatsPerLevel.magicDefense(),
                    csvStatsPerLevel.totalStatPoints()
            );
        }
    }

    public record CsvStatsPerLevel(
            @CsvBindByName(column = "Max Mp", required = true)
            int maxMp,
            @CsvBindByName(column = "Spell Learned", required = true)
            int spellLearned,
            @CsvBindByName(column = "UK1", required = true)
            int unknown,
            @CsvBindByName(column = "AT", required = true)
            int attack,
            @CsvBindByName(column = "MAT", required = true)
            int magicAttack,
            @CsvBindByName(column = "DF", required = true)
            int defense,
            @CsvBindByName(column = "MDF", required = true)
            int magicDefense,
            @CsvBindByName(column = "Total Stats", required = true)
            int totalStatPoints,
            @CsvBindByName(column = "Name", required = true)
            String name
    ) {}
}
