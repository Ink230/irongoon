package lod.irongoon.services.randomizer;


import lod.irongoon.config.Config;
import lod.irongoon.data.Tables;
import lod.irongoon.models.Character;
import lod.irongoon.models.DivineFruit;

import java.util.*;

public class CharacterSpeedRandomizer {
    private CharacterSpeedRandomizer() {}

    private static DivineFruit createDivineFruit(int speed) {
        return new DivineFruit(speed);
    }

    public static DivineFruit randomizeMaintainStock(int characterId, int level) {
        return createDivineFruit(Tables.getCharacterTable().getCharacterStats(characterId, level).getSpeed());
    }

    public static DivineFruit randomizeWithBounds(int characterId, int level) {
        var speedOfAllCharacters = getAllSpeeds(level);

        var minValue = Arrays.stream(speedOfAllCharacters).min().orElseThrow();
        var maxValue = Arrays.stream(speedOfAllCharacters).max().orElseThrow();

        var randomizedSpeed = StatsRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, 787 + characterId);

        return createDivineFruit(randomizedSpeed);
    }

    public static DivineFruit randomizeStockWithBounds(int characterId, int level) {
        var speedOfCharacter = Tables.getCharacterTable().getCharacterStats(characterId, level).getSpeed();

        var randomizedSpeed = StatsRandomizer.calculatePercentModifiedBoundedStat(Config.nonBaselineStatsLowerPercentBound, Config.nonBaselineStatsUpperPercentBound, speedOfCharacter, 801 + characterId);

        return createDivineFruit(randomizedSpeed);
    }

    private static int[] getAllSpeeds(int level) {
        return StatValueCompiler.Characters(level, Character.StatsPerLevel::getSpeed);
    }
}
