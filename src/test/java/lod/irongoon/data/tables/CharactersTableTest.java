package lod.irongoon.data.tables;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class CharactersTableTest {
    final static CharactersTable t = new CharactersTable();

    @BeforeAll
    static void setUp() {
        try {
            t.initialize();
        } catch (FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void initialize()  {
        assertNotNull(t);
        assertEquals(9, t.size());
        for (final var i : CharactersTable.Index.values()) {
            final var c = t.getCharacter(i);
            assertNotNull(c);
            assertEquals(i.name, c.getName());
            for (int j = 0; j < 61; j++) {
                final var s = c.getStats(j);
                assertNotNull(s);
            }
        }
    }

    @Test
    void getCharacter_index() {
        for (final var i : CharactersTable.Index.values()) {
            final var c = t.getCharacter(i);
            assertNotNull(c);
            assertEquals(i.name, c.getName());
        }
    }

    @Test
    void getCharacter_int() {
        for (final var i : CharactersTable.Index.values()) {
            final var c = t.getCharacter(i.ordinal());
            assertNotNull(c);
            assertEquals(i.name, c.getName());
        }
    }

    @Test
    void getCharacter_string() {
        for (final var i : CharactersTable.Index.values()) {
            final var c = t.getCharacter(i.name);
            assertNotNull(c);
            assertEquals(i.name, c.getName());
        }
    }

    @Test
    void getCharacterStats() {
        for (final var i : CharactersTable.Index.values()) {
            final var c = t.getCharacter(i);
            assertNotNull(c);
            assertEquals(i.name, c.getName());
            for (int j = 0; j < 61; j++) {
                final var s = c.getStats(j);
                assertNotNull(s);
            }
        }
    }

    @Test
    void getNames() {
        final var names = new HashSet<>(t.getNames());
        final var expected = CharactersTable.Index.values();
        assertEquals(expected.length, names.size());
        for (var i : expected) {
            assertTrue(names.contains(i.name), "missing " + i.name);
        }
    }

    @Test
    void size() {
        assertEquals(CharactersTable.Index.values().length, t.size());
    }
}