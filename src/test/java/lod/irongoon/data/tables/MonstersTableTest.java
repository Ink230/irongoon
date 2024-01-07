package lod.irongoon.data.tables;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonstersTableTest {
    @Test
    void initialize() {
        final var t = new MonstersTable();
        try {
            t.initialize();
        } catch (Exception e) {
            fail(e);
        }
        assertNotNull(t);
        assertEquals(225, t.size());
        for (int i = 0; i < t.size(); i++) {
            final var m = t.getMonster(i);
            assertNotNull(m);
            assertNotNull(m.getReward());
            assertNotNull(m.getStats());
        }
    }
}
