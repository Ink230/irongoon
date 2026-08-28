package lod.irongoon;

import com.github.slugify.Slugify;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import legend.core.GameEngine;
import legend.game.characters.CharacterData2c;
import legend.game.characters.Element;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.inventory.EquipmentRegistryEvent;
import legend.game.inventory.GatherEquipmentTypesEvent;
import legend.game.inventory.ItemStack;
import legend.game.modding.events.battle.BattleEntityTurnEvent;
import legend.game.modding.events.battle.BattleEndedEvent;
import legend.game.modding.events.battle.BattleMusicEvent;
import legend.game.modding.events.battle.BattleStartedEvent;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.battle.ResolvePhysicalAttackElementsEvent;
import legend.game.modding.events.battle.ResolvePhysicalAttackStatusEvent;
import legend.game.modding.events.battle.ResolveSpellDescriptionEvent;
import legend.game.modding.events.battle.SpellStatsEvent;
import legend.game.modding.events.characters.AdditionUnlockEvent;
import legend.game.modding.events.characters.PostCharacterDragoonLevelUpEvent;
import legend.game.modding.events.characters.PostCharacterLevelUpEvent;
import legend.game.modding.events.characters.PreCharacterDragoonLevelUpEvent;
import legend.game.modding.events.characters.PreCharacterLevelUpEvent;
import legend.game.modding.events.characters.ResolveCharacterElementEvent;
import legend.game.modding.events.characters.ResolveAdditionEvent;
import legend.game.modding.events.characters.ResolveCharacterAdditionSaveEvent;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.gamestate.EncounterEvent;
import legend.game.modding.events.config.NewCampaignConfigEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.gamestate.PartyFlagsChangeEvent;
import legend.game.modding.events.gamestate.PrimaryPartyChangeEvent;
import legend.game.modding.events.inventory.GiveItemEvent;
import legend.game.modding.events.inventory.ShopContentsEvent;
import legend.game.modding.events.inventory.TakeGoodsEvent;
import legend.game.modding.events.submap.SubmapEncounterEvent;
import legend.game.modding.events.submap.SubmapWarpEvent;
import legend.game.modding.events.worldmap.WorldMapEncounterEvent;
import legend.game.types.GameState52c;
import legend.game.saves.*;
import lod.irongoon.config.IrongoonCampaignConfig;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.config.IrongoonSnapshotConfigEntry;
import lod.irongoon.config.SeedConfigEntry;
import lod.irongoon.registries.IrongoonEquipment;
import lod.irongoon.services.Additions;
import lod.irongoon.services.DragoonSpells;
import lod.irongoon.services.DragoonUnlocks;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.events.Priority;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;
import org.legendofdragoon.modloader.Mod;
import legend.game.modding.events.gamestate.GameLoadedEvent;

import lod.irongoon.models.DivineFruit;
import lod.irongoon.services.randomizer.Randomizer;
import lod.irongoon.services.DataTables;
import lod.irongoon.services.data.SeveredChainsLiveDataAdapter;

import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;

@Mod(id = Irongoon.MOD_ID, version = "^3.0.0")
public class Irongoon {
  public static final String MOD_ID = "irongoon";
    private static final Slugify slug = Slugify.builder().underscoreSeparator(true).customReplacement("'", "").customReplacement("-", "_").build();
    public static RegistryId id(final String entryId) {
        return new RegistryId(MOD_ID, entryId);
    }

    private static final IrongoonConfig config = IrongoonConfig.getInstance();
    private static final Randomizer randomizer = Randomizer.getInstance();
    private static final Logger LOGGER = LogManager.getFormatterLogger(Irongoon.class);
    private static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> CONFIG_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.config, MOD_ID);
    private static final RegistryDelegate<SeedConfigEntry> IRONGOON_CAMPAIGN_SEED = CONFIG_REGISTRAR.register("irongoon_campaign_seed", () -> new SeedConfigEntry(randomizer.retrieveNewCampaignSeed()));
    private static final RegistryDelegate<StringConfigEntry> IRONGOON_LAST_SELECTED_PROFILE = CONFIG_REGISTRAR.register("irongoon_last_selected_profile", () -> new StringConfigEntry("", 2, ConfigStorageLocation.GLOBAL, ConfigCategory.OTHER));
    private static final RegistryDelegate<IrongoonSnapshotConfigEntry> IRONGOON_CONFIG_SNAPSHOT = CONFIG_REGISTRAR.register("irongoon_config_snapshot", () -> new IrongoonSnapshotConfigEntry(IRONGOON_CAMPAIGN_SEED.get(), IRONGOON_LAST_SELECTED_PROFILE.get()));

    private final DataTables dataTables = DataTables.getInstance();
    private final Additions additions = Additions.getInstance();
    private final DragoonSpells dragoonSpells = DragoonSpells.getInstance();
    private final DragoonUnlocks dragoonUnlocks = DragoonUnlocks.getInstance();
    private final SeveredChainsLiveDataAdapter liveData = SeveredChainsLiveDataAdapter.getInstance();
    private final IrongoonCampaignConfig campaignConfig = IrongoonCampaignConfig.getInstance();

    public Irongoon() {
        GameEngine.EVENTS.register(this);
    }

    @EventListener
    public void gameConfig(final ConfigRegistryEvent event) {
        CONFIG_REGISTRAR.registryEvent(event);
    }

    @EventListener
    public void newCampaignConfig(final NewCampaignConfigEvent event) {
        this.campaignConfig.stageNewCampaign(
            event.configCollection,
            IRONGOON_CONFIG_SNAPSHOT.get(),
            IRONGOON_LAST_SELECTED_PROFILE.get(),
            event.rememberDefaults
        );
    }

    @EventListener(priority = Priority.LOW)
    public void newGame(final NewGameEvent game) {
        this.campaignConfig.applyNewCampaign(
            GameEngine.CONFIG,
            IRONGOON_CONFIG_SNAPSHOT.get(),
            IRONGOON_LAST_SELECTED_PROFILE.get(),
            GameEngine.CONFIG.getConfig(CoreMod.REMEMBER_CAMPAIGN_SETTINGS_CONFIG.get())
        );

        if (config.useRandomSeedOnNewCampaign) {
            config.publicSeed = GameEngine.CONFIG.getConfig(IRONGOON_CAMPAIGN_SEED.get());
            config.seed = Long.parseLong(config.publicSeed, 16);
        }

        refreshState();
        additions.initializeCampaign(game.gameState);
        randomizer.setLevelOneParty(game.gameState);
        this.dragoonUnlocks.initializeCampaign(game.gameState.goods_19c);
        this.initializeDragoonSpells(game.gameState);
        randomizer.resetDragoonElements();

    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        final IrongoonCampaignConfig.SelectionResult selection = this.campaignConfig.applyLoadedCampaign(
            GameEngine.CONFIG,
            IRONGOON_CONFIG_SNAPSHOT.get(),
            IRONGOON_LAST_SELECTED_PROFILE.get(),
            GameEngine.CONFIG.getConfig(CoreMod.REMEMBER_CAMPAIGN_SETTINGS_CONFIG.get())
        );
        if(selection.migrated()) {
            ConfigStorage.saveConfig(GameEngine.CONFIG, ConfigStorageLocation.CAMPAIGN, game.gameState.campaign.path.resolve("campaign_config.dcnf"));
            LOGGER.info("Baked Irongoon campaign configuration migrated from %s", selection.sourceProfileId());
        }

        if (config.useRandomSeedOnNewCampaign) {
            config.publicSeed = GameEngine.CONFIG.getConfig(IRONGOON_CAMPAIGN_SEED.get());
            config.seed = Long.parseLong(config.publicSeed, 16);
        }

        randomizer.resetCharacterElements();
        randomizer.resetDragoonElements();
        refreshState();
        additions.initializeCampaign(game.gameState);
        randomizer.reapplyAllCharacterStats(game.gameState);
        this.initializeDragoonSpells(game.gameState);
    }

    private void initializeDragoonSpells(final GameState52c gameState) {
        this.dragoonSpells.gatherProfiles();
        for(final CharacterData2c character : gameState.charData_32c) {
            randomizer.doDragoonSpellUnlocks(character, this.dragoonSpells::isProfiled, this.dragoonSpells::isUsableAsFirstSpell);
        }
        this.dragoonSpells.initialize(gameState);
    }

    @EventListener
    public void takeGoods(final TakeGoodsEvent event) {
        this.dragoonUnlocks.preservePermanentUnlocks(event.takenGoods);
    }

    @EventListener
    public void spellStats(final SpellStatsEvent event) {
        event.spell = this.dragoonSpells.resolve(event.character, event.spellId, event.baseSpell);
    }

    @EventListener
    public void resolveSpellDescription(final ResolveSpellDescriptionEvent event) {
        event.description = this.dragoonSpells.describe(event.character, event.spellId, event.spell, event.baseDescription);
    }

    private void refreshState() {
        dataTables.initialize();
        additions.initialize();
    }

    @EventListener
    public void submapWarp(final SubmapWarpEvent game) {
        randomizer.enableAllCharacters(game);
    }

    @EventListener
    public void partyFlagsChange(final PartyFlagsChangeEvent event) {
        final int currentPartyFlags = event.gameState.charData_32c.get(event.characterIndex).partyFlags_04;
        event.partyFlags = randomizer.doPartyFlags(currentPartyFlags, event.partyFlags);
    }

    @EventListener
    public void primaryPartyChange(final PrimaryPartyChangeEvent event) {
        event.characterIndex = randomizer.doPrimaryPartyChange(
                event.gameState.charData_32c,
                event.activePartySlot,
                event.characterIndex
        );
    }

    @EventListener
    public void characterLevelUp(final PostCharacterLevelUpEvent event) {
        if (!this.dataTables.isInitialized()) return;

        final int characterId = getCharacterId(event.character);
        if(characterId < 0) return;

        randomizer.applyCharacterStats(event.character, characterId);
        additions.unlockEligibleAdditions(event.character);

    }

    @EventListener(priority = Priority.LOWEST)
    public void ingestCharacterLevelUp(final PreCharacterLevelUpEvent event) {
        this.liveData.updateCharacterStats(event);
    }

    @EventListener
    public void characterDragoonLevelUp(final PostCharacterDragoonLevelUpEvent event) {
        if (!this.dataTables.isInitialized()) return;

        final int characterId = getCharacterId(event.character);
        if(characterId < 0) return;

        randomizer.applyDragoonStats(event.character, characterId);
    }

    @EventListener(priority = Priority.LOWEST)
    public void ingestCharacterDragoonLevelUp(final PreCharacterDragoonLevelUpEvent event) {
        this.liveData.updateDragoonStats(event);
    }

  private int getCharacterId(final CharacterData2c character) {
    return character.gameState.charData_32c.indexOf(character);
  }

  @EventListener
  public void resolveCharacterElement(final ResolveCharacterElementEvent event) {
    final int characterId = getCharacterId(event.character);

    if(event.bent != null && event.bent.isDragoon()) {
      event.element = randomizer.doDragoonElement(characterId, event.bent, event.baseElement);
      return;
    }

        if(event.bent != null) {
            randomizer.synchronizeDragoonElementState(event.bent);
            this.dragoonSpells.synchronize(event.bent);
        }
    event.element = randomizer.doCharacterElement(characterId, event.baseElement);
  }

    @EventListener(priority = Priority.LOWEST)
    public void monsterStats(final MonsterStatsEvent monster) {
        this.liveData.updateMonsterStats(monster);

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
    randomizer.beginCharacterElementBattle();
    stage.musicIndex = randomizer.doMusic(stage.musicIndex);
        // stage.victoryType = randomizer.doVictory(stage.victoryIndex);

    }

    public void stageEscapeChance() {
        // escapeChance_08 = randomizer.doEscapeChance()
    }

    @EventListener
  public void battleStarted(final BattleStartedEvent event) {
    randomizer.beginDragoonElementBattle();
    this.dragoonSpells.beginBattle();
  }

  @EventListener
  public void battleEnded(final BattleEndedEvent event) {
    randomizer.endDragoonElementBattle();
    this.dragoonSpells.endBattle();
  }

  @EventListener
  public void battleEntityTurn(final BattleEntityTurnEvent<?> event) {
    if(event.bent instanceof final PlayerBattleEntity player) {
      randomizer.synchronizeDragoonElementState(player);
      this.dragoonSpells.synchronize(player);
    }
  }

    @EventListener
    public void submapEncounterData(final SubmapEncounterEvent event) {
        processEncounter(event, submapCut_80052c30);
    }

    @EventListener
    public void worldmapEncounterData(final WorldMapEncounterEvent event) {
        processEncounter(event, event.directionalPathSegment.pathSegmentIndexAndDirection_00);
    }

    private void processEncounter(final EncounterEvent event, final int mapIdentifierId) {
        var encounterEntryId = event.encounter.getRegistryId().entryId().toString();

        if(encounterEntryId.equals("zackwell_lavitzs_spirit")) return;

        var encounterUniqueId = encounterEntryId.hashCode();
        var gameState = event.getGameState();

        event.battleStageId = randomizer.doBattleStage(event.battleStageId, encounterUniqueId, mapIdentifierId);

        var charIds = gameState.charIds_88;
        var randomizedBattleParty = new IntArrayList(randomizer.doBattleParty(gameState.charData_32c, charIds));
        charIds.clear();
        charIds.addAll(randomizedBattleParty);
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

    @EventListener
    public void additionUnlock(final AdditionUnlockEvent addition) {
        if(this.additions.usesRandomizedUnlock(addition.charData, addition.addition.getRegistryId())) return;

        var additionIdentifier = addition.addition.getRegistryId().entryId();
        var additionUnlockLevel = additions.getUnlockLevelByName(additionIdentifier);
        if(addition.charData.level_12 < additionUnlockLevel) {
            addition.cancel();
        }
    }

    @EventListener
    public void resolveAddition(final ResolveAdditionEvent event) {
        event.addition = this.additions.resolve(event.character, event.additionId, event.baseAddition);
    }

    @EventListener
    public void resolveCharacterAdditionSave(final ResolveCharacterAdditionSaveEvent event) {
        final Additions.CharacterAdditionSaveState state = this.additions.getCampaignAdditionSaveState(event.character);
        if(state != null) event.resolve(state.selectedAddition(), state.additions());
    }

    @EventListener
    public void resolvePhysicalAttackElements(final ResolvePhysicalAttackElementsEvent event) {
        if(event.attacker.selectedAddition_58 == null) return;
        final Element additionElement = this.additions.resolveElement(event.attacker.selectedAddition_58);
        if(additionElement != null) event.elements.add(additionElement);
    }

    @EventListener
    public void resolvePhysicalAttackStatus(final ResolvePhysicalAttackStatusEvent event) {
        if(!event.additionCompletedSuccessfully || event.additionId == null || event.baseStatusMask != 0) return;
        final var assignment = this.additions.resolveStatus(event.additionId);
        if(assignment == null) return;
        event.statusMask = assignment.statusMask();
        event.chance = assignment.chance();
    }

    @EventListener
    public void registerEquipment(final EquipmentRegistryEvent event) {
        IrongoonEquipment.register(event);
    }

    @EventListener
    public void gatherEquipmentTypes(final GatherEquipmentTypesEvent event) {
        IrongoonEquipment.registerEquipmentTypes(event);
    }
}
