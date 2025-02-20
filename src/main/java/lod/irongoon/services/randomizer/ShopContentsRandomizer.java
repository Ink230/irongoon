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
        return new ArrayList<>(contents);
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
        final var shopHash = Math.abs(shop.getRegistryId().hashCode());
        final Random random = new Random(config.seed + shopHash);

        final var preparedContents = new ArrayList<>(contents
                .stream().filter(entry -> {
                    return !config.shopContentsRecalled.contains(entry.toString())
                            && (entry.item instanceof Item && (config.shopContentsItemPool.isEmpty() || config.shopContentsItemPool.contains(entry.toString())))
                            || (entry.item instanceof Equipment && (config.shopContentsEquipmentPool.isEmpty() || config.shopContentsEquipmentPool.contains(entry.toString())));
                }).collect(Collectors.toList()));

        if (preparedContents.size() == shopQuantity) {
            return new ArrayList<>(preparedContents);
        }

        if (preparedContents.size() > shopQuantity) {
            Collections.shuffle(preparedContents, random);

            return new ArrayList<>(preparedContents.subList(0, shopQuantity));
        }

        // mono-inventory, provide matching mono-fulfillment to the original contents
        var containsOnlyItems = contents.stream().allMatch(entry -> entry.item instanceof Item);
        var containsOnlyEquipment = contents.stream().allMatch(entry -> entry.item instanceof Equipment);

        if ((containsOnlyItems || containsOnlyEquipment)) {
            for(int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
                final var shopEntry = this.generateRandomShopInventoryEntry(containsOnlyItems, shopHash, i, 0);
                preparedContents.add(shopEntry);
            }

            return new ArrayList<>(preparedContents);
        }

        // mixed inventory and missing inventory quantity, provide mixed fulfillment
        for (int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
            final var shopEntry = this.generateRandomShopInventoryEntry(random.nextBoolean(), shopHash, i, 0);
            preparedContents.add(shopEntry);
        }

        return new ArrayList<>(preparedContents);
    }

    private ShopScreen.ShopEntry<InventoryEntry> generateRandomShopInventoryEntry(final boolean generateItem, long shopHash, int slotNumber, long itemHash) {
        final var uniqueModifier = shopHash + slotNumber + itemHash;

        final var items = StreamSupport.stream(GameEngine.REGISTRIES.items.spliterator(), false).filter(item -> {
            return !config.shopContentsRecalled.contains(item.toString()) && (config.shopContentsItemPool.isEmpty() || config.shopContentsItemPool.contains(item.toString()));
        }).collect(Collectors.toList());

        final var equipment = StreamSupport.stream(GameEngine.REGISTRIES.equipment.spliterator(), false).filter(equip -> {
            return !config.shopContentsRecalled.contains(equip.toString()) && (config.shopContentsEquipmentPool.isEmpty() || config.shopContentsEquipmentPool.contains(equip.toString()));
        }).collect(Collectors.toList());

        final RegistryId inventoryIdentifier = generateItem
                ? items.get(statRandomizer.calculateRandomNumberWithBounds(0, items.size() - 1, uniqueModifier))
                : equipment.get(statRandomizer.calculateRandomNumberWithBounds(0, equipment.size() - 1, uniqueModifier));

        return this.addShopInventoryEntryByRegistryId(generateItem, inventoryIdentifier);
    }

    private ShopScreen.ShopEntry<InventoryEntry> addShopInventoryEntryByRegistryId(final boolean generateItem, RegistryId inventoryIdentifier) {
        final var inventoryEntry = generateItem
                ? GameEngine.REGISTRIES.items.getEntry(inventoryIdentifier).get()
                : GameEngine.REGISTRIES.equipment.getEntry(inventoryIdentifier).get();

        return new ShopScreen.ShopEntry<>(inventoryEntry, inventoryEntry.getPrice());
    }

    private List<ShopScreen.ShopEntry<InventoryEntry>> filterContentsByInventoryType(List<ShopScreen.ShopEntry<InventoryEntry>> contents, final boolean useItem) {
        return useItem
                ? contents.stream().filter(entry -> entry.item instanceof Item).collect(Collectors.toList())
                : contents.stream().filter(entry -> entry.item instanceof Equipment).collect(Collectors.toList());
    }
}
