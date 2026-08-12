package lod.irongoon.services.data;

import lod.irongoon.data.ExternalData;
import org.junit.jupiter.api.Test;

import static legend.game.combat.Monsters.monsterStats_8010ba98;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeveredChainsDataTableSourceTest {
    @Test
    void mapsScMonsterStatsToCurrentTableShape() {
        final SeveredChainsDataTableSource source = new SeveredChainsDataTableSource();
        final var table = source.load(ExternalData.MONSTER_STATS);
        final var first = monsterStats_8010ba98[0];
        final var row = table.data.get(1);

        assertEquals(27, row.length);
        assertEquals(String.valueOf(first.hp_00), row[1]);
        assertEquals(String.valueOf(first.attack_04), row[3]);
        assertEquals(
            String.valueOf(first.attack_04 + first.magicAttack_06 + first.defence_09 + first.magicDefence_0a),
            row[25]
        );
        assertTrue(source.supports(ExternalData.MONSTER_STATS));
        assertFalse(source.supports(ExternalData.ADDITION_STATS));
    }
}
