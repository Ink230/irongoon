package lod.irongoon.services.randomizer;

import legend.core.GameEngine;
import legend.game.inventory.Equipment;
import legend.game.inventory.InventoryEntry;
import legend.game.inventory.Item;
import legend.game.inventory.screens.ShopScreen;
import legend.game.types.Shop;
import legend.lodmod.LodEquipment;
import legend.lodmod.LodItems;
import legend.lodmod.LodMod;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.ShopContents;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ShopContentsRandomizer {
    private static final ShopContentsRandomizer INSTANCE = new ShopContentsRandomizer();
    public static ShopContentsRandomizer getInstance() { return INSTANCE; }

    private ShopContentsRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public List<ShopScreen.ShopEntry<InventoryEntry>> maintainStock(List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
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
        final List<ShopScreen.ShopEntry<InventoryEntry>> preparedContents;
        final var shopHash = Math.abs(shop.getRegistryId().hashCode());
        Random random = new Random(config.seed + shopHash);

        if (contents.size() == shopQuantity) {
            return new ArrayList<>(contents);
        }

        if (contents.size() > shopQuantity) {
            preparedContents = new ArrayList<>(contents);
            Collections.shuffle(preparedContents, random);

            return new ArrayList<>(preparedContents.subList(0, shopQuantity));
        }

        // need to fill out the inventory
        var containsOnlyItems = contents.stream().allMatch(entry -> entry.item instanceof Item);
        var containsOnlyEquipment = contents.stream().allMatch(entry -> entry.item instanceof Equipment);

        if (containsOnlyItems && !config.shopContents.equals("RANDOMIZE_ALL_MIXED")) {
            preparedContents = new ArrayList<>(contents);

            for(int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
                final var shopEntry = this.generateShopInventorySlot(true, shopHash, i, 0);
                preparedContents.add(shopEntry);
            }

            return preparedContents;
        }

        if (containsOnlyEquipment && !config.shopContents.equals("RANDOMIZE_ALL_MIXED")) {
            preparedContents = new ArrayList<>(contents);

            for(int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
                final var shopEntry = this.generateShopInventorySlot(false, shopHash, i, 0);
                preparedContents.add(shopEntry);
            }

            return preparedContents;
        }

        // mixed inventory and missing inventory quantity, provide mixed fulfillment
        preparedContents = new ArrayList<>(contents);
        for (int i = preparedContents.size() + 1; i <= shopQuantity; i++) {
            if (random.nextBoolean()) {
                final var shopEntry = this.generateShopInventorySlot(true, shopHash, i, 0);
                preparedContents.add(shopEntry);
            } else {
                final var shopEntry = this.generateShopInventorySlot(false, shopHash, i, 0);
                preparedContents.add(shopEntry);
            }
        }

        return preparedContents;
    }

    private ShopScreen.ShopEntry<InventoryEntry> generateShopInventorySlot(boolean generateItem, long shopHash, int slotNumber, long itemHash) {
        final var uniqueModifier = shopHash + slotNumber + itemHash;
        final RegistryId inventoryIdentifier = generateItem
                ? LodMod.id(LodMod.ITEM_IDS[statRandomizer.calculateRandomNumberWithBounds(0, LodMod.ITEM_IDS.length - 1, uniqueModifier)])
                : LodMod.id(LodMod.EQUIPMENT_IDS[statRandomizer.calculateRandomNumberWithBounds(0, LodMod.EQUIPMENT_IDS.length - 1, uniqueModifier)]);

        final var inventoryEntry = generateItem
                ? GameEngine.REGISTRIES.items.getEntry(inventoryIdentifier).get()
                : GameEngine.REGISTRIES.equipment.getEntry(inventoryIdentifier).get();

        return new ShopScreen.ShopEntry<>(inventoryEntry, inventoryEntry.getPrice());
    }
}
