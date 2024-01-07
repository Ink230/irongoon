package lod.irongoon.services.randomizer;

import lod.irongoon.data.Tables;
import lod.irongoon.models.Character;
import lod.irongoon.models.Dragoon;

class StatValueCompiler {
    public interface IValue<T> {
        int get(T t);
    }

    public static int[] Characters(int level, IValue<Character.StatsPerLevel> getter) {
        final var table = Tables.getCharacterTable();
        final var count = table.size();
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            final var c = table.getCharacter(i);
            result[i] = getter.get(c.getStats(level));
        }
        return result;
    }

    public static int[] Dragoons(int level, IValue<Dragoon.StatsPerLevel> getter) {
        final var table = Tables.getDragoonTable();
        final var count = table.size();
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            final var c = table.getDragoon(i);
            result[i] = getter.get(c.getStats(level));
        }
        return result;
    }
}
