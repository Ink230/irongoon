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

    public void initialize() {
        for(CharacterData character : CharacterData.values()) {
            addCharacter(character, new DivineFruit());
        }
    }

    private void addCharacter(CharacterData name, DivineFruit character) {
        characters.put(String.valueOf(name), new DivineFruit(character));
    }

    public DivineFruit getCharacter(CharacterData name) {
        return new DivineFruit(characters.get(String.valueOf(name)));
    }
}
