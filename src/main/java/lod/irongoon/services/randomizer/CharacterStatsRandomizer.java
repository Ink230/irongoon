package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.CharacterStatsParser;

import java.util.*;

public class CharacterStatsRandomizer {
    private static final CharacterStatsRandomizer instance = new CharacterStatsRandomizer();
    public static CharacterStatsRandomizer getInstance() { return instance; }

    private CharacterStatsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final CharacterStatsParser parser = CharacterStatsParser.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit growDivineFruit(int[] distribution, int totalStats, DivineFruit previousFruit) {
        int attack = statRandomizer.calculateFinalStat(distribution[0], totalStats) + previousFruit.bodyAttack;
        int defense = statRandomizer.calculateFinalStat(distribution[1], totalStats) + previousFruit.bodyDefense;
        int magicAttack = statRandomizer.calculateFinalStat(distribution[2], totalStats) + previousFruit.bodyMagicAttack;
        int magicDefense = statRandomizer.calculateFinalStat(distribution[3], totalStats) + previousFruit.bodyMagicDefense;

        return new DivineFruit(attack, defense, magicAttack, magicDefense);
    }

    public DivineFruit randomizeMaintainStock(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0));

        for(int subLevel = 1; subLevel <= level; subLevel++) {
            var totalStatsOfCharacterByLevel = parser.getTotalStatsOfCharacterByLevel(characterId, subLevel) - parser.getTotalStatsOfCharacterByLevel(characterId, subLevel - 1);

            var distribution = statRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, config.bodyTotalStatsDistributionPerLevel, config.bodyNumberOfStatsAmount);

            divineTree.add(growDivineFruit(distribution, totalStatsOfCharacterByLevel, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit randomizeWithBounds(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0));

        for (int subLevel = 1; subLevel <= level; subLevel++) {
            var totalStatsPerCharacterByLevel = parser.getTotalStatsOfAllCharactersByLevel(subLevel);
            var totalStatsPerCharacterByLevelMinusOne = parser.getTotalStatsOfAllCharactersByLevel(subLevel - 1);

            for (int i = 0; i < totalStatsPerCharacterByLevel.length; i++) {
                totalStatsPerCharacterByLevel[i] -= totalStatsPerCharacterByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(totalStatsPerCharacterByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(totalStatsPerCharacterByLevel).max().orElseThrow();

            Random random = new Random(config.seed);
            int totalStats = minValue + random.nextInt(maxValue - minValue + 1);

            var distribution = statRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, config.bodyTotalStatsDistributionPerLevel, config.bodyNumberOfStatsAmount);

            divineTree.add(growDivineFruit(distribution, totalStats, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit randomizeAverage(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0));

        for (int subLevel = 1; subLevel <= level; subLevel++) {
            var totalStatsPerCharacterByLevel = parser.getAverageTotalStatsOfAllCharactersByLevel(subLevel) - parser.getAverageTotalStatsOfAllCharactersByLevel(subLevel - 1);

            var distribution = statRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, config.bodyTotalStatsDistributionPerLevel, config.bodyNumberOfStatsAmount);

            divineTree.add(growDivineFruit(distribution, totalStatsPerCharacterByLevel, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }
}
