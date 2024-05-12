package lod.irongoon.services.randomizer;

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

    private DivineFruit createDivineFruit(Elements element, Elements immunity) {
        return new DivineFruit(element, immunity);
    }

    public DivineFruit maintainStock(int monsterId) {
        var element = parser.getMonsterStatsById(monsterId).get(EnemyStatsData.ELEMENT.name());
        return createDivineFruit(Elements.getEnumByIndex(element), Elements.getEnumByIndex(0));
    }

    public DivineFruit randomizeMonsterElement(int monsterId) {
        Random random = new Random(monsterId);
        var element = statsRandomizer.calculateRandomNumberBetweenBounds(0, 16, monsterId) * 8;
        return createDivineFruit(Elements.getEnumByIndex(element), Elements.getEnumByIndex(0));
    }

    public DivineFruit randomizeRandomMonsterElement() {
        Random random = new Random();
        var element = statsRandomizer.calculateRandomNumberBetweenBoundsNoSeed(0, 16);
        return createDivineFruit(Elements.getEnumByIndex(element), Elements.getEnumByIndex(0));
    }
}
