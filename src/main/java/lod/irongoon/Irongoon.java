package lod.irongoon;

import legend.core.GameEngine;
import legend.game.modding.Mod;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.characters.CharacterStatsEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;

import lod.irongoon.models.DivineFruit;
import lod.irongoon.services.randomizer.Randomizer;
import lod.irongoon.services.Characters;
import lod.irongoon.services.DataTables;

@Mod(id = Irongoon.MOD_ID)
public class Irongoon {
    public static final String MOD_ID = "Irongoon";

    private final Characters characters = Characters.getInstance();
    private final DataTables dataTables = DataTables.getInstance();
    private final Randomizer randomizer = Randomizer.getInstance();

    public Irongoon() {
        GameEngine.EVENTS.register(this);
    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        refreshState();
    }

    private void refreshState() {
        characters.initialize();
        dataTables.initialize();
    }

    @EventListener
    public void characterStats(final CharacterStatsEvent character) {
        DivineFruit bodyStatsRandomized = randomizer.doCharacterStats(character);
        DivineFruit dragoonStatsRandomized = randomizer.doDragoonStats(character);
        //todo HP

        character.bodyAttack = bodyStatsRandomized.bodyAttack;
        character.bodyDefence = bodyStatsRandomized.bodyDefense;
        character.bodyMagicAttack = bodyStatsRandomized.bodyMagicAttack;
        character.bodyMagicDefence = bodyStatsRandomized.bodyMagicDefense;
        character.bodySpeed = bodyStatsRandomized.bodySpeed;

        character.dragoonAttack = dragoonStatsRandomized.dragoonAttack;
        character.dragoonDefence = dragoonStatsRandomized.dragoonDefense;
        character.dragoonMagicAttack = dragoonStatsRandomized.dragoonMagicAttack;
        character.dragoonMagicDefence = dragoonStatsRandomized.dragoonMagicDefense;
    }
}
