package lod.irongoon.services;

import lod.irongoon.data.CharacterData;
import lod.irongoon.models.DivineFruit;

import java.util.HashMap;
import java.util.Map;

public class Characters {
    private static final Characters instance = new Characters();

    public static Characters getInstance() {
        return instance;
    }

    private Characters() {
        this.characters = new HashMap<>();
    }

    private final Map<String, DivineFruit> characters;

    public void initialize(CharacterData[] values) {
        for(CharacterData character : values) {
            addCharacter(character, new DivineFruit());
        }
    }

    private void addCharacter(CharacterData name, DivineFruit character) {
        characters.put(String.valueOf(name), character);
    }

    public DivineFruit getCharacter(CharacterData name) {
        return characters.get(name);
    }
}
