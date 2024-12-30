package lod.irongoon.services.randomizer;

import legend.game.characters.Element;
import legend.game.characters.ElementSet;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.Elements;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.data.EnemyStatsData;
import lod.irongoon.parse.game.MonsterStatsParser;


public class MonsterElementRandomizer {
    private static final MonsterElementRandomizer INSTANCE = new MonsterElementRandomizer();
    public static MonsterElementRandomizer getInstance() { return INSTANCE; }
    private MonsterElementRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final MonsterStatsParser parser = MonsterStatsParser.getInstance();
    private final StatsRandomizer statsRandomizer = StatsRandomizer.getInstance();
    private final int elementLength = Elements.values().length;

    private DivineFruit createDivineFruit(Elements element, ElementSet immunity) {
        return new DivineFruit(element, immunity);
    }

    public DivineFruit maintainStock(int monsterId) {
        var element = parser.getMonsterStatsById(monsterId).get(EnemyStatsData.ELEMENT.name());
        return createDivineFruit(Elements.getEnumByIndex(element), processElementImmunity(monsterId));
    }

    public DivineFruit randomizeMonsterElement(int monsterId) {
        var upper = this.elementLength + processElementUpperBound();

        var element = 1 << statsRandomizer.calculateRandomNumberWithBounds(0, upper, monsterId);
        element = processElement(element);

        return createDivineFruit(Elements.getEnumByIndex(element), processElementImmunity(monsterId));
    }

    public DivineFruit randomizeRandomMonsterElement(int monsterId) {
        var upper = this.elementLength + processElementUpperBound();

        var element = 1 << statsRandomizer.calculateRandomNumberWithBoundsNoSeed(0, upper);
        element = processElement(element);

        return createDivineFruit(Elements.getEnumByIndex(element), processElementImmunity(monsterId));
    }

    public DivineFruit randomizeMonsterElementAndImmunity(int monsterId) {
        var elementUpper = this.elementLength + processElementUpperBound();
        var immunityUpper = this.elementLength + processElementImmunityUpperBound();

        var element = 1 << statsRandomizer.calculateRandomNumberWithBounds(0, elementUpper, monsterId);
        element = processElement(element);

        var elementImmune = 1 << statsRandomizer.calculateRandomNumberWithBounds(0, immunityUpper, monsterId + 1000);
        elementImmune = processElement(elementImmune);
        var elementImmunity = new ElementSet().add(Element.fromFlag(elementImmune).get());

        return createDivineFruit(Elements.getEnumByIndex(element), elementImmunity);
    }

    public DivineFruit randomizeRandomMonsterElementAndImmunity() {
        var elementUpper = this.elementLength + processElementUpperBound();
        var immunityUpper = this.elementLength + processElementImmunityUpperBound();

        var element = 1 << statsRandomizer.calculateRandomNumberWithBoundsNoSeed(0, elementUpper);
        element = processElement(element);

        var elementImmune = 1 << statsRandomizer.calculateRandomNumberWithBoundsNoSeed(0, immunityUpper);
        elementImmune = processElement(elementImmune);
        var elementImmunity = new ElementSet().add(Element.fromFlag(elementImmune).get());

        return createDivineFruit(Elements.getEnumByIndex(element), elementImmunity);
    }

    private int processElement(int value) {
        if (value >= (1 << (this.elementLength - 1))) return 0;
        return value;
    }

    private int processElementUpperBound() {
        return switch(config.noElementMonsters) {
            case EXCLUDE, IMMUNITIES_ONLY -> -2;
            case INCLUDE, ELEMENTS_ONLY -> -1;
        };
    }

    private int processElementImmunityUpperBound() {
        return switch(config.noElementMonsters) {
            case EXCLUDE, ELEMENTS_ONLY -> -2;
            case INCLUDE, IMMUNITIES_ONLY -> -1;
        };
    }

    private ElementSet processElementImmunity(int monsterId) {
        var immunity = parser.getMonsterStatsById(monsterId).get(EnemyStatsData.ELEMENT_IMMUNITY.name());
        if (immunity == 0) return new ElementSet();
        return new ElementSet().add(Element.fromFlag(immunity).get());
    }
}
