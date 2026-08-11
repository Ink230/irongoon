package lod.irongoon.services;

import legend.game.characters.CharacterData2c;
import legend.game.characters.CharacterTemplate;
import legend.game.combat.types.MonsterStats1c;
import legend.game.types.GameState52c;
import lod.irongoon.data.CharacterData;
import lod.irongoon.data.ExternalData;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static legend.game.combat.Monsters.monsterStats_8010ba98;
import static legend.lodmod.LodCharacterTemplates.ALBERT;
import static legend.lodmod.LodCharacterTemplates.DART;
import static legend.lodmod.LodCharacterTemplates.HASCHEL;
import static legend.lodmod.LodCharacterTemplates.KONGOL;
import static legend.lodmod.LodCharacterTemplates.LAVITZ;
import static legend.lodmod.LodCharacterTemplates.MERU;
import static legend.lodmod.LodCharacterTemplates.MIRANDA;
import static legend.lodmod.LodCharacterTemplates.ROSE;
import static legend.lodmod.LodCharacterTemplates.SHANA;
import static legend.lodmod.LodMod.ATTACK_STAT;
import static legend.lodmod.LodMod.DEFENSE_STAT;
import static legend.lodmod.LodMod.DRAGOON_ATTACK_STAT;
import static legend.lodmod.LodMod.DRAGOON_DEFENSE_STAT;
import static legend.lodmod.LodMod.DRAGOON_MAGIC_ATTACK_STAT;
import static legend.lodmod.LodMod.DRAGOON_MAGIC_DEFENSE_STAT;
import static legend.lodmod.LodMod.HP_STAT;
import static legend.lodmod.LodMod.MAGIC_ATTACK_STAT;
import static legend.lodmod.LodMod.MAGIC_DEFENSE_STAT;
import static legend.lodmod.LodMod.MP_STAT;
import static legend.lodmod.LodMod.SPEED_STAT;

final class SeveredChainsDataTables {
    private static final int CHARACTER_MAX_LEVEL = 60;
    private static final int DRAGOON_MAX_LEVEL = 5;

    private SeveredChainsDataTables() { }

    static Map<ExternalData, List<String[]>> load(final GameState52c gameState) {
        if (gameState == null) {
            throw new IllegalStateException("Severed Chains game state is unavailable while loading Irongoon data");
        }

        final Map<ExternalData, List<String[]>> tables = new EnumMap<>(ExternalData.class);
        final List<String[]> characterStats = new ArrayList<>();
        final List<String[]> dragoonStats = new ArrayList<>();
        characterStats.add(new String[] {"Speed", "AT", "MAT", "DF", "MDF", "HP", "Unused Addition Unlock Level", "Total Stat Points", "Total Stat Points No Speed", "Name"});
        dragoonStats.add(new String[] {"Max MP", "Spell Learned", "UK1", "AT", "MAT", "DF", "MDF", "Total Stats", "Name"});

        final List<RegistryDelegate<? extends CharacterTemplate>> templates = List.of(
            DART, LAVITZ, SHANA, ROSE, HASCHEL, ALBERT, MERU, KONGOL, MIRANDA
        );

        // Temporary characters are not added to the game state, so Irongoon's post-level handlers ignore them.
        // SC pre-level events still participate, which keeps the derived tables aligned with runtime mod data.
        try {
            for (final CharacterData characterId : CharacterData.values()) {
                final CharacterTemplate template = templates.get(characterId.getValue()).get();
                final CharacterData2c character = template.make(gameState);

                characterStats.add(characterRow(characterId, 0, null));
                characterStats.add(characterRow(characterId, character.level_12, character));
                while (character.level_12 < CHARACTER_MAX_LEVEL) {
                    template.applyLevelUp(character, null);
                    characterStats.add(characterRow(characterId, character.level_12, character));
                }

                dragoonStats.add(dragoonRow(characterId, 0, null));
                dragoonStats.add(dragoonRow(characterId, character.dlevel_13, character));
                while (character.dlevel_13 < DRAGOON_MAX_LEVEL) {
                    template.applyDragoonLevelUp(character, null);
                    dragoonStats.add(dragoonRow(characterId, character.dlevel_13, character));
                }
            }
        } catch (final RuntimeException exception) {
            throw new IllegalStateException("Failed to load character or Dragoon stats from Severed Chains character templates", exception);
        }

        tables.put(ExternalData.CHARACTER_STATS, characterStats);
        tables.put(ExternalData.DRAGOON_STATS, dragoonStats);
        try {
            tables.put(ExternalData.MONSTER_STATS, monsterRows());
        } catch (final RuntimeException exception) {
            throw new IllegalStateException("Failed to load monster stats from the Severed Chains monster table", exception);
        }
        return tables;
    }

    private static String[] characterRow(final CharacterData characterId, final int level, final CharacterData2c character) {
        if (character == null) {
            return new String[] {"0", "0", "0", "0", "0", "0", "255", "0", "0", characterId + " Lv" + level};
        }

        final int speed = character.stats.getStat(SPEED_STAT.get()).getRaw();
        final int attack = character.stats.getStat(ATTACK_STAT.get()).getRaw();
        final int magicAttack = character.stats.getStat(MAGIC_ATTACK_STAT.get()).getRaw();
        final int defense = character.stats.getStat(DEFENSE_STAT.get()).getRaw();
        final int magicDefense = character.stats.getStat(MAGIC_DEFENSE_STAT.get()).getRaw();
        final int hp = character.stats.getStat(HP_STAT.get()).getMaxRaw();
        final int totalWithoutSpeed = attack + magicAttack + defense + magicDefense;

        return strings(speed, attack, magicAttack, defense, magicDefense, hp, 255, totalWithoutSpeed + speed, totalWithoutSpeed, characterId + " Lv" + level);
    }

    private static String[] dragoonRow(final CharacterData characterId, final int level, final CharacterData2c character) {
        if (character == null) {
            return new String[] {"0", "255", "255", "255", "255", "255", "255", "0", characterId + " DLv" + level};
        }

        final int mp = character.stats.getStat(MP_STAT.get()).getMaxRaw();
        final int attack = character.stats.getStat(DRAGOON_ATTACK_STAT.get()).getRaw();
        final int magicAttack = character.stats.getStat(DRAGOON_MAGIC_ATTACK_STAT.get()).getRaw();
        final int defense = character.stats.getStat(DRAGOON_DEFENSE_STAT.get()).getRaw();
        final int magicDefense = character.stats.getStat(DRAGOON_MAGIC_DEFENSE_STAT.get()).getRaw();
        final int total = attack + magicAttack + defense + magicDefense;

        return strings(mp, 255, 255, attack, magicAttack, defense, magicDefense, total, characterId + " DLv" + level);
    }

    private static List<String[]> monsterRows() {
        final List<String[]> rows = new ArrayList<>();
        rows.add(new String[] {"ID", "HP", "MP", "AT", "MAT", "SPD", "DF", "MDF", "A-AV", "M-AV", "SPECIAL", "UK", "Element", "Element Null", "Status Resist", "Target Arrow X", "Target Arrow Y", "Target Arrow Z", "UK2", "UK3", "UK4", "UK5", "UK6", "UK7", "UK8", "TotalStats", "Name"});

        for (int id = 0; id < monsterStats_8010ba98.length; id++) {
            final MonsterStats1c stats = monsterStats_8010ba98[id];
            final int total = stats.attack_04 + stats.magicAttack_06 + stats.speed_08 + stats.defence_09 + stats.magicDefence_0a;
            rows.add(strings(id, stats.hp_00, stats.mp_02, stats.attack_04, stats.magicAttack_06, stats.speed_08, stats.defence_09, stats.magicDefence_0a, stats.attackAvoid_0b, stats.magicAvoid_0c, stats.specialEffectFlag_0d, 0, stats.elementFlag_0f, stats.elementalImmunityFlag_10, stats.statusResistFlag_11, stats.targetArrowX_12, stats.targetArrowY_13, stats.targetArrowZ_14, stats.hitCounterFrameThreshold_15, stats._16, stats._17, stats.middleOffsetX_18, stats.middleOffsetY_19, 0, 0, total, "Monster " + id));
        }

        return rows;
    }

    private static String[] strings(final Object... values) {
        final String[] strings = new String[values.length];
        for (int index = 0; index < values.length; index++) {
            strings[index] = String.valueOf(values[index]);
        }
        return strings;
    }
}
