package lod.irongoon.data;

import lod.irongoon.data.tables.*;

import java.io.FileNotFoundException;

public class Tables {

    private Tables() { }

    private static final Table[] tables;

    static {
        tables = new Table[]{
                new AdditionsTable(),
                new CharactersTable(),
                new DragoonsTable(),
                new EquipmentTable(),
                new MonstersTable(),
                new ShopsTable(),
                new SpellsTable()
        };
    }

    private enum Type {
        ADDITIONS,
        CHARACTERS,
        DRAGOONS,
        EQUIPMENT,
        MONSTERS,
        SHOPS,
        SPELLS,
    }

    public static CharactersTable getCharacterTable() {
        return (CharactersTable) tables[Type.CHARACTERS.ordinal()];
    }

    public static DragoonsTable getDragoonTable() {
        return (DragoonsTable) tables[Type.DRAGOONS.ordinal()];
    }

    public static EquipmentTable getEquipmentTable() {
        return (EquipmentTable) tables[Type.EQUIPMENT.ordinal()];
    }

    public static MonstersTable getMonsterTable() {
        return (MonstersTable) tables[Type.MONSTERS.ordinal()];
    }

    public static ShopsTable getShopTable() {
        return (ShopsTable) tables[Type.SHOPS.ordinal()];
    }

    public static SpellsTable getSpellTable() {
        return (SpellsTable) tables[Type.SPELLS.ordinal()];
    }

    public static AdditionsTable getAdditionTable() {
        return (AdditionsTable) tables[Type.ADDITIONS.ordinal()];
    }

    public static void initialize() throws FileNotFoundException {
        for (final var t : tables) {
            t.initialize();
        }
    }
}
