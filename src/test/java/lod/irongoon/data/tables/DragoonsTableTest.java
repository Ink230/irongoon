package lod.irongoon.data.tables;

import lod.irongoon.models.Character;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DragoonsTableTest {
    @Test
    void initialize() {
        final var t = new DragoonsTable();
        try {
            t.initialize();
        } catch(Exception e) {
            fail(e);
        }
        assertNotNull(t);
        assertEquals(9, t.size());
        for (final var c : Character.Name.values()) {
            final var d = t.getDragoon(c);
            assertNotNull(d);
            assertEquals(c.name, d.getName());
            assertNotNull(d.getStats(0));
        }
    }
}