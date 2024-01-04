package lod.irongoon.services;

import legend.game.modding.events.characters.CharacterStatsEvent;

public class StaleStats {
    private static final StaleStats INSTANCE = new StaleStats();
    public static StaleStats getInstance() { return INSTANCE; }

    private StaleStats() {}

    private final Characters characters = Characters.getInstance();

    public boolean isCharacterStale(CharacterStatsEvent character) {
        var referenceCharacter = characters.getCharacterById(character.characterId);
        return referenceCharacter.level < character.level || referenceCharacter.dLevel < character.dlevel;
    }
}