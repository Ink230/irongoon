package lod.irongoon.services;

import legend.game.inventory.Good;
import legend.game.inventory.GoodsInventory;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.EnableAllDragoons;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import java.util.List;

import static legend.lodmod.LodGoods.BLUE_DRAGOON_SPIRIT;
import static legend.lodmod.LodGoods.DARK_DRAGOON_SPIRIT;
import static legend.lodmod.LodGoods.GOLD_DRAGOON_SPIRIT;
import static legend.lodmod.LodGoods.JADE_DRAGOON_SPIRIT;
import static legend.lodmod.LodGoods.RED_DRAGOON_SPIRIT;
import static legend.lodmod.LodGoods.SILVER_DRAGOON_SPIRIT;
import static legend.lodmod.LodGoods.VIOLET_DRAGOON_SPIRIT;

public final class DragoonUnlocks {
    private static final List<RegistryDelegate<Good>> BASE_DRAGOON_SPIRITS = List.of(
        RED_DRAGOON_SPIRIT,
        BLUE_DRAGOON_SPIRIT,
        JADE_DRAGOON_SPIRIT,
        GOLD_DRAGOON_SPIRIT,
        VIOLET_DRAGOON_SPIRIT,
        SILVER_DRAGOON_SPIRIT,
        DARK_DRAGOON_SPIRIT
    );
    private static final DragoonUnlocks INSTANCE = new DragoonUnlocks();

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public static DragoonUnlocks getInstance() {
        return INSTANCE;
    }

    private DragoonUnlocks() {}

    public void initializeCampaign(final GoodsInventory goods) {
        switch(this.config.enableAllDragoons) {
            case STOCK -> { }
            case PERMANENTLY, STORY_CONTROLLED -> BASE_DRAGOON_SPIRITS.forEach(goods::give);
        }
    }

    public void preservePermanentUnlocks(final List<Good> takenGoods) {
        if(this.config.enableAllDragoons != EnableAllDragoons.PERMANENTLY) return;

        takenGoods.removeIf(this::isBaseDragoonSpirit);
    }

    private boolean isBaseDragoonSpirit(final Good good) {
        return BASE_DRAGOON_SPIRITS.stream().anyMatch(spirit -> spirit.get() == good);
    }
}
