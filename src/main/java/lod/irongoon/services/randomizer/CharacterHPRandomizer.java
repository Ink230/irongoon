package lod.irongoon.services.randomizer;


import lod.irongoon.config.Config;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.CharactersTable;
import lod.irongoon.models.Character;
import lod.irongoon.models.DivineFruit;

import java.util.*;

public class CharacterHPRandomizer {
    private CharacterHPRandomizer() {}

    private static DivineFruit growDivineFruit(int HP, DivineFruit previousFruit) {
        int hp = HP + previousFruit.maxHP;
        return new DivineFruit(hp, 0);
    }

    public static DivineFruit randomizeMaintainStock(int characterId, int level) {
        final var stats = Tables.getCharacterTable().getCharacterStats(characterId, level);
        return new DivineFruit(stats.getMaxHp(), 0);
    }

    public static DivineFruit randomizeWithBounds(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0));

        for(int subLevel = 1; subLevel <= level; subLevel++) {
            var hpOfCharactersByLevel = getAllHps(subLevel);
            var hpOfCharactersByLevelMinusOne = getAllHps(subLevel - 1);

            for(int i = 0; i < hpOfCharactersByLevel.length; i++) {
                hpOfCharactersByLevel[i] -= hpOfCharactersByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(hpOfCharactersByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(hpOfCharactersByLevel).max().orElseThrow();

            var hp = StatsRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, 909 + characterId);

            divineTree.add(growDivineFruit(hp, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public static DivineFruit randomizeWithBoundsAndPercentModifiers(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0));

        for(int subLevel = 1; subLevel <= level; subLevel++) {
            var hpOfCharactersByLevel = getAllHps(subLevel);
            var hpOfCharactersByLevelMinusOne = getAllHps(subLevel - 1);

            for(int i = 0; i < hpOfCharactersByLevel.length; i++) {
                hpOfCharactersByLevel[i] -= hpOfCharactersByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(hpOfCharactersByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(hpOfCharactersByLevel).max().orElseThrow();

            var hpAvailable = StatsRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, 909 + characterId);

            var hp = StatsRandomizer.calculatePercentModifiedBoundedStat(Config.hpStatLowerPercentBound, Config.hpStatUpperPercentBound, hpAvailable, 909 + characterId);

            divineTree.add(growDivineFruit(hp, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public static DivineFruit randomizeStockWithBounds(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0));

        final var characters = Tables.getCharacterTable();
        for(int subLevel = 1; subLevel <= level; subLevel++) {
            var hpOfCharacterByLevel = characters.getCharacterStats(characterId, subLevel).getMaxHp();
            var hpOfCharacterByLevelMinusOne = characters.getCharacterStats(characterId,subLevel - 1).getMaxHp();
            var hpAvailable = hpOfCharacterByLevel - hpOfCharacterByLevelMinusOne;

            int hp = StatsRandomizer.calculatePercentModifiedBoundedStat(Config.nonBaselineStatsLowerPercentBound, Config.nonBaselineStatsUpperPercentBound, hpAvailable, 939 + characterId);

            divineTree.add(growDivineFruit(hp, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    private static int[] getAllHps(int level) {
        return SumStats.Characters(level, Character.StatsPerLevel::getMaxHp);
    }
}
