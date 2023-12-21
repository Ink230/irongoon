package lod.irongoon.entitymanager;

import lod.irongoon.data.CharacterData;

import java.util.HashMap;
import java.util.Map;

public class Characters {
    private final Map<String, DivineFruit> characters;

    public Characters() {
        this.characters = new HashMap<>();
    }

    public void addCharacter(CharacterData name, DivineFruit character) {
        characters.put(String.valueOf(name), character);
    }

    public DivineFruit getCharacter(CharacterData name) {
        return characters.get(name);
    }
}
