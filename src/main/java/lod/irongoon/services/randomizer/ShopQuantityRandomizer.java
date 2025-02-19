package lod.irongoon.services.randomizer;

import legend.game.inventory.InventoryEntry;
import legend.game.inventory.screens.ShopScreen;
import legend.game.types.Shop;
import lod.irongoon.config.IrongoonConfig;

import java.util.List;

public class ShopQuantityRandomizer {
    private static final ShopQuantityRandomizer INSTANCE = new ShopQuantityRandomizer();
    public static ShopQuantityRandomizer getInstance() { return INSTANCE; }

    private ShopQuantityRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public int maintainStock(List<ShopScreen.ShopEntry<InventoryEntry>> contents) {
        return contents.size();
    }

    public int randomBounds(Shop shop) {
        final var hash = Math.abs(shop.getRegistryId().hashCode());
        return statRandomizer.calculateRandomNumberWithBounds(config.shopQuantityLowerBound, config.shopQuantityUpperBound, hash);
    }
}
