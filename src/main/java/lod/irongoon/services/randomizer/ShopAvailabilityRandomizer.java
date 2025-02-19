package lod.irongoon.services.randomizer;

import legend.game.inventory.Equipment;
import legend.game.inventory.InventoryEntry;
import legend.game.inventory.Item;
import legend.game.inventory.screens.ShopScreen;
import legend.game.types.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopAvailabilityRandomizer {
    private static final ShopAvailabilityRandomizer INSTANCE = new ShopAvailabilityRandomizer();
    public static ShopAvailabilityRandomizer getInstance() { return INSTANCE; }

    private ShopAvailabilityRandomizer() {}

    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public List<ShopScreen.ShopEntry<InventoryEntry>> random(final Shop shop, final List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        final var hash = Math.abs(shop.getRegistryId().hashCode());
        final var result = statRandomizer.calculateNextBoolean(hash);
        if(result) return new ArrayList<>();

        return new ArrayList<>(contents);
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> maintainStock(final List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        return new ArrayList<>(contents);
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> noShops() {
        return new ArrayList<>();
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> noItemsInShops(final List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        return contents.stream()
                .filter(entry -> !(entry.item instanceof Item))
                .collect(Collectors.toList());
    }

    public List<ShopScreen.ShopEntry<InventoryEntry>> noEquipmentInShops(final List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        return contents.stream()
                .filter(entry -> !(entry.item instanceof Equipment))
                .collect(Collectors.toList());
    }
}
