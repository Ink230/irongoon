package lod.irongoon;

import legend.core.GameEngine;
import legend.game.modding.Mod;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.gamestate.GameLoadedEvent;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.parse.CSVParser;
import lod.irongoon.parse.DataParser;

@Mod(id = Irongoon.MOD_ID)
public class Irongoon {
    public static final String MOD_ID = "Irongoon";

    private final DataParser dataParser = CSVParser.getInstance();
    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public Irongoon() {
        GameEngine.EVENTS.register(this);
    }

    private void refreshState() {
        loadExternalData();
    }

    private void loadExternalData() {
        var list = dataParser.load(config.ExternalDataLoadPath + "scdk-character-stats" + config.ExternalDataLoadExtension);
    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        refreshState();
    }
}
