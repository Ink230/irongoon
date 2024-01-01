package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.parse.game.CharacterStatsParser;

import java.util.*;

public class CharacterSpeedRandomizer {
    private static final CharacterSpeedRandomizer INSTANCE = new CharacterSpeedRandomizer();
    public static CharacterSpeedRandomizer getInstance() { return INSTANCE; }

    private CharacterSpeedRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final CharacterStatsParser parser = CharacterStatsParser.getInstance();
    private final StatsRandomizer statsRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(int speed) {
        return new DivineFruit(speed);
    }

    public DivineFruit randomizeMaintainStock(int character, int level) {
        return createDivineFruit(parser.getSpeedOfCharacterByLevel(character, level));
    }

    public DivineFruit randomizeWithBounds(int character, int level) {
        var speedOfAllCharacters = parser.getCharactersSpeedStats(level);

        var minValue = Arrays.stream(speedOfAllCharacters).min().orElseThrow();
        var maxValue = Arrays.stream(speedOfAllCharacters).max().orElseThrow();

        var randomizedSpeed = statsRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, 787);

        return createDivineFruit(randomizedSpeed);
    }

    public DivineFruit randomizeStockWithBounds(int character, int level) {
        var speedOfCharacter = parser.getSpeedOfCharacterByLevel(character, level);

        var randomizedSpeed = statsRandomizer.calculatePercentModifiedBoundedStat(config.NonBaselineStatsLowerPercentBound, config.NonBaselineStatsUpperPercentBound, speedOfCharacter, 801);

        return createDivineFruit(randomizedSpeed);
    }
}
