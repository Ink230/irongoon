package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.CharactersTable;
import lod.irongoon.models.Character;
import lod.irongoon.models.DivineFruit;

import java.util.*;

public class CharacterHPRandomizer {
    private static final CharacterHPRandomizer INSTANCE = new CharacterHPRandomizer();
    public static CharacterHPRandomizer getInstance() { return INSTANCE; }

    private CharacterHPRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final CharactersTable characters = Tables.getInstance().getCharacterTable();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit growDivineFruit(int HP, DivineFruit previousFruit) {
        int hp = HP + previousFruit.maxHP;
        return new DivineFruit(hp, 0);
    }

    public DivineFruit randomizeMaintainStock(int characterId, int level) {
        final var stats = this.characters.getCharacterStats(characterId, level);
        return new DivineFruit(stats.hp(), 0);
    }

    public DivineFruit randomizeWithBounds(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0));

        for(int subLevel = 1; subLevel <= level; subLevel++) {
            var hpOfCharactersByLevel = this.getAllHps(subLevel);
            var hpOfCharactersByLevelMinusOne = this.getAllHps(subLevel - 1);

            for(int i = 0; i < hpOfCharactersByLevel.length; i++) {
                hpOfCharactersByLevel[i] -= hpOfCharactersByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(hpOfCharactersByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(hpOfCharactersByLevel).max().orElseThrow();

            var hp = statRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, 909 + characterId);

            divineTree.add(growDivineFruit(hp, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit randomizeWithBoundsAndPercentModifiers(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0));

        for(int subLevel = 1; subLevel <= level; subLevel++) {
            var hpOfCharactersByLevel = this.getAllHps(subLevel);
            var hpOfCharactersByLevelMinusOne = this.getAllHps(subLevel - 1);

            for(int i = 0; i < hpOfCharactersByLevel.length; i++) {
                hpOfCharactersByLevel[i] -= hpOfCharactersByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(hpOfCharactersByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(hpOfCharactersByLevel).max().orElseThrow();

            var hpAvailable = statRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, 909 + characterId);

            var hp = statRandomizer.calculatePercentModifiedBoundedStat(config.hpStatLowerPercentBound, config.hpStatUpperPercentBound, hpAvailable, 909 + characterId);

            divineTree.add(growDivineFruit(hp, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit randomizeStockWithBounds(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0));

        for(int subLevel = 1; subLevel <= level; subLevel++) {
            var hpOfCharacterByLevel = this.characters.getCharacterStats(characterId, subLevel).hp();
            var hpOfCharacterByLevelMinusOne = this.characters.getCharacterStats(characterId,subLevel - 1).hp();
            var hpAvailable = hpOfCharacterByLevel - hpOfCharacterByLevelMinusOne;

            int hp = statRandomizer.calculatePercentModifiedBoundedStat(config.nonBaselineStatsLowerPercentBound, config.nonBaselineStatsUpperPercentBound, hpAvailable, 939 + characterId);

            divineTree.add(growDivineFruit(hp, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    private int[] getAllHps(int level) {
        return StatsUtil.GetStatForAllCharacters(level, Character.StatsPerLevel::hp);
    }
}
