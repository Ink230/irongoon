package lod.irongoon.services;

import lod.irongoon.parse.game.AdditionUnlockParser;

import java.util.HashMap;
import java.util.Map;

public class Additions {
    private static final Additions INSTANCE = new Additions();
    public static Additions getInstance() {
        return INSTANCE;
    }

    private Additions() {}

    private final AdditionUnlockParser additionUnlockParser = AdditionUnlockParser.getInstance();

    private final Map<Integer, AdditionUnlock> additions = new HashMap<>();

    public void initialize() {
        additions.clear();

        int totalAdditions = additionUnlockParser.getTotalAdditions();

        for (int i = 0; i < totalAdditions; i++) {
            int id = additionUnlockParser.getAdditionId(i);
            String name = additionUnlockParser.getAdditionName(i).trim();
            int unlockLevel = additionUnlockParser.getAdditionUnlockLevel(i);

            AdditionUnlock addition = new AdditionUnlock(id, name, unlockLevel);
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