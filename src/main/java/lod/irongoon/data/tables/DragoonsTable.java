package lod.irongoon.data.tables;

import com.opencsv.bean.CsvToBeanBuilder;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.Dragoon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class DragoonsTable implements Table {
    private final static String externalFile = IrongoonConfig.getFullPath("scdk-dragoon-stats");

    final List<Dragoon> table = new ArrayList<>();
    final Map<String, Dragoon> byName = new HashMap<>();

    public DragoonsTable() {}

    @Override
    public void initialize() throws FileNotFoundException {
        final List<Object> l = new CsvToBeanBuilder<>(new FileReader(externalFile))
                .withType(Dragoon.CsvStatsPerLevel.class)
                .withSkipLines(1) // Skip header line
                .build()
                .parse();

        // Collect each level's stats to each dragoon
        final Map<String, List<Dragoon.StatsPerLevel>> dragoons = new HashMap<>();
        for (final var i : l) {
            final Dragoon.CsvStatsPerLevel stats = (Dragoon.CsvStatsPerLevel) i;
            final String name = stats.name().split(" ")[0];
            if (!dragoons.containsKey(name)) {
                dragoons.put(name, new ArrayList<>());
            }
            final var list = dragoons.get(name);
            list.add(new Dragoon.StatsPerLevel(stats));
        }

        for (Map.Entry<String, List<Dragoon.StatsPerLevel>> e : dragoons.entrySet()) {
            final var d = new Dragoon(e.getKey(), e.getValue().toArray(new Dragoon.StatsPerLevel[0]));
            this.table.add(d);
            this.byName.put(d.getName(), d);

        }
    }

    public enum Index {
        DART,
        LAVITZ,
        SHANA,
        ROSE,
        HASCHEL,
        ALBERT,
        MERU,
        KONGOL,
        MIRANDA;
    }

    public Dragoon getDragoon(final Index index) {
        return this.table.get(index.ordinal());
    }

    public Dragoon getDragoon(final int dragoonID) {
        return this.table.get(dragoonID);
    }

    public Dragoon.StatsPerLevel getStats(final int dragoonID, final int level) {
        return this.table.get(dragoonID).getStats(level);
    }

    public Dragoon getDragoon(final String name) {
        final var c = this.byName.get(name);
        if (c == null) {
            throw new IllegalArgumentException("unknown dragoon " + name);
        }
        return c;
    }

    public Collection<String> getNames() {
        return this.byName.keySet();
    }

    public int size() {
        return this.table.size();
    }
}
