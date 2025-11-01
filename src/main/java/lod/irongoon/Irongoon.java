package lod.irongoon;

import com.github.slugify.Slugify;
import legend.core.GameEngine;
import legend.game.characters.Element;
import legend.game.inventory.ItemStack;
import legend.game.modding.events.battle.BattleMusicEvent;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.inventory.GiveItemEvent;
import legend.game.modding.events.inventory.ShopContentsEvent;
import legend.game.modding.events.submap.SubmapGenerateEncounterEvent;
import legend.game.modding.events.submap.SubmapWarpEvent;
import legend.game.saves.*;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.config.SeedConfigEntry;
import lod.irongoon.data.EnableAllCharacters;
import lod.irongoon.services.StaleStats;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;
import org.legendofdragoon.modloader.Mod;
import legend.game.modding.events.characters.CharacterStatsEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;

import lod.irongoon.models.DivineFruit;
import lod.irongoon.services.randomizer.Randomizer;
import lod.irongoon.services.Characters;
import lod.irongoon.services.DataTables;

import java.util.List;
import java.util.Random;
import java.util.stream.StreamSupport;

import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.combat.Battle.characterElements_800c706c;

@Mod(id = Irongoon.MOD_ID, version = "^3.0.0")
public class Irongoon {
    public static final String MOD_ID = "irongoon";
    private static final Slugify slug = Slugify.builder().underscoreSeparator(true).customReplacement("'", "").customReplacement("-", "_").build();
    public static RegistryId id(final String entryId) {
        return new RegistryId(MOD_ID, entryId);
    }

    private static final IrongoonConfig config = IrongoonConfig.getInstance();
    private static final Randomizer randomizer = Randomizer.getInstance();
    private static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> CONFIG_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.config, MOD_ID);
    private static final RegistryDelegate<SeedConfigEntry> IRONGOON_CAMPAIGN_SEED = CONFIG_REGISTRAR.register("irongoon_campaign_seed", () -> new SeedConfigEntry(randomizer.retrieveNewCampaignSeed()));

    private final Characters characters = Characters.getInstance();
    private final DataTables dataTables = DataTables.getInstance();
    private final StaleStats staleStats = StaleStats.getInstance();

    private final RegistryDelegate<Element>[] characterElementsUnmodified = characterElements_800c706c.clone();

    public Irongoon() {
        GameEngine.EVENTS.register(this);
    }

    @EventListener
    public void gameConfig(final ConfigRegistryEvent event) {
        CONFIG_REGISTRAR.registryEvent(event);
    }

    @EventListener
    public void newGame(final NewGameEvent game) {
        if (config.useRandomSeedOnNewCampaign) {
            config.publicSeed = config.campaignSeed;
        }

        randomizer.setLevelOneParty(game.gameState);
    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        config.regenerateConfig();

        if (config.useRandomSeedOnNewCampaign) {
            config.publicSeed = GameEngine.CONFIG.getConfig(IRONGOON_CAMPAIGN_SEED.get());
            config.seed = Long.parseLong(config.publicSeed, 16);
        }

        refreshState();
    }

    private void refreshState() {
        characters.initialize();
        dataTables.initialize();
    }

    @EventListener
    public void submapWarp(final SubmapWarpEvent game) {
        randomizer.enableAllCharacters(game);
    }

    @EventListener
    public void characterStats(final CharacterStatsEvent character) {
        if (!staleStats.isCharacterStale(character)) {
            characters.updateCharacterByReferenceCharacter(character);
            return;
        }

        DivineFruit bodyStatsRandomized = randomizer.doCharacterStats(character);
        DivineFruit dragoonStatsRandomized = randomizer.doDragoonStats(character);
        DivineFruit hpStatRandomized = randomizer.doCharacterHP(character);
        DivineFruit speedStatRandomized = randomizer.doCharacterSpeed(character);

        character.bodyAttack = bodyStatsRandomized.bodyAttack;
        character.bodyDefence = Math.max(1, bodyStatsRandomized.bodyDefense);
        character.bodyMagicAttack = bodyStatsRandomized.bodyMagicAttack;
        character.bodyMagicDefence =Math.max(1,  bodyStatsRandomized.bodyMagicDefense);

        character.dragoonAttack = dragoonStatsRandomized.dragoonAttack;
        character.dragoonDefence = Math.max(1, dragoonStatsRandomized.dragoonDefense);
        character.dragoonMagicAttack = dragoonStatsRandomized.dragoonMagicAttack;
        character.dragoonMagicDefence = Math.max(1, dragoonStatsRandomized.dragoonMagicDefense);

        character.maxHp = hpStatRandomized.maxHP;
        character.bodySpeed = speedStatRandomized.bodySpeed;

        characters.saveCharacter(character);
    }

    @EventListener
    public void monsterStats(final MonsterStatsEvent monster) {
        DivineFruit monsterStatsRandomized = randomizer.doMonsterStats(monster);
        DivineFruit monsterHPRandomized = randomizer.doMonsterHP(monster);
        DivineFruit monsterSpeedRandomized = randomizer.doMonsterSpeed(monster);
        DivineFruit monsterElementRandomized = randomizer.doMonsterElement(monster);

        randomizer.doMonsterVariance(monsterStatsRandomized, monsterHPRandomized, monsterSpeedRandomized);

        monster.attack = monsterStatsRandomized.bodyAttack;
        monster.defence = Math.max(1, monsterStatsRandomized.bodyDefense);
        monster.magicAttack = monsterStatsRandomized.bodyMagicAttack;
        monster.magicDefence = Math.max(1, monsterStatsRandomized.bodyMagicDefense);

        monster.maxHp = monsterHPRandomized.maxHP;
        monster.hp = monsterHPRandomized.maxHP;
        monster.speed = monsterSpeedRandomized.bodySpeed;

        monster.elementFlag = Element.fromFlag(monsterElementRandomized.element.getValue()).get();
        monster.elementalImmunityFlag.set(monsterElementRandomized.elementImmunity);
    }

    @EventListener
    public void stageData(final BattleMusicEvent stage) {
        stage.musicIndex = randomizer.doMusic(stage.musicIndex);
        // stage.victoryType = randomizer.doVictory(stage.victoryIndex);

        final var randomizedElements = randomizer.doCharacterElement(characterElementsUnmodified);
        for (int i = 0; i < randomizedElements.length; i++) {
            characterElements_800c706c[i] = randomizedElements[i];
        }
    }

    public void stageEscapeChance() {
        // escapeChance_08 = randomizer.doEscapeChance()
    }

    @EventListener
    public void encounterData(final SubmapGenerateEncounterEvent encounter) {
        if(encounter.encounterId == 431) return;
        var submapId = submapCut_80052c30;
        encounter.battleStageId = randomizer.doBattleStage(encounter.battleStageId, encounter.encounterId, submapId);

        final var randomizedBattleParty = randomizer.doBattleParty(gameState_800babc8.charData_32c, gameState_800babc8.charIds_88);
        for(int i = 0; i < gameState_800babc8.charIds_88.length; i++) {
            gameState_800babc8.charIds_88[i] = randomizedBattleParty[i];
        }
    }
    
    @EventListener
    public void shopData(final ShopContentsEvent shop) {
        final var shopQuantity = randomizer.doShopQuantity(shop.shop, shop.contents);
        final var shopContents = randomizer.doShopContents(shop.shop, shop.contents, shopQuantity); // can handle generating uniqueInventoryEntries later
        final var randomizedContents = randomizer.doShopAvailability(shop.shop, shopContents);

        shop.contents.clear();
        shop.contents.addAll(randomizedContents);
    }

    @EventListener
    public void giveItem(final GiveItemEvent event) {
        final List<ItemStack> inventoryItems = StreamSupport
                .stream(event.inventory.spliterator(), false)
                .toList();

        final List<ItemStack> limitedItems = randomizer.doItemCarryingLimit(inventoryItems, event.givenItems);

        event.givenItems.clear();
        event.givenItems.addAll(limitedItems);
    }
}
