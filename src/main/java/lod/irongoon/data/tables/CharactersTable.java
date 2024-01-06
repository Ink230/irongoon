package lod.irongoon.data.tables;

import com.opencsv.bean.CsvToBeanBuilder;
import lod.irongoon.models.Character;
import lod.irongoon.config.IrongoonConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CharactersTable implements Table {
    private final static String externalFile = IrongoonConfig.getFullPath("scdk-character-stats");

    final List<Character> table = new ArrayList<>();
    final Map<String, Character> byName = new HashMap<>();

    public CharactersTable() {}

    @Override
    public void initialize() throws FileNotFoundException {
        final List<Object> l = new CsvToBeanBuilder<>(new FileReader(externalFile))
                .withType(Character.CsvStatsPerLevel.class)
                .build()
                .parse();

        // Collect each level's stats to each character
        final Map<String, List<Character.StatsPerLevel>> characters = new HashMap<>();
        for (final var i : l) {
            final Character.CsvStatsPerLevel stats = (Character.CsvStatsPerLevel) i;
            final String name = stats.name.split(" ")[0];
            if (!characters.containsKey(name)) {
                characters.put(name, new ArrayList<>());
            }
            final var list = characters.get(name);
            list.add(new Character.StatsPerLevel(stats));
        }

        for (Map.Entry<String, List<Character.StatsPerLevel>> e : characters.entrySet()) {
            final var c = new Character(e.getKey(), e.getValue().toArray(new Character.StatsPerLevel[0]));
            this.byName.put(c.getName(), c);

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

    public enum Index {
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
        Index(String name) {
            this.name = name;
        }
    }

    public Character getCharacter(final Index index) {
        return this.table.get(index.ordinal());
    }

    public Character getCharacter(final int characterID) {
        return this.table.get(characterID);
    }

    public Character getCharacter(String name) {
        final var c = this.byName.get(name);
        if (c == null) {
            throw new IllegalArgumentException("unknown character " + name);
        }
        return c;
    }

    public Character.StatsPerLevel getCharacterStats(final int characterId, final int level) {
        return this.getCharacter(characterId).getStats(level);
    }

    public Collection<String> getNames() {
        return this.byName.keySet();
    }

    public int size() {
        return this.table.size();
    }
}
