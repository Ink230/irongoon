package lod.irongoon.data;

public enum CharacterData implements Data<Integer> {
    DART(0),
    LAVITZ(1),
    SHANA(2),
    ROSE(3),
    HASCHEL(4),
    ALBERT(5),
    MERU(6),
    KONGOL(7),
    MIRANDA(8);

    private final int characterData;

    CharacterData(int characterData) {
        this.characterData = characterData;
    }

    public Integer getValue() {
        return characterData;
    }

    public static CharacterData getEnumByIndex(int index) {
        for(CharacterData character : CharacterData.values()) {
            if(character.getValue() == index) {
                return character;
            }
        }
        throw new IllegalArgumentException("No index for character found");
    }
}
