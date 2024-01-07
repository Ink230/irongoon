package lod.irongoon.services.randomizer;

import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.characters.CharacterStatsEvent;

import lod.irongoon.config.Config;
import lod.irongoon.models.DivineFruit;

public class Randomizer {
    private Randomizer() {
    }

    public static DivineFruit doCharacterStats(CharacterStatsEvent character) {
        return switch (Config.bodyTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    CharacterStatsRandomizer.randomizeWithBounds(character.characterId, character.level);
            case MAINTAIN_STOCK ->
                    CharacterStatsRandomizer.randomizeMaintainStock(character.characterId, character.level);
            case AVERAGE_ALL_CHARACTERS ->
                    CharacterStatsRandomizer.randomizeAverage(character.characterId, character.level);
        };
    }

    public static DivineFruit doCharacterHP(CharacterStatsEvent character) {
        return switch (Config.hpStatPerLevel) {
            case MAINTAIN_STOCK -> CharacterHPRandomizer.randomizeMaintainStock(character.characterId, character.level);
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    CharacterHPRandomizer.randomizeWithBounds(character.characterId, character.level);
            case RANDOMIZE_STOCK_BOUNDS ->
                    CharacterHPRandomizer.randomizeStockWithBounds(character.characterId, character.level);
            case RANDOMIZE_BOUNDS_PERCENT_MODIFIED_PER_LEVEL ->
                    CharacterHPRandomizer.randomizeWithBoundsAndPercentModifiers(character.characterId, character.level);
        };
    }

    public static DivineFruit doCharacterSpeed(CharacterStatsEvent character) {
        return switch (Config.speedStatPerLevel) {
            case MAINTAIN_STOCK ->
                    CharacterSpeedRandomizer.randomizeMaintainStock(character.characterId, character.level);
            case RANDOMIZE_BOUNDS ->
                    CharacterSpeedRandomizer.randomizeWithBounds(character.characterId, character.level);
            case RANDOMIZE_RANDOM_BOUNDS ->
                    CharacterSpeedRandomizer.randomizeStockWithBounds(character.characterId, character.level);
        };
    }

    public static DivineFruit doDragoonStats(CharacterStatsEvent dragoon) {
        return switch (Config.bodyTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    DragoonStatsRandomizer.randomizeWithBounds(dragoon.characterId, dragoon.dlevel);
            case MAINTAIN_STOCK -> DragoonStatsRandomizer.randomizeMaintainStock(dragoon.characterId, dragoon.dlevel);
            case AVERAGE_ALL_CHARACTERS -> DragoonStatsRandomizer.randomizeAverage(dragoon.characterId, dragoon.dlevel);
        };
    }

    public static DivineFruit doMonsterStats(MonsterStatsEvent monster) {
        return switch (Config.monsterTotalStats) {
            case RANDOMIZE_BOUNDS -> MonsterStatsRandomizer.randomizeWithBounds(monster.enemyId);
            case MAINTAIN_STOCK -> MonsterStatsRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_STOCK_BOUNDS -> MonsterStatsRandomizer.randomizeStockWithPercentBounds(monster.enemyId);
        };
    }

    public static DivineFruit doMonsterHP(MonsterStatsEvent monster) {
        return switch (Config.hpStatMonsters) {
            case MAINTAIN_STOCK -> MonsterHPRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_BOUNDS -> MonsterHPRandomizer.randomizeStockWithBounds(monster.enemyId);
        };
    }

    public static DivineFruit doMonsterSpeed(MonsterStatsEvent monster) {
        return switch (Config.speedStatMonsters) {
            case MAINTAIN_STOCK -> MonsterSpeedRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_BOUNDS -> MonsterSpeedRandomizer.randomizeWithBounds(monster.enemyId);
            case RANDOMIZE_RANDOM_BOUNDS -> MonsterSpeedRandomizer.randomizeRandomWithBounds();
        };
    }

    public static void doMonsterVariance(DivineFruit monsterStats, DivineFruit monsterHP, DivineFruit monsterSpeed) {
        switch (Config.statsVarianceMonsters) {
            case STOCK:
                break;
            case RANDOM_PERCENT_BOUNDS:
                MonsterStatsRandomizer.varianceStats(monsterStats, monsterHP, monsterSpeed);
                break;
        }
    }
}
