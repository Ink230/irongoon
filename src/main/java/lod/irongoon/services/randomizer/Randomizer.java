package lod.irongoon.services.randomizer;


import it.unimi.dsi.fastutil.ints.IntList;
import legend.game.characters.CharacterData2c;
import legend.game.characters.Element;
import legend.game.inventory.Equipment;
import legend.game.inventory.InventoryEntry;
import legend.game.inventory.Item;
import legend.game.inventory.ItemStack;
import legend.game.inventory.screens.ShopScreen;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.submap.SubmapWarpEvent;
import legend.game.types.EquipmentSlot;
import legend.game.types.GameState52c;
import legend.game.types.Shop;
import legend.lodmod.LodEquipment;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.BattleParty;
import lod.irongoon.data.EnableAllCharacters;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.registries.IrongoonEquipment;
import java.util.*;
import java.util.stream.Collectors;

import static legend.lodmod.LodMod.ATTACK_STAT;
import static legend.lodmod.LodMod.DEFENSE_STAT;
import static legend.lodmod.LodMod.DRAGOON_ATTACK_STAT;
import static legend.lodmod.LodMod.DRAGOON_DEFENSE_STAT;
import static legend.lodmod.LodMod.DRAGOON_MAGIC_ATTACK_STAT;
import static legend.lodmod.LodMod.DRAGOON_MAGIC_DEFENSE_STAT;
import static legend.lodmod.LodMod.HP_STAT;
import static legend.lodmod.LodMod.MAGIC_ATTACK_STAT;
import static legend.lodmod.LodMod.MAGIC_DEFENSE_STAT;
import static legend.lodmod.LodMod.MP_STAT;
import static legend.lodmod.LodMod.SPEED_STAT;

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
  private final CharacterElementRandomizer characterElementRandomizer = CharacterElementRandomizer.getInstance();
    private final MonsterStatsRandomizer monsterStatsRandomizer = MonsterStatsRandomizer.getInstance();
    private final MonsterHPRandomizer monsterHPRandomizer = MonsterHPRandomizer.getInstance();
    private final MonsterSpeedRandomizer monsterSpeedRandomizer = MonsterSpeedRandomizer.getInstance();
    private final MonsterElementRandomizer monsterElementRandomizer = MonsterElementRandomizer.getInstance();
    private final BattleStageRandomizer battleStageRandomizer = BattleStageRandomizer.getInstance();
    private final EscapeChanceRandomizer escapeChanceRandomizer = EscapeChanceRandomizer.getInstance();
    private final ShopAvailabilityRandomizer shopAvailabilityRandomizer = ShopAvailabilityRandomizer.getInstance();
    private final ShopQuantityRandomizer shopQuantityRandomizer = ShopQuantityRandomizer.getInstance();
    private final ShopContentsRandomizer shopContentsRandomizer = ShopContentsRandomizer.getInstance();
    private final BattlePartyRandomizer battlePartyRandomizer = BattlePartyRandomizer.getInstance();

    public static String retrieveNewCampaignSeed() {
        config.campaignSeed = seedRandomizer.generateNewSeed();
        return config.campaignSeed;
    }

    public DivineFruit doCharacterStats(final int characterId, final int level) {
        return switch (config.bodyTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    characterStatsRandomizer.randomizeWithBounds(characterId, level);
            case MAINTAIN_STOCK ->
                    characterStatsRandomizer.randomizeMaintainStock(characterId, level);
            case AVERAGE_ALL_CHARACTERS ->
                    characterStatsRandomizer.randomizeAverage(characterId, level);
            case STOCK -> characterStatsRandomizer.stock(characterId, level);
        };
    }

    public DivineFruit doCharacterHP(final int characterId, final int level) {
        return switch (config.hpStatPerLevel) {
            case MAINTAIN_STOCK -> characterHPRandomizer.randomizeMaintainStock(characterId, level);
            case RANDOMIZE_BOUNDS_PER_LEVEL -> characterHPRandomizer.randomizeWithBounds(characterId, level);
            case RANDOMIZE_STOCK_BOUNDS -> characterHPRandomizer.randomizeStockWithBounds(characterId, level);
            case RANDOMIZE_RANDOM_STOCK_BOUNDS -> characterHPRandomizer.randomizeRandomStockWithBounds(characterId, level);
            case RANDOMIZE_BOUNDS_PERCENT_MODIFIED_PER_LEVEL -> characterHPRandomizer.randomizeWithBoundsAndPercentModifiers(characterId, level);
        };
    }

    public DivineFruit doCharacterSpeed(final int characterId, final int level) {
        return switch(config.speedStatPerLevel) {
            case MAINTAIN_STOCK -> characterSpeedRandomizer.randomizeMaintainStock(characterId, level);
            case RANDOMIZE_BOUNDS -> characterSpeedRandomizer.randomizeWithBounds(characterId, level);
            case RANDOMIZE_RANDOM_BOUNDS -> characterSpeedRandomizer.randomizeStockWithBounds(characterId, level);
        };
    }

    public DivineFruit doDragoonStats(final int characterId, final int dlevel) {
        return switch (config.dragoonTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS_PER_LEVEL ->
                    dragoonStatsRandomizer.randomizeWithBounds(characterId, dlevel);
            case MAINTAIN_STOCK -> dragoonStatsRandomizer.randomizeMaintainStock(characterId, dlevel);
            case AVERAGE_ALL_CHARACTERS -> dragoonStatsRandomizer.randomizeAverage(characterId, dlevel);
            case STOCK -> dragoonStatsRandomizer.stock(characterId, dlevel);
        };
    }

    public void applyCharacterStats(final CharacterData2c character, final int characterId) {
        final DivineFruit bodyStats = doCharacterStats(characterId, character.level_12);
        final DivineFruit hpStat = doCharacterHP(characterId, character.level_12);
        final DivineFruit speedStat = doCharacterSpeed(characterId, character.level_12);

        character.stats.getStat(ATTACK_STAT.get()).setRaw(bodyStats.bodyAttack);
        character.stats.getStat(DEFENSE_STAT.get()).setRaw(Math.max(1, bodyStats.bodyDefense));
        character.stats.getStat(MAGIC_ATTACK_STAT.get()).setRaw(bodyStats.bodyMagicAttack);
        character.stats.getStat(MAGIC_DEFENSE_STAT.get()).setRaw(Math.max(1, bodyStats.bodyMagicDefense));
        character.stats.getStat(HP_STAT.get()).setMaxRaw(hpStat.maxHP);
        character.stats.getStat(SPEED_STAT.get()).setRaw(speedStat.bodySpeed);
    }

    public void applyDragoonStats(final CharacterData2c character, final int characterId) {
        final DivineFruit dragoonStats = doDragoonStats(characterId, character.dlevel_13);

        character.stats.getStat(DRAGOON_ATTACK_STAT.get()).setRaw(dragoonStats.dragoonAttack);
        character.stats.getStat(DRAGOON_DEFENSE_STAT.get()).setRaw(Math.max(1, dragoonStats.dragoonDefense));
        character.stats.getStat(DRAGOON_MAGIC_ATTACK_STAT.get()).setRaw(dragoonStats.dragoonMagicAttack);
        character.stats.getStat(DRAGOON_MAGIC_DEFENSE_STAT.get()).setRaw(Math.max(1, dragoonStats.dragoonMagicDefense));
        character.stats.getStat(MP_STAT.get()).setMaxRaw(dragoonStats.maxMP);
    }

  public void reapplyAllCharacterStats(final GameState52c gameState) {
    for(int characterId = 0; characterId < gameState.charData_32c.size(); characterId++) {
      final CharacterData2c character = gameState.charData_32c.get(characterId);
      applyCharacterStats(character, characterId);
      applyDragoonStats(character, characterId);

      character.stats.getStat(HP_STAT.get()).getCurrent();
      character.stats.getStat(MP_STAT.get()).getCurrent();
    }
  }

  public Element doCharacterElement(final int characterId, final Element baseElement) {
    final var element = this.characterElementRandomizer.resolve(characterId);
    return element == null ? baseElement : element.get();
  }

  public void resetCharacterElements() {
    this.characterElementRandomizer.reset();
  }

  public void beginCharacterElementBattle() {
    this.characterElementRandomizer.beginBattle();
  }

  public DivineFruit doMonsterStats(MonsterStatsEvent monster) {
        return switch (config.monsterTotalStatsPerLevel) {
            case RANDOMIZE_BOUNDS -> monsterStatsRandomizer.randomizeWithBounds(monster.enemyId);
            case MAINTAIN_STOCK -> monsterStatsRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_STOCK_BOUNDS -> monsterStatsRandomizer.randomizeStockWithPercentBounds(monster.enemyId);
        };
    }

    public DivineFruit doMonsterHP(MonsterStatsEvent monster){
        return switch (config.hpStatMonsters) {
            case MAINTAIN_STOCK -> monsterHPRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_BOUNDS -> monsterHPRandomizer.randomizeStockWithBounds(monster.enemyId);
        };
    }

    public DivineFruit doMonsterSpeed(MonsterStatsEvent monster) {
        return switch (config.speedStatMonsters) {
            case MAINTAIN_STOCK -> monsterSpeedRandomizer.randomizeMaintainStock(monster.enemyId);
            case RANDOMIZE_BOUNDS -> monsterSpeedRandomizer.randomizeWithBounds(monster.enemyId);
            case RANDOMIZE_RANDOM_BOUNDS -> monsterSpeedRandomizer.randomizeRandomWithBounds();
        };
    }

    public DivineFruit doMonsterElement(MonsterStatsEvent monster) {
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

    public int doBattleStage(final int battleStageId, final int encounterId, final int submapId) {
        return switch (config.battleStage) {
            case STOCK -> battleStageRandomizer.maintainStock(battleStageId);
            case RANDOM -> battleStageRandomizer.randomRandom();
            case RANDOM_FIXED_ENCOUNTER -> battleStageRandomizer.randomFixed(encounterId + 646);
            case RANDOM_FIXED_SUBMAP -> battleStageRandomizer.randomFixed(submapId + 293);
        };
    }

    public int doMusic(final int currentMusicIndex) {
        final int[] musicNumbers = {702, 707, 712, 717, 722, 727, 732};
        final var random = new Random();

        return switch(config.battleMusic) {
            case STOCK -> currentMusicIndex;
            case RANDOM -> musicNumbers[random.nextInt(musicNumbers.length)];
        };
    }

    public int doEscapeChance(final int escapeChance, final int encounterId, final int submapId) {
        return switch (config.escapeChance) {
            case STOCK -> escapeChanceRandomizer.maintainStock(escapeChance);
            case RANDOMIZE_BOUNDS -> escapeChanceRandomizer.randomizeBounds();
            case RANDOMIZE_BOUNDS_FIXED_ENCOUNTER -> escapeChanceRandomizer.randomizeBoundsFixed(encounterId);
            case RANDOMIZE_BOUNDS_FIXED_SUBMAP -> escapeChanceRandomizer.randomizeBoundsFixed(submapId);
            case NO_ESCAPE -> 0;
            case COWARD -> 100;
        };
    }

    public int doShopQuantity(final Shop shop, final List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents) {
        return switch (config.shopQuantity) {
            case STOCK -> shopQuantityRandomizer.maintainStock(contents);
            case RANDOMIZE_BOUNDS -> shopQuantityRandomizer.randomBounds(shop);
        };
    }

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> doShopAvailability(final Shop shop, final List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents) {
        return switch (config.shopAvailability) {
            case STOCK -> shopAvailabilityRandomizer.maintainStock(contents);
            case RANDOM -> shopAvailabilityRandomizer.random(shop, contents);
            case NO_SHOPS -> shopAvailabilityRandomizer.noShops();
            case NO_ITEMS -> shopAvailabilityRandomizer.noItemsInShops(contents);
            case NO_EQUIPMENT -> shopAvailabilityRandomizer.noEquipmentInShops(contents);
        };
    }

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> doShopContents(final Shop shop, final List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents, final int shopQuantity) {
        final var preparedContents = shopContentsRandomizer.prepareContents(shop, contents, shopQuantity);

        final var randomizedContents = switch (config.shopContents) {
            case STOCK -> shopContentsRandomizer.maintainStock(shop, preparedContents);
            case RANDOMIZE_ITEMS -> shopContentsRandomizer.randomizeItems(shop, preparedContents);
            case RANDOMIZE_EQUIPMENT -> shopContentsRandomizer.randomizeEquipment(shop, preparedContents);
            case RANDOMIZE_ALL -> shopContentsRandomizer.randomizeAll(shop, preparedContents);
            case RANDOMIZE_ALL_MIXED -> shopContentsRandomizer.randomizeAllMixed(shop, preparedContents);
        };

        return shopContentsRandomizer.processContents(shop, randomizedContents);
    }

    public List<ItemStack> doItemCarryingLimit(List<ItemStack> inventory, List<ItemStack> givenItems) {
        final var preparedItems = new ArrayList<>(givenItems);
        if (config.itemCarryLimit == 0) return new ArrayList<>(preparedItems);

        final Map<Item, Integer> heldItemsCount = inventory.stream()
                .filter(Objects::nonNull)
                .filter(stack -> !stack.isEmpty())
                .collect(Collectors.toMap(
                        ItemStack::getItem,
                        ItemStack::getSize,
                        Integer::sum
                ));

        final List<ItemStack> allowed = new ArrayList<>();

        for (ItemStack stack : preparedItems) {
            if (stack == null || stack.isEmpty()) continue;

            final Item item = stack.getItem();
            final int currentCount = heldItemsCount.getOrDefault(item, 0);
            final int limit = config.itemCarryLimit;

            if (currentCount >= limit) {
                continue;
            }

            int remaining = limit - currentCount;
            if (stack.getSize() <= remaining) {
                allowed.add(stack);
                heldItemsCount.put(item, currentCount + stack.getSize());
            } else {
                ItemStack partial = new ItemStack(item, remaining, stack.getCurrentDurability());
                allowed.add(partial);
                heldItemsCount.put(item, limit);
            }
        }

        return allowed;
    }

    public void setLevelOneParty(final GameState52c game) {
        if(config.enableAllCharacters != EnableAllCharacters.STOCK) {

            var chars = game.charData_32c;
            var i = 0;
            for (var character : chars) {
                character.level_12 = 1;
                character.xp_00 = 0;
                applyCharacterStats(character, i);
                applyDragoonStats(character, i);
                character.stats.getStat(HP_STAT.get()).restore();
                character.stats.getStat(MP_STAT.get()).restore();
                i++;
            }

            final CharacterData2c dart = game.charData_32c.get(0);
            equipStartingGear(dart, IrongoonEquipment.BROAD_SWORD.get(), LodEquipment.LEATHER_BOOTS.get());

            final CharacterData2c lavitz = game.charData_32c.get(1);
            equipStartingGear(lavitz, IrongoonEquipment.SPEAR.get(), LodEquipment.LEATHER_BOOTS.get());

            final CharacterData2c shana = game.charData_32c.get(2);
            equipStartingGear(shana, IrongoonEquipment.SHORT_BOW.get(), LodEquipment.LEATHER_SHOES.get());

            final CharacterData2c rose = game.charData_32c.get(3);
            equipStartingGear(rose, IrongoonEquipment.RAPIER.get(), LodEquipment.LEATHER_SHOES.get());

            final CharacterData2c haschel = game.charData_32c.get(4);
            equipStartingGear(haschel, IrongoonEquipment.IRON_KNUCKLE.get(), LodEquipment.LEATHER_BOOTS.get());

            final CharacterData2c albert = game.charData_32c.get(5);
            equipStartingGear(albert, IrongoonEquipment.SPEAR.get(), LodEquipment.LEATHER_BOOTS.get());

            final CharacterData2c meru = game.charData_32c.get(6);
            equipStartingGear(meru, IrongoonEquipment.MACE.get(), LodEquipment.LEATHER_SHOES.get());

            final CharacterData2c kongol = game.charData_32c.get(7);
            equipStartingGear(kongol, IrongoonEquipment.AXE.get(), LodEquipment.LEATHER_BOOTS.get());

            final CharacterData2c miranda = game.charData_32c.get(8);
            equipStartingGear(miranda, IrongoonEquipment.SHORT_BOW.get(), LodEquipment.LEATHER_SHOES.get());
        }
    }

    private void equipStartingGear(final CharacterData2c character, final Equipment weapon, final Equipment boots) {
        character.equip(EquipmentSlot.WEAPON, weapon);
        character.equip(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
        character.equip(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
        character.equip(EquipmentSlot.BOOTS, boots);
        character.equip(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());
    }

    public void enableAllCharacters(final SubmapWarpEvent game) {
        if((game.submapCut == 10 && config.enableAllCharacters == EnableAllCharacters.STORY_CONTROLLED) || config.enableAllCharacters == EnableAllCharacters.PERMANENTLY) {
            for(final CharacterData2c character : game.getGameState().charData_32c) {
                character.partyFlags_04 |= 0x3;
            }
        }
    }

    public int doPartyFlags(final int currentPartyFlags, final int partyFlags) {
        if(config.enableAllCharacters == EnableAllCharacters.PERMANENTLY) return partyFlags | 0x3;

        if(config.enableAllCharacters == EnableAllCharacters.STORY_CONTROLLED && (currentPartyFlags & 0x3) == 0x3) return partyFlags | 0x3;

        return partyFlags;
    }

    public int doPrimaryPartyChange(
            final List<CharacterData2c> characterData,
            final int activePartySlot,
            final int characterIndex
    ) {
        if(config.battleParty != BattleParty.RANDOM_CAMPAIGN) return characterIndex;

        final var campaignParty = battlePartyRandomizer.randomizeCampaign(characterData);
        if(activePartySlot < 0 || activePartySlot >= campaignParty.size()) return -1;

        return campaignParty.getInt(activePartySlot);
    }

    public IntList doBattleParty(final List<CharacterData2c> characterData, final IntList battleParty) {
        return switch (config.battleParty) {
            case STOCK -> battlePartyRandomizer.maintainStock(battleParty);
            case RANDOM_CAMPAIGN -> battlePartyRandomizer.randomizeCampaign(characterData);
            case RANDOM_BATTLE -> battlePartyRandomizer.randomizeBattle(characterData);
        };
    }
}
