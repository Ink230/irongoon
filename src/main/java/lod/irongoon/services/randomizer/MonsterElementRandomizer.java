package lod.irongoon.services.randomizer;

import legend.game.characters.ElementSet;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.Elements;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.data.EnemyStatsData;
import lod.irongoon.parse.game.MonsterStatsParser;

import java.util.Random;


public class MonsterElementRandomizer {
    private static final MonsterElementRandomizer INSTANCE = new MonsterElementRandomizer();
    public static MonsterElementRandomizer getInstance() { return INSTANCE; }
    private MonsterElementRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final MonsterStatsParser parser = MonsterStatsParser.getInstance();
    private final StatsRandomizer statsRandomizer = StatsRandomizer.getInstance();

    private DivineFruit createDivineFruit(Elements element, ElementSet immunity) {
        return new DivineFruit(element, immunity);
    }

    public DivineFruit maintainStock(int monsterId) {
        var element = parser.getMonsterStatsById(monsterId).get(EnemyStatsData.ELEMENT.name());
        return createDivineFruit(Elements.getEnumByIndex(element), new ElementSet());
    }

    public DivineFruit randomizeMonsterElement(int monsterId) {
        var element = 1 << statsRandomizer.calculateRandomNumberBetweenBounds(0, 7, monsterId);
        return createDivineFruit(Elements.getEnumByIndex(element), new ElementSet());
    }

    public DivineFruit randomizeRandomMonsterElement() {
        var element = 1 << statsRandomizer.calculateRandomNumberBetweenBoundsNoSeed(0, 7);
        return createDivineFruit(Elements.getEnumByIndex(element), new ElementSet());
    }
}
