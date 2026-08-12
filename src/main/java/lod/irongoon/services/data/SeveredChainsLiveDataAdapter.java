package lod.irongoon.services.data;

import legend.game.characters.CharacterData2c;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.characters.PreCharacterDragoonLevelUpEvent;
import legend.game.modding.events.characters.PreCharacterLevelUpEvent;
import lod.irongoon.data.CharacterStatsData;
import lod.irongoon.data.DragoonStatsData;
import lod.irongoon.data.EnemyStatsData;
import lod.irongoon.data.ExternalData;
import lod.irongoon.services.DataTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public final class SeveredChainsLiveDataAdapter {
    private static final Logger LOGGER = LogManager.getFormatterLogger(SeveredChainsLiveDataAdapter.class);
    private static final int CHARACTER_LEVELS = 61;
    private static final int DRAGOON_LEVELS = 6;

    private final DataTables dataTables;

    public SeveredChainsLiveDataAdapter() {
        this(DataTables.getInstance());
    }

    SeveredChainsLiveDataAdapter(final DataTables dataTables) {
        this.dataTables = dataTables;
    }

    public void updateMonsterStats(final MonsterStatsEvent event) {
        if (!this.dataTables.allowsLiveUpdates(ExternalData.MONSTER_STATS)) return;

        this.dataTables.updateDataRow(ExternalData.MONSTER_STATS, event.enemyId, row -> {
            row[EnemyStatsData.HP.getValue()] = String.valueOf(event.maxHp);
            row[EnemyStatsData.ATTACK.getValue()] = String.valueOf(event.attack);
            row[EnemyStatsData.MAGIC_ATTACK.getValue()] = String.valueOf(event.magicAttack);
            row[EnemyStatsData.SPEED.getValue()] = String.valueOf(event.speed);
            row[EnemyStatsData.DEFENSE.getValue()] = String.valueOf(event.defence);
            row[EnemyStatsData.MAGIC_DEFENSE.getValue()] = String.valueOf(event.magicDefence);
            row[EnemyStatsData.ATTACK_AVOID.getValue()] = String.valueOf(event.attackAvoid);
            row[EnemyStatsData.MAGIC_ATTACK_AVOID.getValue()] = String.valueOf(event.magicAvoid);
            row[EnemyStatsData.SPECIAL.getValue()] = String.valueOf(event.specialEffectFlag);
            row[EnemyStatsData.ELEMENT.getValue()] = String.valueOf(event.elementFlag.flag);
            row[EnemyStatsData.ELEMENT_IMMUNITY.getValue()] = String.valueOf(event.elementalImmunityFlag.pack());
            row[14] = String.valueOf(event.statusResistFlag);
            row[25] = String.valueOf(event.attack + event.magicAttack + event.defence + event.magicDefence);
            return row;
        });
    }

    public void updateCharacterStats(final PreCharacterLevelUpEvent event) {
        if (event.isCanceled() || !this.dataTables.allowsLiveUpdates(ExternalData.CHARACTER_STATS)) return;

        final int characterId = this.getCharacterId(event.character);
        if (characterId < 0) return;

        final int sourceLevel = event.character.level_12;
        final int destinationLevel = sourceLevel + event.levelsToAdd;
        final int sourceRow = characterId * CHARACTER_LEVELS + sourceLevel;
        final int destinationRow = characterId * CHARACTER_LEVELS + destinationLevel;
        final String[] source = this.dataTables.getDataTable(ExternalData.CHARACTER_STATS).data.get(sourceRow + 1);

        this.dataTables.updateDataRow(ExternalData.CHARACTER_STATS, destinationRow, row -> {
            row[CharacterStatsData.SPEED.getValue()] = add(source, CharacterStatsData.SPEED.getValue(), event.statsToAdd.getInt(SPEED_STAT.get()));
            row[CharacterStatsData.ATTACK.getValue()] = add(source, CharacterStatsData.ATTACK.getValue(), event.statsToAdd.getInt(ATTACK_STAT.get()));
            row[CharacterStatsData.MAGIC_ATTACK.getValue()] = add(source, CharacterStatsData.MAGIC_ATTACK.getValue(), event.statsToAdd.getInt(MAGIC_ATTACK_STAT.get()));
            row[CharacterStatsData.DEFENSE.getValue()] = add(source, CharacterStatsData.DEFENSE.getValue(), event.statsToAdd.getInt(DEFENSE_STAT.get()));
            row[CharacterStatsData.MAGIC_DEFENSE.getValue()] = add(source, CharacterStatsData.MAGIC_DEFENSE.getValue(), event.statsToAdd.getInt(MAGIC_DEFENSE_STAT.get()));
            row[CharacterStatsData.MAX_HP.getValue()] = add(source, CharacterStatsData.MAX_HP.getValue(), event.statsToAdd.getInt(HP_STAT.get()));
            final int totalWithoutSpeed = integer(row, 1) + integer(row, 2) + integer(row, 3) + integer(row, 4);
            row[CharacterStatsData.TOTAL_STATS_NO_SPEED.getValue()] = String.valueOf(totalWithoutSpeed);
            row[CharacterStatsData.TOTAL_STATS.getValue()] = String.valueOf(totalWithoutSpeed + integer(row, 0));
            return row;
        });

        this.warnSkippedLevels(ExternalData.CHARACTER_STATS, characterId, sourceLevel, destinationLevel);
    }

    public void updateDragoonStats(final PreCharacterDragoonLevelUpEvent event) {
        if (event.isCanceled() || !this.dataTables.allowsLiveUpdates(ExternalData.DRAGOON_STATS)) return;

        final int characterId = this.getCharacterId(event.character);
        if (characterId < 0) return;

        final int sourceLevel = event.character.dlevel_13;
        final int destinationLevel = sourceLevel + event.levelsToAdd;
        final int sourceRow = characterId * DRAGOON_LEVELS + sourceLevel;
        final int destinationRow = characterId * DRAGOON_LEVELS + destinationLevel;
        final String[] source = this.dataTables.getDataTable(ExternalData.DRAGOON_STATS).data.get(sourceRow + 1);

        this.dataTables.updateDataRow(ExternalData.DRAGOON_STATS, destinationRow, row -> {
            row[DragoonStatsData.MAX_MP.getValue()] = add(source, DragoonStatsData.MAX_MP.getValue(), event.statsToAdd.getInt(MP_STAT.get()));
            row[DragoonStatsData.DRAGOON_ATTACK.getValue()] = add(source, DragoonStatsData.DRAGOON_ATTACK.getValue(), event.statsToAdd.getInt(DRAGOON_ATTACK_STAT.get()));
            row[DragoonStatsData.DRAGOON_MAGIC_ATTACK.getValue()] = add(source, DragoonStatsData.DRAGOON_MAGIC_ATTACK.getValue(), event.statsToAdd.getInt(DRAGOON_MAGIC_ATTACK_STAT.get()));
            row[DragoonStatsData.DRAGOON_DEFENSE.getValue()] = add(source, DragoonStatsData.DRAGOON_DEFENSE.getValue(), event.statsToAdd.getInt(DRAGOON_DEFENSE_STAT.get()));
            row[DragoonStatsData.DRAGOON_MAGIC_DEFENSE.getValue()] = add(source, DragoonStatsData.DRAGOON_MAGIC_DEFENSE.getValue(), event.statsToAdd.getInt(DRAGOON_MAGIC_DEFENSE_STAT.get()));
            row[DragoonStatsData.TOTAL_STATS.getValue()] = String.valueOf(
                integer(row, 3) + integer(row, 4) + integer(row, 5) + integer(row, 6)
            );
            return row;
        });

        this.warnSkippedLevels(ExternalData.DRAGOON_STATS, characterId, sourceLevel, destinationLevel);
    }

    private int getCharacterId(final CharacterData2c character) {
        return character.gameState.charData_32c.indexOf(character);
    }

    private void warnSkippedLevels(
        final ExternalData data,
        final int characterId,
        final int sourceLevel,
        final int destinationLevel
    ) {
        if (destinationLevel - sourceLevel <= 1) return;

        LOGGER.warn(
            "Updated %s character %d from level %d to %d; intermediate SC rows remain unchanged",
            data,
            characterId,
            sourceLevel,
            destinationLevel
        );
    }

    private static String add(final String[] row, final int column, final int amount) {
        return String.valueOf(integer(row, column) + amount);
    }

    private static int integer(final String[] row, final int column) {
        return Integer.parseInt(row[column]);
    }
}
