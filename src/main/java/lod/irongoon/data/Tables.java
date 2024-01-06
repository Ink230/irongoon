package lod.irongoon.data;

import lod.irongoon.config.IrongoonConfig;
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

    private Tables() {
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();
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

    public CharactersTable getCharacterTable() {
        return (CharactersTable)this.dataTables[Type.CHARACTERS.ordinal()];
    }

    public DragoonsTable getDragoonTable() {
        return (DragoonsTable)this.dataTables[Type.DRAGOONS.ordinal()];
    }

    public EquipmentTable getEquipmentTable() {
        return (EquipmentTable)this.dataTables[Type.EQUIPMENT.ordinal()];
    }

    public MonstersTable getMonsterTable() {
        return (MonstersTable)this.dataTables[Type.MONSTERS.ordinal()];
    }

    public ShopsTable getShopTable() {
        return (ShopsTable)this.dataTables[Type.SHOPS.ordinal()];
    }

    public SpellsTable getSpellTable() {
        return (SpellsTable)this.dataTables[Type.SPELLS.ordinal()];
    }

    public AdditionsTable getAdditionTable() {
        return (AdditionsTable)this.dataTables[Type.ADDITIONS.ordinal()];
    }
}
