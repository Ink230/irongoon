package lod.irongoon;

import legend.core.GameEngine;
import legend.game.modding.Mod;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.characters.CharacterStatsEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.CharacterData;
import lod.irongoon.data.ExternalData;
import lod.irongoon.entitymanager.Characters;
import lod.irongoon.entitymanager.DataTable;
import lod.irongoon.entitymanager.DataTables;
import lod.irongoon.entitymanager.DivineFruit;
import lod.irongoon.parse.CSVParser;
import lod.irongoon.parse.DataParser;

@Mod(id = Irongoon.MOD_ID)
public class Irongoon {
    public static final String MOD_ID = "Irongoon";

    private final DataParser dataParser = CSVParser.getInstance();
    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final Characters characters = new Characters();
    private final DataTables dataTables = new DataTables();

    public Irongoon() {
        GameEngine.EVENTS.register(this);
        initialize();
    }

    private void initialize() {
        for(CharacterData character : CharacterData.values()) {
            characters.addCharacter(character, new DivineFruit());
        }
    }

    private void refreshState() {
        loadExternalData();
        updateState();
    }

    private void loadExternalData() {
        for(ExternalData data : ExternalData.values()) {
            var list = dataParser.load(config.ExternalDataLoadPath + data.getValue() + config.ExternalDataLoadExtension);
            dataTables.addDataTable(data, list);
        }
    }

    private void updateState() {

    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        refreshState();
    }
}
