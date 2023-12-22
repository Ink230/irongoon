package lod.irongoon;

import legend.core.GameEngine;
import legend.game.modding.Mod;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.gamestate.GameLoadedEvent;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.CharacterData;
import lod.irongoon.data.ExternalData;
import lod.irongoon.services.Characters;
import lod.irongoon.services.DataTables;
import lod.irongoon.parse.CSVParser;
import lod.irongoon.parse.DataParser;

@Mod(id = Irongoon.MOD_ID)
public class Irongoon {
    public static final String MOD_ID = "Irongoon";

    private final DataParser dataParser = CSVParser.getInstance();
    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final Characters characters = Characters.getInstance();
    private final DataTables dataTables = DataTables.getInstance();

    public Irongoon() {
        GameEngine.EVENTS.register(this);
    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        refreshState();
    }

    private void refreshState() {
        characters.initialize(CharacterData.values());
        dataTables.initialize(ExternalData.values(), dataParser, config);
    }
}
