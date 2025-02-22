package lod.irongoon;

import com.github.slugify.Slugify;
import legend.core.GameEngine;
import legend.game.characters.Element;
import legend.game.inventory.InventoryEntry;
import legend.game.inventory.screens.ShopScreen;
import legend.game.modding.events.battle.BattleEncounterStageDataEvent;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.inventory.ShopContentsEvent;
import legend.game.modding.events.submap.SubmapGenerateEncounterEvent;
import legend.game.saves.*;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.config.SeedConfigEntry;
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

import java.util.ArrayList;
import java.util.Random;

import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_800b.encounterId_800bb0f8;

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
    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
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
        character.bodyDefence = bodyStatsRandomized.bodyDefense;
        character.bodyMagicAttack = bodyStatsRandomized.bodyMagicAttack;
        character.bodyMagicDefence = bodyStatsRandomized.bodyMagicDefense;

        character.dragoonAttack = dragoonStatsRandomized.dragoonAttack;
        character.dragoonDefence = dragoonStatsRandomized.dragoonDefense;
        character.dragoonMagicAttack = dragoonStatsRandomized.dragoonMagicAttack;
        character.dragoonMagicDefence = dragoonStatsRandomized.dragoonMagicDefense;

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
        monster.defence = monsterStatsRandomized.bodyDefense;
        monster.magicAttack = monsterStatsRandomized.bodyMagicAttack;
        monster.magicDefence = monsterStatsRandomized.bodyMagicDefense;

        monster.maxHp = monsterHPRandomized.maxHP;
        monster.hp = monsterHPRandomized.maxHP;
        monster.speed = monsterSpeedRandomized.bodySpeed;

        monster.elementFlag = Element.fromFlag(monsterElementRandomized.element.getValue()).get();
        monster.elementalImmunityFlag.set(monsterElementRandomized.elementImmunity);
    }

    @EventListener
    public void stageData(final BattleEncounterStageDataEvent stage) {
        var stageData = stage.stageData;
        var submapId = submapCut_80052c30;
        var encounterId = encounterId_800bb0f8;
        int[] musicNumbers = {0, 1, 2, 16, 17, 18, 19}; // valid music indices

        Random random = new Random();
        stageData.musicIndex_04 = musicNumbers[random.nextInt(musicNumbers.length)];
        stageData.escapeChance_08 = randomizer.doEscapeChance(stageData.escapeChance_08, encounterId, submapId);
    }

    @EventListener
    public void encounterData(final SubmapGenerateEncounterEvent encounter) {
        if(encounter.encounterId == 431) return;
        var submapId = submapCut_80052c30;
        encounter.battleStageId = randomizer.doBattleStage(encounter.battleStageId, encounter.encounterId, submapId);

    }
    
    @EventListener
    public void shopData(final ShopContentsEvent shop) {
        final var shopQuantity = randomizer.doShopQuantity(shop.shop, shop.contents);
        final var shopContents = randomizer.doShopContents(shop.shop, shop.contents, shopQuantity); // can handle generating uniqueInventoryEntries later
        final var randomizedContents = randomizer.doShopAvailability(shop.shop, shopContents);

        shop.contents.clear();
        shop.contents.addAll(randomizedContents);
    }

    // public void canPurchase(final ShopPurchaseEvent invoice)
        // Carried, TimesBoughtFromShop, TimesBoughtInCampaign, Character, ShopStockLevel
        // Can all be used to dictate if you can buy what is in the shop
        // BUY_4 in ShopScreen in input handlers
}
