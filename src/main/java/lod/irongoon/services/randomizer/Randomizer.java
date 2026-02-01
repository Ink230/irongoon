package lod.irongoon.services.randomizer;


import legend.game.characters.Element;
import legend.game.inventory.Equipment;
import legend.game.inventory.InventoryEntry;
import legend.game.inventory.Item;
import legend.game.inventory.ItemStack;
import legend.game.inventory.screens.ShopScreen;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.characters.CharacterStatsEvent;
import legend.game.modding.events.submap.SubmapWarpEvent;
import legend.game.title.NewGame;
import legend.game.types.CharacterData2c;
import legend.game.types.EquipmentSlot;
import legend.game.types.GameState52c;
import legend.game.types.Shop;
import legend.lodmod.LodEquipment;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.EnableAllCharacters;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.registries.IrongoonEquipment;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import java.util.*;
import java.util.stream.Collectors;

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
    private final BattleStageRandomizer battleStageRandomizer = BattleStageRandomizer.getInstance();
    private final EscapeChanceRandomizer escapeChanceRandomizer = EscapeChanceRandomizer.getInstance();
    private final ShopAvailabilityRandomizer shopAvailabilityRandomizer = ShopAvailabilityRandomizer.getInstance();
    private final ShopQuantityRandomizer shopQuantityRandomizer = ShopQuantityRandomizer.getInstance();
    private final ShopContentsRandomizer shopContentsRandomizer = ShopContentsRandomizer.getInstance();
    private final CharacterElementRandomizer characterElementRandomizer = CharacterElementRandomizer.getInstance();
    private final BattlePartyRandomizer battlePartyRandomizer = BattlePartyRandomizer.getInstance();

    public static String retrieveNewCampaignSeed() {
        config.campaignSeed = seedRandomizer.generateNewSeed();
        return config.campaignSeed;
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
            case RANDOMIZE_RANDOM_STOCK_BOUNDS -> characterHPRandomizer.randomizeRandomStockWithBounds(character.characterId, character.level);
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

    public RegistryDelegate<Element>[] doCharacterElement(RegistryDelegate<Element>[] characterElements) {
        return switch (config.characterElements) {
            case STOCK -> characterElements;
            case RANDOM_CAMPAIGN -> characterElementRandomizer.randomizeCampaign(characterElements);
            case RANDOM_BATTLE -> characterElementRandomizer.randomizeBattle(characterElements);
        };
    }

    public void setLevelOneParty(final GameState52c game) {
        if(config.enableAllCharacters != EnableAllCharacters.STOCK) {
            Arrays.fill(NewGame.characterStartingLevels, 1);

            final Map<EquipmentSlot, Equipment> dart = game.charData_32c[0].equipment_14;
            dart.put(EquipmentSlot.WEAPON, IrongoonEquipment.BROAD_SWORD.get());
            dart.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            dart.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            dart.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_BOOTS.get());
            dart.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

            final Map<EquipmentSlot, Equipment> lavitz = game.charData_32c[1].equipment_14;
            lavitz.put(EquipmentSlot.WEAPON, IrongoonEquipment.SPEAR.get());
            lavitz.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            lavitz.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            lavitz.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_BOOTS.get());
            lavitz.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

            final Map<EquipmentSlot, Equipment> shana = game.charData_32c[2].equipment_14;
            shana.put(EquipmentSlot.WEAPON, IrongoonEquipment.SHORT_BOW.get());
            shana.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            shana.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            shana.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_SHOES.get());
            shana.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

            final Map<EquipmentSlot, Equipment> rose = game.charData_32c[3].equipment_14;
            rose.put(EquipmentSlot.WEAPON, IrongoonEquipment.RAPIER.get());
            rose.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            rose.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            rose.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_SHOES.get());
            rose.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

            final Map<EquipmentSlot, Equipment> haschel = game.charData_32c[4].equipment_14;
            haschel.put(EquipmentSlot.WEAPON, IrongoonEquipment.IRON_KNUCKLE.get());
            haschel.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            haschel.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            haschel.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_BOOTS.get());
            haschel.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

            final Map<EquipmentSlot, Equipment> albert = game.charData_32c[5].equipment_14;
            albert.put(EquipmentSlot.WEAPON, IrongoonEquipment.SPEAR.get());
            albert.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            albert.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            albert.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_BOOTS.get());
            albert.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

            final Map<EquipmentSlot, Equipment> meru = game.charData_32c[6].equipment_14;
            meru.put(EquipmentSlot.WEAPON, IrongoonEquipment.MACE.get());
            meru.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            meru.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            meru.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_SHOES.get());
            meru.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

            final Map<EquipmentSlot, Equipment> kongol = game.charData_32c[7].equipment_14;
            kongol.put(EquipmentSlot.WEAPON, IrongoonEquipment.AXE.get());
            kongol.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            kongol.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            kongol.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_BOOTS.get());
            kongol.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());

            final Map<EquipmentSlot, Equipment> miranda = game.charData_32c[8].equipment_14;
            miranda.put(EquipmentSlot.WEAPON, IrongoonEquipment.SHORT_BOW.get());
            miranda.put(EquipmentSlot.HELMET, LodEquipment.BANDANA.get());
            miranda.put(EquipmentSlot.ARMOUR, LodEquipment.LEATHER_ARMOR.get());
            miranda.put(EquipmentSlot.BOOTS, LodEquipment.LEATHER_SHOES.get());
            miranda.put(EquipmentSlot.ACCESSORY, LodEquipment.BRACELET.get());
        }
    }

    public void enableAllCharacters(final SubmapWarpEvent game) {
        if((game.submapCut == 10 && config.enableAllCharacters == EnableAllCharacters.STORY_CONTROLLED) || config.enableAllCharacters == EnableAllCharacters.PERMANENTLY) {
            for(var i = 0; i < game.getGameState().charData_32c.length; i++) {
                game.getGameState().charData_32c[i].partyFlags_04 |= 0x3;
            }
        }
    }

    public int[] doBattleParty(final CharacterData2c[] characterData, final int[] battleParty) {
        return switch (config.battleParty) {
            case STOCK -> battlePartyRandomizer.maintainStock(battleParty);
            case RANDOM_CAMPAIGN -> battlePartyRandomizer.randomizeCampaign(characterData);
            case RANDOM_BATTLE -> battlePartyRandomizer.randomizeBattle(characterData);
        };
    }
}
