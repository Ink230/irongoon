package lod.irongoon.services.randomizer;

import legend.core.GameEngine;
import legend.game.inventory.Equipment;
import legend.game.inventory.InventoryEntry;
import legend.game.inventory.Item;
import legend.game.inventory.ItemStack;
import legend.game.inventory.screens.ShopScreen;
import legend.game.types.Shop;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.ShopContents;
import lod.irongoon.data.ShopDuplicates;
import lod.irongoon.data.ShopQuantityLogic;
import org.legendofdragoon.modloader.registries.RegistryId;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ShopContentsRandomizer {
    private static final ShopContentsRandomizer INSTANCE = new ShopContentsRandomizer();
    public static ShopContentsRandomizer getInstance() { return INSTANCE; }

    private ShopContentsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> maintainStock(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents) {
        return new ArrayList<>(contents);
    }

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> randomizeItems(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents) {
        final var shopHash = shop.toString().hashCode();

        final List<ShopScreen.ShopEntry<InventoryEntry<?>>> result = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            final var entry = contents.get(i);
            if(entry.item instanceof Equipment) {
                result.add(entry);
                continue;
            }
            final var itemHash = entry.item.toString().hashCode();
            result.add(this.generateRandomShopInventoryEntry(true, shopHash, i, itemHash, result));
        }
        return result;
    }

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> randomizeEquipment(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents) {
        final var shopHash = shop.toString().hashCode();

        final List<ShopScreen.ShopEntry<InventoryEntry<?>>> result = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            final var entry = contents.get(i);
            if(entry.item instanceof Item) {
                result.add(entry);
                continue;
            }
            final var itemHash = entry.item.toString().hashCode();
            result.add(this.generateRandomShopInventoryEntry(false, shopHash, i, itemHash, result));
        }
        return result;
    }

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> randomizeAll(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents) {
        final var equipmentResults = this.randomizeEquipment(shop, contents);
        final var itemResults = this.randomizeItems(shop, contents);

        return IntStream.range(0, contents.size())
                .mapToObj(i -> contents.get(i).item instanceof Equipment ? equipmentResults.get(i) : itemResults.get(i))
                .collect(Collectors.toList());
    }

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> randomizeAllMixed(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents) {
        final var shopHash = shop.toString().hashCode();
        final var random = new Random(config.seed + shopHash);

        final List<ShopScreen.ShopEntry<InventoryEntry<?>>> result = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            final var entry = contents.get(i);
            final var itemHash = entry.item.toString().hashCode();
            result.add(this.generateRandomShopInventoryEntry(random.nextBoolean(), shopHash, i, itemHash, result));
        }
        return result;
    }

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> prepareContents(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents, final int shopQuantity) {
        final var shopHash = Math.abs(shop.getRegistryId().toString().hashCode());
        final Random random = new Random(config.seed + shopHash);
        final var fillItemShops = (config.shopQuantityLogic == ShopQuantityLogic.FILL_ALL || config.shopContents != ShopContents.RANDOMIZE_EQUIPMENT);
        final var containsOnlyItems = contents.stream().allMatch(entry -> isItem(entry.item));
        final var fillEquipmentShops = (config.shopQuantityLogic == ShopQuantityLogic.FILL_ALL || config.shopContents != ShopContents.RANDOMIZE_ITEMS);
        final var containsOnlyEquipment = contents.stream().allMatch(entry -> isEquipment(entry.item));

        final List<ShopScreen.ShopEntry<InventoryEntry<?>>> preparedContents = new ArrayList<>(contents
                .stream().filter(entry -> {
                    final var isItem = isItem(entry.item);
                    final var registryId = getRegistryIdString(entry.item);

                    final var onBanList = config.shopContentsRecalled.contains(registryId);
                    final var onItemPool = (isItem && (config.shopContentsItemPool.isEmpty() || config.shopContentsItemPool.contains(registryId)));
                    final var onEquipmentPool = (!isItem && (config.shopContentsEquipmentPool.isEmpty() || config.shopContentsEquipmentPool.contains(registryId)));

                    return !onBanList && (onItemPool || onEquipmentPool);
                }).collect(Collectors.toList()));

        final var doNothingToItems = shop.shopType_00 == 1 && config.shopContents == ShopContents.RANDOMIZE_EQUIPMENT && preparedContents.stream().allMatch(entry -> isItem(entry.item));
        final var doNothingToEquipment = shop.shopType_00 == 0 && config.shopContents == ShopContents.RANDOMIZE_ITEMS && preparedContents.stream().allMatch(entry -> isEquipment(entry.item));

        if (preparedContents.size() == shopQuantity || doNothingToItems || doNothingToEquipment) {
            return preparedContents;
        }

        if (preparedContents.size() > shopQuantity) {
            return new ArrayList<>(preparedContents.subList(0, shopQuantity));
        }

        // mono-inventory, provide matching mono-fulfillment to the original contents
        if (((containsOnlyItems && fillItemShops) || (containsOnlyEquipment && fillEquipmentShops))) {
            for(int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
                final var shopEntry = this.generateRandomShopInventoryEntry(containsOnlyItems, shopHash, i, 0, preparedContents);
                if (shopEntry != null) {
                    preparedContents.add(shopEntry);
                }
            }

            return preparedContents;
        }

        if (containsOnlyEquipment || containsOnlyEquipment) return preparedContents;

        // mixed inventory and missing inventory quantity, provide mixed fulfillment
        for (int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
            final var shopEntry = this.generateRandomShopInventoryEntry(random.nextBoolean(), shopHash, i, 0, preparedContents);
            if (shopEntry != null) {
                preparedContents.add(shopEntry);
            }
        }

        return preparedContents;
    }

    private boolean isItem(InventoryEntry<?> entry) {
        if (entry instanceof ItemStack) {
            return true;
        }
        return entry instanceof Item;
    }

    private boolean isEquipment(InventoryEntry<?> entry) {
        return entry instanceof Equipment;
    }

    private String getRegistryIdString(InventoryEntry<?> entry) {
        if (entry instanceof ItemStack) {
            return ((ItemStack) entry).getRegistryId().toString();
        } else if (entry instanceof Equipment) {
            return ((Equipment) entry).getRegistryId().toString();
        } else if (entry instanceof Item) {
            return ((Item) entry).getRegistryId().toString();
        }
        return entry.toString();
    }

    public List<ShopScreen.ShopEntry<InventoryEntry<?>>> processContents(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry<?>>> contents) {
        final var shopHash = Math.abs(shop.getRegistryId().toString().hashCode());
        Random random = new Random(config.seed + shopHash);

        final var processContents = new ArrayList<>(contents);

        if((config.shopQuantityLogic == ShopQuantityLogic.FILL_ALL) || !(shop.shopType_00 == 1 && config.shopContents == ShopContents.RANDOMIZE_EQUIPMENT) && !(shop.shopType_00 == 0 && config.shopContents == ShopContents.RANDOMIZE_ITEMS)){
            Collections.shuffle(processContents, random);
        }

        return processContents;
    }

    private ShopScreen.ShopEntry<InventoryEntry<?>> generateRandomShopInventoryEntry(final boolean generateItem, long shopHash, int slotNumber, long itemHash, List<ShopScreen.ShopEntry<InventoryEntry<?>>> currentInventory) {
        final var uniqueModifier = shopHash + slotNumber + itemHash;

        final var items = StreamSupport.stream(GameEngine.REGISTRIES.items.spliterator(), false).filter(item -> {
            return !config.shopContentsRecalled.contains(item.toString())
                    && (config.shopDuplicates == ShopDuplicates.ANY || (currentInventory.isEmpty() || !(this.isPresentInShop(true, currentInventory, item))))
                    && (config.shopContentsItemPool.isEmpty() || config.shopContentsItemPool.contains(item.toString()));
        }).collect(Collectors.toList());

        final var equipment = StreamSupport.stream(GameEngine.REGISTRIES.equipment.spliterator(), false).filter(equip -> {
            return !config.shopContentsRecalled.contains(equip.toString())
                    && (config.shopDuplicates == ShopDuplicates.ANY || (currentInventory.isEmpty() || !(this.isPresentInShop(false, currentInventory, equip))))
                    && (config.shopContentsEquipmentPool.isEmpty() || config.shopContentsEquipmentPool.contains(equip.toString()));
        }).collect(Collectors.toList());

        // only one item in shop pool or shop pool items < shop quantity
        if(items.size() == 0) return null;
        if(equipment.size() == 0) return null;

        if((items.size() == 1 && generateItem) || (equipment.size() == 1 && !generateItem)) {
            final RegistryId inventoryIdentifier = generateItem
                    ? items.get(0)
                    : equipment.get(0);

            return this.addShopInventoryEntryByRegistryId(generateItem, inventoryIdentifier);
        }

        final RegistryId inventoryIdentifier = generateItem
                ? items.get(statRandomizer.calculateRandomNumberWithBounds(0, items.size() - 1, uniqueModifier))
                : equipment.get(statRandomizer.calculateRandomNumberWithBounds(0, equipment.size() - 1, uniqueModifier));

        return this.addShopInventoryEntryByRegistryId(generateItem, inventoryIdentifier);
    }

    private ShopScreen.ShopEntry<InventoryEntry<?>> addShopInventoryEntryByRegistryId(final boolean generateItem, RegistryId inventoryIdentifier) {
        final var inventoryEntry = this.getInventoryEntry(generateItem, inventoryIdentifier);

        return new ShopScreen.ShopEntry<>(inventoryEntry, inventoryEntry.getBuyPrice());
    }

    private InventoryEntry<?> getInventoryEntry(final boolean checkItemRegistry, final RegistryId registryId) {
        if (checkItemRegistry) {
            Item item = GameEngine.REGISTRIES.items.getEntry(registryId).get();
            return new ItemStack(item);
        } else {
            return GameEngine.REGISTRIES.equipment.getEntry(registryId).get();
        }
    }

    private boolean isPresentInShop(final boolean checkItemRegistry, final List<ShopScreen.ShopEntry<InventoryEntry<?>>> currentInventory, final RegistryId item) {
        return currentInventory.stream().anyMatch(entry -> {
            if (entry.item instanceof Item && !checkItemRegistry) return false;
            if (entry.item instanceof Equipment && checkItemRegistry) return false;

            return entry.item.toString().equals(this.getInventoryEntry(checkItemRegistry, item).toString());
        });
    }
}