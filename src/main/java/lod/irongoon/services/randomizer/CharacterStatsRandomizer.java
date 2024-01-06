package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.CharactersTable;
import lod.irongoon.models.Character;
import lod.irongoon.models.DivineFruit;

import java.util.*;

public class CharacterStatsRandomizer {
    private static final CharacterStatsRandomizer INSTANCE = new CharacterStatsRandomizer();

    public static CharacterStatsRandomizer getInstance() {
        return INSTANCE;
    }

    private CharacterStatsRandomizer() {
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final CharactersTable characters = Tables.getInstance().getCharacterTable();
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

        for (int subLevel = 1; subLevel <= level; subLevel++) {
            final var totalStatsLevel = this.characters.getCharacterStats(characterId, level).totalStatPoints();
            final var totalStatsLevelMinusOne = this.characters.getCharacterStats(characterId, level - 1).totalStatPoints();
            final var distribution = statRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, config.bodyTotalStatsDistributionPerLevel, config.bodyNumberOfStatsAmount, characterId);

            divineTree.add(growDivineFruit(distribution, totalStatsLevel - totalStatsLevelMinusOne, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit randomizeWithBounds(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0));

        for (int subLevel = 1; subLevel <= level; subLevel++) {
            var totalStatsPerCharacterByLevel = this.getAllTotalStats(subLevel);
            var totalStatsPerCharacterByLevelMinusOne = this.getAllTotalStats(subLevel - 1);

            for (int i = 0; i < totalStatsPerCharacterByLevel.length; i++) {
                totalStatsPerCharacterByLevel[i] -= totalStatsPerCharacterByLevelMinusOne[i];
            }

            var minValue = Arrays.stream(totalStatsPerCharacterByLevel).min().orElseThrow();
            var maxValue = Arrays.stream(totalStatsPerCharacterByLevel).max().orElseThrow();

            var totalStats = statRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, characterId);

            var distribution = statRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, config.bodyTotalStatsDistributionPerLevel, config.bodyNumberOfStatsAmount, characterId);

            divineTree.add(growDivineFruit(distribution, totalStats, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    public DivineFruit randomizeAverage(int characterId, int level) {
        var divineTree = new ArrayList<DivineFruit>();
        divineTree.add(new DivineFruit(0, 0, 0, 0));

        for (int subLevel = 1; subLevel <= level; subLevel++) {
            var totalStatsPerCharacterByLevel = this.getAverageTotalStats(subLevel) - this.getAverageTotalStats(subLevel - 1);
            var distribution = statRandomizer.calculateDistributionOfTotalStats(subLevel, characterId, config.bodyTotalStatsDistributionPerLevel, config.bodyNumberOfStatsAmount, characterId);

            divineTree.add(growDivineFruit(distribution, totalStatsPerCharacterByLevel, divineTree.get(divineTree.size() - 1)));
        }

        return divineTree.get(divineTree.size() - 1);
    }

    private int[] getAllTotalStats(int level) {
        return StatsUtil.GetStatForAllCharacters(level, Character.StatsPerLevel::totalStatPoints);
    }

    private int getAverageTotalStats(int level) {
        final var totalStats = this.getAllTotalStats(level);
        int sum = 0;
        for (int i : totalStats) {
            sum += i;
        }
        return sum / totalStats.length;
    }
}
