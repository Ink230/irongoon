package lod.irongoon.services;

import legend.game.additions.UnlockState;
import legend.game.characters.CharacterAdditionInfo;
import legend.game.characters.CharacterData2c;
import lod.irongoon.parse.game.AdditionUnlockParser;

import java.util.HashMap;
import java.util.Map;

public class Additions {
    private static final Additions INSTANCE = new Additions();
    private static final int MASTERY_UNLOCK_LEVEL = 255;

    public static Additions getInstance() {
        return INSTANCE;
    }

    private Additions() {}

    private final AdditionUnlockParser additionUnlockParser = AdditionUnlockParser.getInstance();

    private final Map<Integer, AdditionUnlock> additions = new HashMap<>();

    public void initialize() {
        additions.clear();

        var totalAdditions = additionUnlockParser.getTotalAdditions();

        for (int i = 0; i < totalAdditions; i++) {
            var id = additionUnlockParser.getAdditionId(i);
            var name = additionUnlockParser.getAdditionName(i).trim();
            var unlockLevel = additionUnlockParser.getAdditionUnlockLevel(i);

            var addition = new AdditionUnlock(id, name, unlockLevel);
            additions.put(id, addition);
        }
    }

    public int getUnlockLevelById(int additionId) {
        var addition = additions.get(additionId);
        return addition != null ? addition.unlockLevel : -1;
    }

    public int getUnlockLevelByName(String additionName) {
        var addition = additions.values().stream()
                .filter(a -> a.name.equals(additionName.trim()))
                .findFirst()
                .orElse(null);
        return addition != null ? addition.unlockLevel : -1;
    }

    public AdditionUnlock getAdditionById(int additionId) {
        return additions.get(additionId);
    }

    public AdditionUnlock getAdditionByName(String additionName) {
        return additions.values().stream()
                .filter(a -> a.name.equals(additionName.trim()))
                .findFirst()
                .orElse(null);
    }

    public void resetLevelOneAdditions(final CharacterData2c character) {
        if (character.getAllAdditions().isEmpty()) return;

        var unlockTimestamp = character.gameState.timestamp_a0;

        for (final var additionId : character.getAllAdditions()) {
            final CharacterAdditionInfo info = character.getAdditionInfo(additionId);
            info.level = 1;
            info.xp = 0;

            final int unlockLevel = this.getRequiredUnlockLevel(additionId.entryId().toString());
            if (unlockLevel <= character.level_12) {
                info.setUnlockState(UnlockState.UNLOCKED, unlockTimestamp++);
            } else {
                info.setUnlockState(
                        unlockLevel == MASTERY_UNLOCK_LEVEL ? UnlockState.UNLOCKABLE : UnlockState.LOCKED,
                        -1
                );
            }
        }

        final var unlockedAdditions = character.getUnlockedAdditions();
        if (unlockedAdditions.isEmpty()) {
            throw new IllegalStateException("Character has no additions available at level " + character.level_12);
        }

        character.selectedAddition_19 = unlockedAdditions.getFirst();
    }

    public void unlockEligibleAdditions(final CharacterData2c character) {
        if (this.additions.isEmpty()) return;

        var unlockTimestamp = character.gameState.timestamp_a0;

        for (final var additionId : character.getAllAdditions()) {
            final CharacterAdditionInfo info = character.getAdditionInfo(additionId);
            if (info.getUnlockState().isUsable()) continue;

            final int unlockLevel = this.getRequiredUnlockLevel(additionId.entryId().toString());
            final boolean levelUnlocked = unlockLevel < MASTERY_UNLOCK_LEVEL && character.level_12 >= unlockLevel;
            final boolean masteryUnlocked = unlockLevel == MASTERY_UNLOCK_LEVEL && info.checkUnlockCriteria(character);
            if (levelUnlocked || masteryUnlocked) {
                info.unlock(unlockTimestamp++);
            }
        }
    }

    private int getRequiredUnlockLevel(final String additionName) {
        final var addition = this.getAdditionByName(additionName);
        if (addition == null) {
            throw new IllegalStateException("Addition unlock metadata is unavailable for " + additionName);
        }

        return addition.unlockLevel;
    }

    public static class AdditionUnlock {
        public final int id;
        public final String name;
        public final int unlockLevel;

        public AdditionUnlock(int id, String name, int unlockLevel) {
            this.id = id;
            this.name = name;
            this.unlockLevel = unlockLevel;
        }
    }
}
