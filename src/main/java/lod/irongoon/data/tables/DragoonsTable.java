package lod.irongoon.data.tables;

import com.opencsv.bean.CsvToBeanBuilder;

import lod.irongoon.config.Config;
import lod.irongoon.models.Character;
import lod.irongoon.models.Dragoon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class DragoonsTable implements Table {
    private final static String externalFile = Config.getFullPath("scdk-dragoon-stats");

    final List<Dragoon> table = new ArrayList<>();
    final Map<String, Dragoon> byName = new HashMap<>();

    public DragoonsTable() {}

    @Override
    public void initialize() throws FileNotFoundException {
        final List<Object> l = new CsvToBeanBuilder<>(new FileReader(externalFile))
                .withType(Dragoon.StatsPerLevel.class)
                .build()
                .parse();

        // Collect each level's stats to each dragoon
        final Map<String, List<Dragoon.StatsPerLevel>> dragoons = new HashMap<>();
        for (final var i : l) {
            final Dragoon.StatsPerLevel stats = (Dragoon.StatsPerLevel) i;
            final String name = stats.getName().split(" ")[0];
            if (!dragoons.containsKey(name)) {
                dragoons.put(name, new ArrayList<>());
            }
            dragoons.get(name).add(stats);
        }

        for (Map.Entry<String, List<Dragoon.StatsPerLevel>> e : dragoons.entrySet()) {
            final var d = new Dragoon(e.getKey(), e.getValue().toArray(new Dragoon.StatsPerLevel[0]));
            this.byName.put(d.getName(), d);
        }

        this.table.add(this.byName.get("Dart"));
        this.table.add(this.byName.get("Lavitz"));
        this.table.add(this.byName.get("Shana"));
        this.table.add(this.byName.get("Rose"));
        this.table.add(this.byName.get("Haschel"));
        this.table.add(this.byName.get("Albert"));
        this.table.add(this.byName.get("Meru"));
        this.table.add(this.byName.get("Kongol"));
        if (this.byName.containsKey("???")) {
            this.table.add(this.byName.get("???"));
        } else {
            this.table.add(this.byName.get("Miranda"));
        }
    }

    public Dragoon getDragoon(final Character.Name name) {
        return this.table.get(name.ordinal());
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