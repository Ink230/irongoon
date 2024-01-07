package lod.irongoon;

import com.github.slugify.Slugify;
import legend.core.GameEngine;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.characters.CharacterStatsEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;
import lod.irongoon.data.Tables;
import lod.irongoon.services.StatsRandomizer;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.FileNotFoundException;

@Mod(id = Irongoon.MOD_ID)
public class Irongoon {
    public static final String MOD_ID = "irongoon";
    private static final Slugify slug = Slugify.builder().underscoreSeparator(true).customReplacement("'", "").customReplacement("-", "_").build();
    public static RegistryId id(final String entryId) {
        return new RegistryId(MOD_ID, entryId);
    }

    public Irongoon() {
        GameEngine.EVENTS.register(this);
    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        try {
            Tables.initialize();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void characterStats(final CharacterStatsEvent character) {
        StatsRandomizer.randomize(character);
    }

    @EventListener
    public void monsterStats(final MonsterStatsEvent monster) {
        StatsRandomizer.randomize(monster);
    }
}
