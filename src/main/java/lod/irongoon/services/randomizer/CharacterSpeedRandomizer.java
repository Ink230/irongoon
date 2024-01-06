package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.Tables;
import lod.irongoon.data.tables.CharactersTable;
import lod.irongoon.models.Character;
import lod.irongoon.models.DivineFruit;

import java.util.*;

public class CharacterSpeedRandomizer {
    private static final CharacterSpeedRandomizer INSTANCE = new CharacterSpeedRandomizer();
    public static CharacterSpeedRandomizer getInstance() { return INSTANCE; }

    private CharacterSpeedRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final CharactersTable characters = Tables.getCharacterTable();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(int speed) {
        return new DivineFruit(speed);
    }

    public DivineFruit randomizeMaintainStock(int characterId, int level) {
        return createDivineFruit(this.characters.getCharacterStats(characterId, level).speed());
    }

    public DivineFruit randomizeWithBounds(int characterId, int level) {
        var speedOfAllCharacters = this.getAllSpeeds(level);

        var minValue = Arrays.stream(speedOfAllCharacters).min().orElseThrow();
        var maxValue = Arrays.stream(speedOfAllCharacters).max().orElseThrow();

        var randomizedSpeed = statRandomizer.calculateRandomNumberBetweenBounds(minValue, maxValue, 787 + characterId);

        return createDivineFruit(randomizedSpeed);
    }

    public DivineFruit randomizeStockWithBounds(int characterId, int level) {
        var speedOfCharacter = this.characters.getCharacterStats(characterId, level).speed();

        var randomizedSpeed = statRandomizer.calculatePercentModifiedBoundedStat(config.nonBaselineStatsLowerPercentBound, config.nonBaselineStatsUpperPercentBound, speedOfCharacter, 801 + characterId);

        return createDivineFruit(randomizedSpeed);
    }

    private int[] getAllSpeeds(int level) {
        return StatsUtil.GetStatForAllCharacters(level, Character.StatsPerLevel::speed);
    }
}
