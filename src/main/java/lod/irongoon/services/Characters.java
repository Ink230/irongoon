package lod.irongoon.services;

import legend.game.modding.events.characters.CharacterStatsEvent;
import lod.irongoon.data.Tables;
import lod.irongoon.models.DivineFruit;

import java.util.ArrayList;
import java.util.List;

public class Characters {
    private static final Characters instance = new Characters();
    public static Characters getInstance() {
        return instance;
    }
    private final List<DivineFruit> characters = new ArrayList<>();

    private Characters() {}


    public void initialize() {
        for(int i = 0; i < Tables.getCharacterTable().size(); i++) {
            this.characters.add(new DivineFruit());
        }
    }

    public DivineFruit getCharacterById(int id) {
        return this.characters.get(id);
    }

    public void updateCharacterByReferenceCharacter(CharacterStatsEvent character) {
        var referenceCharacter = getCharacterById(character.characterId);

        character.bodyAttack = referenceCharacter.bodyAttack;
        character.bodyDefence = referenceCharacter.bodyDefense;
        character.bodyMagicAttack = referenceCharacter.bodyMagicAttack;
        character.bodyMagicDefence = referenceCharacter.bodyMagicDefense;

        character.dragoonAttack = referenceCharacter.dragoonAttack;
        character.dragoonDefence = referenceCharacter.dragoonDefense;
        character.dragoonMagicAttack = referenceCharacter.dragoonMagicAttack;
        character.dragoonMagicDefence = referenceCharacter.dragoonMagicDefense;

        character.maxHp = referenceCharacter.maxHP;
        character.bodySpeed = referenceCharacter.bodySpeed;

        character.level = referenceCharacter.level;
        character.dlevel = referenceCharacter.dLevel;
    }

    public void saveCharacter(CharacterStatsEvent referenceCharacter) {
        var character = getCharacterById(referenceCharacter.characterId);

        character.bodyAttack = referenceCharacter.bodyAttack;
        character.bodyDefense = referenceCharacter.bodyDefence;
        character.bodyMagicAttack = referenceCharacter.bodyMagicAttack;
        character.bodyMagicDefense = referenceCharacter.bodyMagicDefence;

        character.dragoonAttack = referenceCharacter.dragoonAttack;
        character.dragoonDefense = referenceCharacter.dragoonDefence;
        character.dragoonMagicAttack = referenceCharacter.dragoonMagicAttack;
        character.dragoonMagicDefense = referenceCharacter.dragoonMagicDefence;

        character.maxHP = referenceCharacter.maxHp;
        character.bodySpeed = referenceCharacter.bodySpeed;

        character.level = referenceCharacter.level;
        character.dLevel = referenceCharacter.dlevel;
    }
}
