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

    public static class Stats {
        @CsvBindByName(column = "ID", required = true)
        private int id;
        @CsvBindByName(column = "HP", required = true)
        private int hp;
        @CsvBindByName(column = "MP", required = true)
        private int mp;
        @CsvBindByName(column = "AT", required = true)
        private int attack;
        @CsvBindByName(column = "MAT", required = true)
        private int magicAttack;
        @CsvBindByName(column = "SPD", required = true)
        private int speed;
        @CsvBindByName(column = "DF", required = true)
        private int defense;
        @CsvBindByName(column = "MDF", required = true)
        private int magicDefense;
        @CsvBindByName(column = "A-AV", required = true)
        private int attackAvoidance;
        @CsvBindByName(column = "M-AV", required = true)
        private int magicAvoidance;
        @CsvBindByName(column = "SPECIAL", required = true)
        private int special;
        @CsvBindByName(column = "Element", required = true)
        private int element;
        @CsvBindByName(column = "Element Null", required = true)
        private int elementNull;
        @CsvBindByName(column = "Status Resist", required = true)
        private int statusResist;
//        @CsvBindByName(column = "Target Arrow X", required = true)
//        private int targetArrowX;
//        @CsvBindByName(column = "Target Arrow Y", required = true)
//        private int targetArrowY;
//        @CsvBindByName(column = "Target Arrow Z", required = true)
//        private int targetArrowZ;
        @CsvBindByName(column = "TotalStats", required = true)
        private int totalStats;
        @CsvBindByName(column = "Name", required = true)
        private String name;

        public Stats() {
        }

        public int getId() {
            return id;
        }

        public int getHp() {
            return hp;
        }

        public int getMp() {
            return mp;
        }

        public int getAttack() {
            return attack;
        }

        public int getMagicAttack() {
            return magicAttack;
        }

        public int getSpeed() {
            return speed;
        }

        public int getDefense() {
            return defense;
        }

        public int getMagicDefense() {
            return magicDefense;
        }

        public int getAttackAvoidance() {
            return attackAvoidance;
        }

        public int getMagicAvoidance() {
            return magicAvoidance;
        }

        public int getSpecial() {
            return special;
        }

        public int getElement() {
            return element;
        }

        public int getElementNull() {
            return elementNull;
        }

        public int getStatusResist() {
            return statusResist;
        }

        public int getTotalStats() {
            return totalStats;
        }

        public String getName() {
            return name;
        }
    }

    public static class Reward {
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

        public Reward() {
        }

        public int getExp() {
            return exp;
        }

        public int getGold() {
            return gold;
        }

        public int getDropRate() {
            return dropRate;
        }

        public int getItem() {
            return item;
        }

        public String getName() {
            return name;
        }
    }
}
