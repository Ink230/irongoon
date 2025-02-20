package lod.irongoon.services.randomizer;

import legend.core.GameEngine;
import legend.game.inventory.Equipment;
import legend.game.inventory.InventoryEntry;
import legend.game.inventory.Item;
import legend.game.inventory.screens.ShopScreen;
import legend.game.types.Shop;
import lod.irongoon.config.IrongoonConfig;
import org.legendofdragoon.modloader.registries.RegistryId;
import java.util.List;
import java.util.stream.Collectors;
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

    public List<ShopScreen.ShopEntry<InventoryEntry>> maintainStock(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        final var shopHash = Math.abs(shop.getRegistryId().hashCode());

        if(config.shopContentsItemPool.isEmpty() && config.shopContentsEquipmentPool.isEmpty()) return new ArrayList<>(contents);

        final var filteredItemContents = contents.stream()
                .filter(entry -> {
                    if(!(entry.item instanceof Item)) return false;

                    if(config.shopContentsItemPool.isEmpty()) return true;

                    final var modIdAndIdentifier = ((Item) entry.item).getRegistryId().toString();
                    return config.shopContentsItemPool.contains(modIdAndIdentifier);
                })
                .collect(Collectors.toList());

        final var filteredEquipmentContents = contents.stream()
                .filter(entry -> {
                    if(!(entry.item instanceof Equipment)) return false;

                    if(config.shopContentsEquipmentPool.isEmpty()) return true;

                    final var modIdAndIdentifier = ((Equipment) entry.item).getRegistryId().toString();
                    return config.shopContentsEquipmentPool.contains(modIdAndIdentifier);
                })
                .collect(Collectors.toList());

        final var filteredContents = shop.shopType_00 == 0 ? new ArrayList<>(filteredEquipmentContents) : new ArrayList<>(filteredItemContents);
        final var shopContentsPool = shop.shopType_00 == 0 ? config.shopContentsEquipmentPool : config.shopContentsItemPool;
        final var useItemShop = shop.shopType_00 == 0 ? false : true;

        if(shopContentsPool.isEmpty()) return filteredContents;

        for(int i = filteredContents.size() + 1; i <= contents.size(); i++) {
            var randomEntryId = new RegistryId(shopContentsPool.get(statRandomizer.calculateRandomNumberWithBounds(0, shopContentsPool.size() - 1, shopHash + i)));
            filteredContents.add(this.generateShopInventorySlot(useItemShop, randomEntryId));
        }

        return filteredContents;
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> randomizeItems(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        return new ArrayList<>(contents);
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> randomizeEquipment(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        return new ArrayList<>(contents);
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> randomizeAll(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        return new ArrayList<>(contents);
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> randomizeAllMixed(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        return new ArrayList<>(contents);
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> prepareContentSlots(Shop shop, List<ShopScreen.ShopEntry<InventoryEntry>> contents, final int shopQuantity) {
        final List<ShopScreen.ShopEntry<InventoryEntry>> preparedContents = new ArrayList<>(contents);
        final var shopHash = Math.abs(shop.getRegistryId().hashCode());
        final Random random = new Random(config.seed + shopHash);

        if (contents.size() == shopQuantity) {
            return new ArrayList<>(contents);
        }

        if (contents.size() > shopQuantity) {
            Collections.shuffle(preparedContents, random);

            return new ArrayList<>(preparedContents.subList(0, shopQuantity));
        }

        // need to fill out the inventory - approximate workaround
        var containsOnlyItems = contents.stream().allMatch(entry -> entry.item instanceof Item);
        var containsOnlyEquipment = contents.stream().allMatch(entry -> entry.item instanceof Equipment);

        if ((containsOnlyItems || containsOnlyEquipment) && !config.shopContents.equals("RANDOMIZE_ALL_MIXED")) {
            final var generateItem = containsOnlyItems;

            for(int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
                final var shopEntry = this.generateRandomShopInventoryRegistryId(generateItem, shopHash, i, 0);
                preparedContents.add(shopEntry);
            }

            return new ArrayList<>(preparedContents);
        }

        // mixed inventory and missing inventory quantity, provide mixed fulfillment
        for (int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
            final var shopEntry = this.generateRandomShopInventoryRegistryId(random.nextBoolean(), shopHash, i, 0);
            preparedContents.add(shopEntry);
        }

        return new ArrayList<>(preparedContents);
    }

    private ShopScreen.ShopEntry<InventoryEntry> generateRandomShopInventoryRegistryId(final boolean generateItem, long shopHash, int slotNumber, long itemHash) {
        final var uniqueModifier = shopHash + slotNumber + itemHash;
        final var items = StreamSupport.stream(GameEngine.REGISTRIES.items.spliterator(), false).collect(Collectors.toList());
        final var equipment = StreamSupport.stream(GameEngine.REGISTRIES.equipment.spliterator(), false).collect(Collectors.toList());

        final RegistryId inventoryIdentifier = generateItem
                ? items.get(statRandomizer.calculateRandomNumberWithBounds(0, items.size() - 1, uniqueModifier))
                : equipment.get(statRandomizer.calculateRandomNumberWithBounds(0, equipment.size() - 1, uniqueModifier));

        return this.generateShopInventorySlot(generateItem, inventoryIdentifier);
    }

    private ShopScreen.ShopEntry<InventoryEntry> generateShopInventorySlot(final boolean generateItem, RegistryId inventoryIdentifier) {
        final var inventoryEntry = generateItem
                ? GameEngine.REGISTRIES.items.getEntry(inventoryIdentifier).get()
                : GameEngine.REGISTRIES.equipment.getEntry(inventoryIdentifier).get();

        return new ShopScreen.ShopEntry<>(inventoryEntry, inventoryEntry.getPrice());
    }
}
