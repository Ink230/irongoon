package lod.irongoon.services.randomizer;

import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.characters.CharacterStatsEvent;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.DivineFruit;

public class Randomizer {
    private static final Randomizer instance = new Randomizer();
    public static Randomizer getInstance() { return instance; }

    private Randomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final CharacterStatsRandomizer characterStatsRandomizer = CharacterStatsRandomizer.getInstance();
    private final CharacterHPRandomizer characterHPRandomizer = CharacterHPRandomizer.getInstance();
    private final CharacterSpeedRandomizer characterSpeedRandomizer = CharacterSpeedRandomizer.getInstance();
    private final DragoonStatsRandomizer dragoonStatsRandomizer = DragoonStatsRandomizer.getInstance();
    private final MonsterStatsRandomizer monsterStatsRandomizer = MonsterStatsRandomizer.getInstance();
    private final MonsterHPRandomizer monsterHPRandomizer = MonsterHPRandomizer.getInstance();
    private final MonsterSpeedRandomizer monsterSpeedRandomizer = MonsterSpeedRandomizer.getInstance();

    public DivineFruit doCharacterStats(CharacterStatsEvent character) {
        return switch (config.bodyTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    characterStatsRandomizer.randomizeWithBounds(character.characterId, character.level);
            case MAINTAIN_STOCK ->
                    characterStatsRandomizer.randomizeMaintainStock(character.characterId, character.level);
            case AVERAGE_ALL_CHARACTERS ->
                    characterStatsRandomizer.randomizeAverage(character.characterId, character.level);
        };
    }

    public DivineFruit doCharacterHP(CharacterStatsEvent character) {
        return switch (config.hpStatPerLevel) {
            case MAINTAIN_STOCK -> characterHPRandomizer.randomizeMaintainStock(character.characterId, character.level);
            case RANDOMIZE_BOUNDS_PER_LEVEL -> characterHPRandomizer.randomizeWithBounds(character.characterId, character.level);
            case RANDOMIZE_STOCK_BOUNDS -> characterHPRandomizer.randomizeStockWithBounds(character.characterId, character.level);
            case RANDOMIZE_BOUNDS_PERCENT_MODIFIED_PER_LEVEL -> characterHPRandomizer.randomizeWithBoundsAndPercentModifiers(character.characterId, character.level);
        };
    }

    public DivineFruit doCharacterSpeed(CharacterStatsEvent character) {
        return switch(config.speedStatPerLevel) {
            case MAINTAIN_STOCK -> characterSpeedRandomizer.randomizeMaintainStock(character.characterId, character.level);
            case RANDOMIZE_BOUNDS -> characterSpeedRandomizer.randomizeWithBounds(character.characterId, character.level);
            case RANDOMIZE_RANDOM_BOUNDS -> characterSpeedRandomizer.randomizeStockWithBounds(character.characterId, character.level);
        };
    }

    public DivineFruit doDragoonStats(CharacterStatsEvent dragoon) {
        return switch (config.bodyTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    dragoonStatsRandomizer.randomizeWithBounds(dragoon.characterId, dragoon.dlevel);
            case MAINTAIN_STOCK -> dragoonStatsRandomizer.randomizeMaintainStock(dragoon.characterId, dragoon.dlevel);
            case AVERAGE_ALL_CHARACTERS -> dragoonStatsRandomizer.randomizeAverage(dragoon.characterId, dragoon.dlevel);
        };
    }

    public DivineFruit doMonsterStats(MonsterStatsEvent monster) {
        return switch (config.monsterTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS -> monsterStatsRandomizer.randomizeWithBounds(monster.enemyId);
            case MAINTAIN_STOCK -> monsterStatsRandomizer.randomizeMaintainStock(monster.enemyId);
        };
    }

    public DivineFruit doMonsterHP (MonsterStatsEvent monster){
        return switch (config.hpStatMonsters) {
            case MAINTAIN_STOCK -> monsterHPRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_BOUNDS -> monsterHPRandomizer.randomizeStockWithBounds(monster.enemyId);
        };
    }

    public DivineFruit doMonsterSpeed (MonsterStatsEvent monster) {
        return switch (config.speedStatMonsters) {
            case MAINTAIN_STOCK -> monsterSpeedRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_BOUNDS -> monsterSpeedRandomizer.randomizeWithBounds(monster.enemyId);
            case RANDOMIZE_RANDOM_BOUNDS -> monsterSpeedRandomizer.randomizeRandomWithBounds();
        };
    }
}
