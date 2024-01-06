package lod.irongoon.data.tables;

import com.opencsv.bean.CsvToBeanBuilder;
import lod.irongoon.models.Character;
import lod.irongoon.config.IrongoonConfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
                .withSkipLines(1) // Skip header line
                .build()
                .parse();

        // Collect each level's stats to each character
        final Map<String, List<Character.StatsPerLevel>> characters = new HashMap<>();
        for (final var i : l) {
            final Character.CsvStatsPerLevel stats = (Character.CsvStatsPerLevel) i;
            final String name = stats.name().split(" ")[0];
            if (!characters.containsKey(name)) {
                characters.put(name, new ArrayList<>());
            }
            final var list = characters.get(name);
            list.add(new Character.StatsPerLevel(stats));
        }

        for (Map.Entry<String, List<Character.StatsPerLevel>> e : characters.entrySet()) {
            final var c = new Character(e.getKey(), e.getValue().toArray(new Character.StatsPerLevel[0]));
            this.table.add(c);
            this.byName.put(c.getName(), c);

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
