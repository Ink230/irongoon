package lod.irongoon.data;

import lod.irongoon.data.tables.*;

import java.io.FileNotFoundException;

public class Tables {
    private static final Tables instance = new Tables();

    public static Tables getInstance() {
        return instance;
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
        return (CharactersTable) instance.dataTables[Type.CHARACTERS.ordinal()];
    }

    public static DragoonsTable getDragoonTable() {
        return (DragoonsTable) instance.dataTables[Type.DRAGOONS.ordinal()];
    }

    public static EquipmentTable getEquipmentTable() {
        return (EquipmentTable) instance.dataTables[Type.EQUIPMENT.ordinal()];
    }

    public static MonstersTable getMonsterTable() {
        return (MonstersTable) instance.dataTables[Type.MONSTERS.ordinal()];
    }

    public static ShopsTable getShopTable() {
        return (ShopsTable) instance.dataTables[Type.SHOPS.ordinal()];
    }

    public static SpellsTable getSpellTable() {
        return (SpellsTable) instance.dataTables[Type.SPELLS.ordinal()];
    }

    public static AdditionsTable getAdditionTable() {
        return (AdditionsTable) instance.dataTables[Type.ADDITIONS.ordinal()];
    }

    private Tables() {
    }

    private final Table[] dataTables = new Table[]{
            new AdditionsTable(),
            new CharactersTable(),
            new DragoonsTable(),
            new EquipmentTable(),
            new MonstersTable(),
            new ShopsTable(),
            new SpellsTable()
    };

    public void initialize() throws FileNotFoundException {
        for (final var t : dataTables) {
            t.initialize();
        }
    }
}
