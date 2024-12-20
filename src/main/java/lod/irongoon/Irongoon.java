package lod.irongoon;

import com.github.slugify.Slugify;
import legend.core.GameEngine;
import legend.game.characters.Element;
import legend.game.characters.ElementSet;
import legend.game.modding.events.battle.BattleEncounterStageDataEvent;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.config.ConfigEvent;
import legend.game.modding.events.config.ConfigLoadedEvent;
import legend.game.modding.events.config.ConfigUpdatedEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.submap.SubmapGenerateEncounterEvent;
import legend.game.saves.*;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.config.SeedConfigEntry;
import lod.irongoon.services.StaleStats;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;
import org.legendofdragoon.modloader.Mod;
import legend.game.modding.events.characters.CharacterStatsEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;

import lod.irongoon.models.DivineFruit;
import lod.irongoon.services.randomizer.Randomizer;
import lod.irongoon.services.Characters;
import lod.irongoon.services.DataTables;

import java.util.Collections;
import java.util.HashSet;

@Mod(id = Irongoon.MOD_ID)
public class Irongoon {
    public static final String MOD_ID = "irongoon";
    private static final Slugify slug = Slugify.builder().underscoreSeparator(true).customReplacement("'", "").customReplacement("-", "_").build();
    public static RegistryId id(final String entryId) {
        return new RegistryId(MOD_ID, entryId);
    }

    private static final IrongoonConfig config = IrongoonConfig.getInstance(); //can be removed, dependencies of config can be moved somewhere else
    private static final Randomizer randomizer = Randomizer.getInstance();
    private static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> CONFIG_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.config, MOD_ID);
    private static final RegistryDelegate<SeedConfigEntry> IRONGOON_CAMPAIGN_SEED = CONFIG_REGISTRAR.register("irongoon_campaign_seed", () -> new SeedConfigEntry(randomizer.retrieveNewCampaignSeed()));

    private final Characters characters = Characters.getInstance();
    private final DataTables dataTables = DataTables.getInstance();
    private final StaleStats staleStats = StaleStats.getInstance();

    public Irongoon() {
        GameEngine.EVENTS.register(this);
    }

    @EventListener
    public void gameConfig(final ConfigRegistryEvent event) {
        CONFIG_REGISTRAR.registryEvent(event);
    }

    @EventListener
    public void newGame(final NewGameEvent game) {
        if (config.useRandomSeedOnNewCampaign) {
            config.publicSeed = config.campaignSeed;
        }
    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        if (config.useRandomSeedOnNewCampaign) {
            var campaignSeed = GameEngine.CONFIG.getConfig(IRONGOON_CAMPAIGN_SEED.get());
            config.publicSeed = campaignSeed;
            config.seed = Integer.parseUnsignedInt(config.publicSeed, 16);
        }

        refreshState();
    }

    private void refreshState() {
        characters.initialize();
        dataTables.initialize();
    }

    @EventListener
    public void characterStats(final CharacterStatsEvent character) {
        if (!staleStats.isCharacterStale(character)) {
            characters.updateCharacterByReferenceCharacter(character);
            return;
        }

        DivineFruit bodyStatsRandomized = randomizer.doCharacterStats(character);
        DivineFruit dragoonStatsRandomized = randomizer.doDragoonStats(character);
        DivineFruit hpStatRandomized = randomizer.doCharacterHP(character);
        DivineFruit speedStatRandomized = randomizer.doCharacterSpeed(character);

        character.bodyAttack = bodyStatsRandomized.bodyAttack;
        character.bodyDefence = bodyStatsRandomized.bodyDefense;
        character.bodyMagicAttack = bodyStatsRandomized.bodyMagicAttack;
        character.bodyMagicDefence = bodyStatsRandomized.bodyMagicDefense;

        character.dragoonAttack = dragoonStatsRandomized.dragoonAttack;
        character.dragoonDefence = dragoonStatsRandomized.dragoonDefense;
        character.dragoonMagicAttack = dragoonStatsRandomized.dragoonMagicAttack;
        character.dragoonMagicDefence = dragoonStatsRandomized.dragoonMagicDefense;

        character.maxHp = hpStatRandomized.maxHP;
        character.bodySpeed = speedStatRandomized.bodySpeed;

        characters.saveCharacter(character);
    }

    @EventListener
    public void monsterStats(final MonsterStatsEvent monster) {
        DivineFruit monsterStatsRandomized = randomizer.doMonsterStats(monster);
        DivineFruit monsterHPRandomized = randomizer.doMonsterHP(monster);
        DivineFruit monsterSpeedRandomized = randomizer.doMonsterSpeed(monster);
        DivineFruit monsterElementRandomized = randomizer.doMonsterElement(monster);

        randomizer.doMonsterVariance(monsterStatsRandomized, monsterHPRandomized, monsterSpeedRandomized);

        monster.attack = monsterStatsRandomized.bodyAttack;
        monster.defence = monsterStatsRandomized.bodyDefense;
        monster.magicAttack = monsterStatsRandomized.bodyMagicAttack;
        monster.magicDefence = monsterStatsRandomized.bodyMagicDefense;

        monster.maxHp = monsterHPRandomized.maxHP;
        monster.hp = monsterHPRandomized.maxHP;
        monster.speed = monsterSpeedRandomized.bodySpeed;

        monster.elementFlag = Element.fromFlag(monsterElementRandomized.element.getValue()).get();
        monster.elementalImmunityFlag.set(monsterElementRandomized.elementImmunity);
    }
}
