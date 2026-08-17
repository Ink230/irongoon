package lod.irongoon.services.randomizer;

import legend.core.GameEngine;
import legend.game.additions.Addition;
import legend.game.characters.AdditionLevelUnlockCriterion;
import legend.game.characters.AdditionUnlockCriterion;
import legend.game.characters.CharacterAdditionInfo;
import legend.game.characters.CharacterData2c;
import lod.irongoon.config.IrongoonConfig;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class AdditionUnlockRandomizer {
    private static final AdditionUnlockRandomizer INSTANCE = new AdditionUnlockRandomizer();
    private static final long SEED_SALT = 0x4fbd_31a2_6c18_9e77L;

    public static AdditionUnlockRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private AdditionUnlockRandomizer() {
    }

    public UnlockSequence randomize(final CharacterData2c character, final List<RegistryId> eligibleAdditionIds) {
        if(eligibleAdditionIds.size() < 2) return UnlockSequence.stock();

        final List<RegistryId> remaining = new ArrayList<>(eligibleAdditionIds);
        remaining.sort((left, right) -> left.toString().compareTo(right.toString()));
        final Random random = new Random(this.seed(character.template.getRegistryId()));

        final RegistryId masteryAdditionId = remaining.remove(random.nextInt(remaining.size()));
        final RegistryId starterAdditionId = remaining.remove(random.nextInt(remaining.size()));
        Collections.shuffle(remaining, random);

        final List<Integer> unlockLevels = this.generateUnlockLevels(remaining.size(), random);
        final Map<RegistryId, AdditionUnlockCriterion> criteria = new LinkedHashMap<>();
        criteria.put(starterAdditionId, new AdditionLevelUnlockCriterion(1));
        for(int index = 0; index < remaining.size(); index++) {
            criteria.put(remaining.get(index), new AdditionLevelUnlockCriterion(unlockLevels.get(index)));
        }
        criteria.put(masteryAdditionId, this.masteryCriterion(masteryAdditionId));

        return new UnlockSequence(starterAdditionId, masteryAdditionId, criteria);
    }

    private List<Integer> generateUnlockLevels(final int count, final Random random) {
        final int lowerBound = this.config.additionUnlockLevelLowerBound;
        final int upperBound = this.config.additionUnlockLevelUpperBound;
        final int rangeSize = upperBound - lowerBound + 1;
        final List<Integer> levels = new ArrayList<>(count);

        if(rangeSize >= count) {
            final List<Integer> pool = new ArrayList<>(rangeSize);
            for(int level = lowerBound; level <= upperBound; level++) pool.add(level);
            Collections.shuffle(pool, random);
            levels.addAll(pool.subList(0, count));
        } else {
            for(int index = 0; index < count; index++) levels.add(random.nextInt(lowerBound, upperBound + 1));
        }

        Collections.sort(levels);
        return levels;
    }

    private AdditionUnlockCriterion masteryCriterion(final RegistryId masteryAdditionId) {
        return (character, additionInfo) -> {
            for(final RegistryId additionId : character.getAllAdditions()) {
                if(additionId.equals(masteryAdditionId)) continue;
                final CharacterAdditionInfo info = character.getAdditionInfo(additionId);
                final Addition addition = GameEngine.REGISTRIES.additions.getEntry(additionId).get();
                if(addition.countsTowardsMastery(character, info) && !addition.isComplete(character, info)) return false;
            }
            return true;
        };
    }

    private long seed(final RegistryId characterId) {
        return this.config.seed ^ SEED_SALT ^ Integer.toUnsignedLong(characterId.toString().hashCode());
    }

    public record UnlockSequence(
        RegistryId starterAdditionId,
        RegistryId masteryAdditionId,
        Map<RegistryId, AdditionUnlockCriterion> criteria
    ) {
        private static UnlockSequence stock() {
            return new UnlockSequence(null, null, Map.of());
        }

        public UnlockSequence {
            criteria = Collections.unmodifiableMap(new LinkedHashMap<>(criteria));
        }

        public boolean isStock() {
            return this.criteria.isEmpty();
        }
    }
}
