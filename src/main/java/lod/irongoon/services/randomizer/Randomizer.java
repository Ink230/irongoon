package lod.irongoon.services.randomizer;

import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.characters.CharacterStatsEvent;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.models.DivineFruit;

public class Randomizer {
    private static final Randomizer instance = new Randomizer();
    public static Randomizer getInstance() { return instance; }

    private Randomizer() {}

    private static final IrongoonConfig config = IrongoonConfig.getInstance();
    private static final SeedRandomizer seedRandomizer = SeedRandomizer.getInstance();
    private final CharacterStatsRandomizer characterStatsRandomizer = CharacterStatsRandomizer.getInstance();
    private final CharacterHPRandomizer characterHPRandomizer = CharacterHPRandomizer.getInstance();
    private final CharacterSpeedRandomizer characterSpeedRandomizer = CharacterSpeedRandomizer.getInstance();
    private final DragoonStatsRandomizer dragoonStatsRandomizer = DragoonStatsRandomizer.getInstance();
    private final MonsterStatsRandomizer monsterStatsRandomizer = MonsterStatsRandomizer.getInstance();
    private final MonsterHPRandomizer monsterHPRandomizer = MonsterHPRandomizer.getInstance();
    private final MonsterSpeedRandomizer monsterSpeedRandomizer = MonsterSpeedRandomizer.getInstance();
    private final MonsterElementRandomizer monsterElementRandomizer = MonsterElementRandomizer.getInstance();
    private final BattleRandomizer battleRandomizer = BattleRandomizer.getInstance();

    public static String retrieveNewCampaignSeed() {
        String newSeed;
        newSeed = seedRandomizer.generateNewSeed();
        config.campaignSeed = newSeed;
        return newSeed;
    }

    public DivineFruit doCharacterStats(CharacterStatsEvent character) {
        return switch (config.bodyTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    characterStatsRandomizer.randomizeWithBounds(character.characterId, character.level);
            case MAINTAIN_STOCK ->
                    characterStatsRandomizer.randomizeMaintainStock(character.characterId, character.level);
            case AVERAGE_ALL_CHARACTERS ->
                    characterStatsRandomizer.randomizeAverage(character.characterId, character.level);
            case STOCK -> characterStatsRandomizer.stock(character.characterId, character.level);
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
        return switch (config.dragoonTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    dragoonStatsRandomizer.randomizeWithBounds(dragoon.characterId, dragoon.dlevel);
            case MAINTAIN_STOCK -> dragoonStatsRandomizer.randomizeMaintainStock(dragoon.characterId, dragoon.dlevel);
            case AVERAGE_ALL_CHARACTERS -> dragoonStatsRandomizer.randomizeAverage(dragoon.characterId, dragoon.dlevel);
            case STOCK -> dragoonStatsRandomizer.stock(dragoon.characterId, dragoon.dlevel);
        };
    }

    public DivineFruit doMonsterStats(MonsterStatsEvent monster) {
        return switch (config.monsterTotalStats) {
            case RANDOMIZE_BOUNDS -> monsterStatsRandomizer.randomizeWithBounds(monster.enemyId);
            case MAINTAIN_STOCK -> monsterStatsRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_STOCK_BOUNDS -> monsterStatsRandomizer.randomizeStockWithPercentBounds(monster.enemyId);
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

    public DivineFruit doMonsterElement (MonsterStatsEvent monster) {
        return switch (config.monsterElements) {
            case MAINTAIN_STOCK -> monsterElementRandomizer.maintainStock(monster.enemyId);
            case RANDOMIZE -> monsterElementRandomizer.randomizeMonsterElement(monster.enemyId);
            case RANDOMIZE_RANDOM -> monsterElementRandomizer.randomizeRandomMonsterElement(monster.enemyId);
            case RANDOMIZE_AND_TYPINGS -> monsterElementRandomizer.randomizeMonsterElementAndImmunity(monster.enemyId);
            case RANDOMIZE_RANDOM_AND_TYPINGS -> monsterElementRandomizer.randomizeRandomMonsterElementAndImmunity();
        };
    }

    public void doMonsterVariance(DivineFruit monsterStats, DivineFruit monsterHP, DivineFruit monsterSpeed) {
        switch (config.statsVarianceMonsters) {
            case STOCK:
                break;
            case RANDOM_PERCENT_BOUNDS:
                monsterStatsRandomizer.varianceStats(monsterStats, monsterHP, monsterSpeed);
                break;
        }
    }

    public int doBattleStage(int battleStageId, int encounterId, int submapId) {
        return switch (config.battleStage) {
            case STOCK -> battleRandomizer.maintainStock(battleStageId);
            case RANDOM -> battleRandomizer.randomRandom();
            case RANDOM_FIXED_ENCOUNTER -> battleRandomizer.randomFixedEncounter(encounterId);
            case RANDOM_FIXED_SUBMAP -> battleRandomizer.randomFixedSubmap(submapId);
        };
    }
}
