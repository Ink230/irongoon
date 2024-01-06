package lod.irongoon.models;

import com.opencsv.bean.CsvBindByName;

public class Monster {
    private final Stats stats;
    private final Reward reward;

    public Monster(Stats stats, Reward reward) {
        this.stats = stats;
        this.reward = reward;
    }

    public Stats getStats() {
        return stats;
    }

    public Reward getReward() {
        return reward;
    }

    public int getID() {
        return stats.id;
    }

    public String getName() {
        return stats.name;
    }

    public record Stats(
            @CsvBindByName(column = "ID", required = true)
            int id,
            @CsvBindByName(column = "HP", required = true)
            int hp,
            @CsvBindByName(column = "MP", required = true)
            int mp,
            @CsvBindByName(column = "AT", required = true)
            int attack,
            @CsvBindByName(column = "MAT", required = true)
            int magicAttack,
            @CsvBindByName(column = "SPD", required = true)
            int speed,
            @CsvBindByName(column = "DF", required = true)
            int defense,
            @CsvBindByName(column = "MDF", required = true)
            int magicDefense,
            @CsvBindByName(column = "A-AV", required = true)
            int attackAvoidance,
            @CsvBindByName(column = "M-AV", required = true)
            int magicAvoidance,
            @CsvBindByName(column = "SPECIAL", required = true)
            int special,
            @CsvBindByName(column = "Element", required = true)
            int element,
            @CsvBindByName(column = "Element Null", required = true)
            int elementNull,
            @CsvBindByName(column = "Status Resist", required = true)
            int statusResist,
            @CsvBindByName(column = "Target Arrow X", required = true)
            int targetArrowX,
            @CsvBindByName(column = "Target Arrow Y", required = true)
            int targetArrowY,
            @CsvBindByName(column = "Target Arrow Z", required = true)
            int targetArrowZ,
            @CsvBindByName(column = "TotalStats", required = true)
            int totalStats,
            @CsvBindByName(column = "Name", required = true)
            String name

            
    ) {}
    public record Reward(
            @CsvBindByName(column = "Exp", required = true)
            int exp,
            @CsvBindByName(column = "Gold", required = true)
            int gold,
            @CsvBindByName(column = "Drop Rate", required = true)
            int dropRate,
            @CsvBindByName(column = "Item", required = true)
            int item,
            @CsvBindByName(column = "Name", required = true)
            String name
    ) {}
}
