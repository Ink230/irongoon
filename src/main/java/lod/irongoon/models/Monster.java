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
            int id,
            int hp,
            int mp,
            int attack,
            int magicAttack,
            int speed,
            int defense,
            int magicDefense,
            int attackAvoidance,
            int magicAvoidance,
            int special,
            int element,
            int elementNull,
            int statusResist,
            int targetArrowX,
            int targetArrowY,
            int targetArrowZ,
            int totalStats,
            String name
    ) {
        public Stats(CsvStats csvStats) {
            this(
                    csvStats.id,
                    csvStats.hp,
                    csvStats.mp,
                    csvStats.attack,
                    csvStats.magicAttack,
                    csvStats.speed,
                    csvStats.defense,
                    csvStats.magicDefense,
                    csvStats.attackAvoidance,
                    csvStats.magicAvoidance,
                    csvStats.special,
                    csvStats.element,
                    csvStats.elementNull,
                    csvStats.statusResist,
                    csvStats.targetArrowX,
                    csvStats.targetArrowY,
                    csvStats.targetArrowZ,
                    csvStats.totalStats,
                    csvStats.name
            );
        }
    }

    public static class CsvStats {
        @CsvBindByName(column = "ID", required = true)
        public int id;
        @CsvBindByName(column = "HP", required = true)
        public int hp;
        @CsvBindByName(column = "MP", required = true)
        public int mp;
        @CsvBindByName(column = "AT", required = true)
        public int attack;
        @CsvBindByName(column = "MAT", required = true)
        public int magicAttack;
        @CsvBindByName(column = "SPD", required = true)
        public int speed;
        @CsvBindByName(column = "DF", required = true)
        public int defense;
        @CsvBindByName(column = "MDF", required = true)
        public int magicDefense;
        @CsvBindByName(column = "A-AV", required = true)
        public int attackAvoidance;
        @CsvBindByName(column = "M-AV", required = true)
        public int magicAvoidance;
        @CsvBindByName(column = "SPECIAL", required = true)
        public int special;
        @CsvBindByName(column = "Element", required = true)
        public int element;
        @CsvBindByName(column = "Element Null", required = true)
        public int elementNull;
        @CsvBindByName(column = "Status Resist", required = true)
        public int statusResist;
        @CsvBindByName(column = "Target Arrow X", required = true)
        public int targetArrowX;
        @CsvBindByName(column = "Target Arrow Y", required = true)
        public int targetArrowY;
        @CsvBindByName(column = "Target Arrow Z", required = true)
        public int targetArrowZ;
        @CsvBindByName(column = "TotalStats", required = true)
        public int totalStats;
        @CsvBindByName(column = "Name", required = true)
        public String name;

        public CsvStats() {
        }
    }

    public record Reward(
            int exp,
            int gold,
            int dropRate,
            int item,
            String name
    ) {
        public Reward(CsvReward csvReward) {
            this(
                    csvReward.exp,
                    csvReward.gold,
                    csvReward.dropRate,
                    csvReward.item,
                    csvReward.name
            );
        }
    }

    public static class CsvReward {
        @CsvBindByName(column = "Exp", required = true)
        public int exp;
        @CsvBindByName(column = "Gold", required = true)
        public int gold;
        @CsvBindByName(column = "Drop Rate", required = true)
        public int dropRate;
        @CsvBindByName(column = "Item", required = true)
        public int item;
        @CsvBindByName(column = "Name", required = true)
        public String name;

        public CsvReward() {
        }
    }
}
