package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.CharacterStatsParser;

import java.util.*;

public class CharacterHPRandomizer {
    private static final CharacterHPRandomizer INSTANCE = new CharacterHPRandomizer();
    public static CharacterHPRandomizer getInstance() { return INSTANCE; }

    private CharacterHPRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final CharacterStatsParser parser = CharacterStatsParser.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit growDivineFruit(int HP, DivineFruit previousFruit) {
        int hp = HP + previousFruit.maxHP;
        return new DivineFruit(hp, 0);
    }

    public DivineFruit randomizeMaintainStock(int characterId, int level) {
        return new DivineFruit(parser.getHPOfCharacterByLevel(characterId, level), 0);
    }

    public DivineFruit randomizeWithBounds(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0));

        for(int subLevel = 1; subLevel <= level; subLevel++) {
            var hpOfCharactersByLevel = parser.getHPOfAllCharactersByLevel(subLevel);
            var hpOfCharactersByLevelMinusOne = parser.getHPOfAllCharactersByLevel(subLevel - 1);

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
            var hpOfCharactersByLevel = parser.getHPOfAllCharactersByLevel(subLevel);
            var hpOfCharactersByLevelMinusOne = parser.getHPOfAllCharactersByLevel(subLevel - 1);

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
            var hpOfCharacterByLevel = parser.getHPOfCharacterByLevel(characterId, subLevel);
            var hpOfCharacterByLevelMinusOne = parser.getHPOfCharacterByLevel(characterId,subLevel - 1);
            var hpAvailable = hpOfCharacterByLevel - hpOfCharacterByLevelMinusOne;

            int hp = statRandomizer.calculatePercentModifiedBoundedStat(config.NonBaselineStatsLowerPercentBound, config.NonBaselineStatsUpperPercentBound, hpAvailable, 939 + characterId);

            divineTree.add(growDivineFruit(hp, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }
}
