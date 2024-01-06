package lod.irongoon.data.tables;

import com.opencsv.bean.CsvToBeanBuilder;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.Monster;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class MonstersTable implements Table {
    private final static String statsExternalFile = IrongoonConfig.getFullPath("scdk-monster-stats");
    private final static String rewardsExternalFile = IrongoonConfig.getFullPath("scdk-monster-rewards");

    private final List<Monster> tables = new ArrayList<>();
    private final Map<String, Monster> byName = new HashMap<>();

    public MonstersTable() {
    }

    @Override
    public void initialize() throws FileNotFoundException {
        final var stats = initializeStats();
        final var rewards = initializeRewards();
        final var sorted = new TreeMap<Integer, Monster>();
        for (final var e : stats.entrySet()) {
            final String name = e.getKey();
            final var r = rewards.get(name);
            if (r == null) {
                throw new RuntimeException("Monster " + name + " does not have a reward entry.");
            }

            final Monster m = new Monster(e.getValue(), r);
            this.byName.put(name, m);
            sorted.put(m.getID(), m);
        }
        this.tables.addAll(sorted.values());
    }

    public HashMap<String, Monster.Stats> initializeStats() throws FileNotFoundException {
        final List<Object> l = new CsvToBeanBuilder<>(new FileReader(statsExternalFile))
                .withType(Monster.Stats.class)
                .withSkipLines(1) // Skip header line
                .build()
                .parse();

        final HashMap<String, Monster.Stats> stats = new HashMap<>();
        for (final var i : l) {
            final Monster.Stats s = (Monster.Stats) i;
            stats.put(s.name(), s);
        }
        return stats;
    }

    public HashMap<String, Monster.Reward> initializeRewards() throws FileNotFoundException {
        final List<Object> l = new CsvToBeanBuilder<>(new FileReader(rewardsExternalFile))
                .withType(Monster.Reward.class)
                .withSkipLines(1) // Skip header line
                .build()
                .parse();

        final HashMap<String, Monster.Reward> rewards = new HashMap<>();
        for (final var i : l) {
            final Monster.Reward r = (Monster.Reward) i;
            rewards.put(r.name(), r);
        }
        return rewards;
    }

    public Monster getMonster(final int monsterID) {
        return this.tables.get(monsterID);
    }

    public Monster.Stats getMonsterStats(final int monsterID) {
        return this.tables.get(monsterID).getStats();
    }

    public Monster.Reward getMonsterReward(final int monsterID) {
        return this.tables.get(monsterID).getReward();
    }

    public Monster getMonster(final String name) {
        return this.byName.get(name);
    }

    public Collection<String> getNames() {
        return this.byName.keySet();
    }
}
