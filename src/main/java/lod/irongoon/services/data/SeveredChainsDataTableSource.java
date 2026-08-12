package lod.irongoon.services.data;

import legend.game.combat.types.MonsterStats1c;
import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;

import java.util.ArrayList;
import java.util.List;

import static legend.game.combat.Monsters.monsterStats_8010ba98;

public final class SeveredChainsDataTableSource implements DataTableSource {
    private static final SeveredChainsDataTableSource INSTANCE = new SeveredChainsDataTableSource();

    public static SeveredChainsDataTableSource getInstance() {
        return INSTANCE;
    }

    private SeveredChainsDataTableSource() {
    }

    @Override
    public String name() {
        return "Severed Chains";
    }

    @Override
    public boolean supports(final ExternalData data) {
        return data == ExternalData.MONSTER_STATS;
    }

    @Override
    public DataTable load(final ExternalData data) {
        if (!this.supports(data)) {
            throw new IllegalArgumentException(this.name() + " does not provide a complete snapshot for " + data);
        }

        final List<String[]> rows = new ArrayList<>();
        rows.add(new String[] {"ID", "HP", "MP", "AT", "MAT", "SPD", "DF", "MDF", "A-AV", "M-AV", "SPECIAL", "UK", "Element", "Element Null", "Status Resist", "Target Arrow X", "Target Arrow Y", "Target Arrow Z", "UK2", "UK3", "UK4", "UK5", "UK6", "UK7", "UK8", "TotalStats", "Name"});

        for (int id = 0; id < monsterStats_8010ba98.length; id++) {
            rows.add(this.toRow(id, monsterStats_8010ba98[id]));
        }

        return new DataTable(rows);
    }

    private String[] toRow(final int id, final MonsterStats1c stats) {
        final int totalStats = stats.attack_04 + stats.magicAttack_06 + stats.defence_09 + stats.magicDefence_0a;

        return strings(
            id,
            stats.hp_00,
            stats.mp_02,
            stats.attack_04,
            stats.magicAttack_06,
            stats.speed_08,
            stats.defence_09,
            stats.magicDefence_0a,
            stats.attackAvoid_0b,
            stats.magicAvoid_0c,
            stats.specialEffectFlag_0d,
            0, // Legacy UK column no longer modeled by SC
            stats.elementFlag_0f,
            stats.elementalImmunityFlag_10,
            stats.statusResistFlag_11,
            stats.targetArrowX_12,
            stats.targetArrowY_13,
            stats.targetArrowZ_14,
            stats.hitCounterFrameThreshold_15,
            stats._16,
            stats._17,
            stats.middleOffsetX_18,
            stats.middleOffsetY_19,
            0, // Legacy UK7 column no longer modeled by SC
            0, // Legacy UK8 column no longer modeled by SC
            totalStats,
            "Monster " + id
        );
    }

    private static String[] strings(final Object... values) {
        final String[] result = new String[values.length];
        for (int index = 0; index < values.length; index++) {
            result[index] = String.valueOf(values[index]);
        }
        return result;
    }
}
