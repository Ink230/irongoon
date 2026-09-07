package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.AdditionValueMode;
import lod.irongoon.services.ResolvedAddition;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public final class AdditionScalingRandomizer {
    private static final AdditionScalingRandomizer INSTANCE = new AdditionScalingRandomizer();
    private static final long SEED_SALT = 0x75f0_c3b1_249a_6de8L;
    private static final long DAMAGE_SALT = 0x2a0f_558dL;
    private static final long SP_SALT = 0x7e31_90b4L;

    public static AdditionScalingRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private AdditionScalingRandomizer() {
    }

    public Map<RegistryId, ResolvedAddition> randomize(
        final RegistryId characterId,
        final Map<RegistryId, ResolvedAddition> additions,
        final Set<RegistryId> eligibleAdditionIds
    ) {
        if(this.config.additionLevelScaling == AdditionValueMode.STOCK || eligibleAdditionIds.isEmpty()) return additions;

        final List<RegistryId> sortedIds = sorted(eligibleAdditionIds);
        final Map<RegistryId, float[]> shuffledDamage = this.shuffledCurves(
            characterId, sortedIds, additions, DAMAGE_SALT, ResolvedAddition::copyDamageMultipliers
        );
        final Map<RegistryId, float[]> shuffledSp = this.shuffledCurves(
            characterId, sortedIds, additions, SP_SALT, ResolvedAddition::copySpMultipliers
        );
        final Map<RegistryId, ResolvedAddition> resolved = new LinkedHashMap<>(additions);

        for(final RegistryId additionId : sortedIds) {
            final ResolvedAddition addition = additions.get(additionId);
            float[] damageMultipliers = addition.copyDamageMultipliers();
            float[] spMultipliers = addition.copySpMultipliers();
            boolean resolvedDamage = addition.hasResolvedDamage();
            boolean resolvedSp = addition.hasResolvedSp();

            if(this.config.additionRandomizeDamageScaling) {
                final AdditionValueMode mode = this.effectiveMode(characterId, additionId, DAMAGE_SALT);
                damageMultipliers = this.resolveCurve(
                    mode,
                    characterId,
                    additionId,
                    damageMultipliers,
                    shuffledDamage,
                    this.config.additionDamageScalingLowerPercentBound,
                    this.config.additionDamageScalingUpperPercentBound,
                    DAMAGE_SALT
                );
                resolvedDamage |= mode != AdditionValueMode.STOCK;
            }

            if(this.config.additionRandomizeSpScaling) {
                final AdditionValueMode mode = this.effectiveMode(characterId, additionId, SP_SALT);
                spMultipliers = this.resolveCurve(
                    mode,
                    characterId,
                    additionId,
                    spMultipliers,
                    shuffledSp,
                    this.config.additionSpScalingLowerPercentBound,
                    this.config.additionSpScalingUpperPercentBound,
                    SP_SALT
                );
                resolvedSp |= mode != AdditionValueMode.STOCK;
            }

            resolved.put(additionId, new ResolvedAddition(
                additionId,
                addition.baseAddition(),
                addition.copyHits(),
                damageMultipliers,
                spMultipliers,
                resolvedDamage,
                resolvedSp
            ));
        }

        return Map.copyOf(resolved);
    }

    private float[] resolveCurve(
        final AdditionValueMode mode,
        final RegistryId characterId,
        final RegistryId additionId,
        final float[] stockCurve,
        final Map<RegistryId, float[]> shuffled,
        final int lowerBound,
        final int upperBound,
        final long fieldSalt
    ) {
        return switch(mode) {
            case STOCK -> stockCurve;
            case SHUFFLE -> shuffled.get(additionId).clone();
            case RANDOMIZE_BOUNDS -> this.randomizeBounds(characterId, additionId, stockCurve, lowerBound, upperBound, fieldSalt);
            case RANDOMIZE_RANDOM -> throw new IllegalStateException("RANDOMIZE_RANDOM must be resolved before calculating addition scaling");
        };
    }

    private float[] randomizeBounds(
        final RegistryId characterId,
        final RegistryId additionId,
        final float[] stockCurve,
        final int lowerBound,
        final int upperBound,
        final long fieldSalt
    ) {
        final Random random = this.random(characterId, additionId, fieldSalt);
        final float[] randomized = stockCurve.clone();
        for(int level = 0; level < randomized.length; level++) {
            randomized[level] = Math.max(0.0f, stockCurve[level] * random.nextInt(lowerBound, upperBound + 1) / 100.0f);
        }
        return randomized;
    }

    private Map<RegistryId, float[]> shuffledCurves(
        final RegistryId characterId,
        final List<RegistryId> sortedIds,
        final Map<RegistryId, ResolvedAddition> additions,
        final long fieldSalt,
        final Function<ResolvedAddition, float[]> getter
    ) {
        final Map<Integer, List<RegistryId>> idsByLevelCount = new HashMap<>();
        for(final RegistryId additionId : sortedIds) {
            idsByLevelCount.computeIfAbsent(getter.apply(additions.get(additionId)).length, ignored -> new ArrayList<>()).add(additionId);
        }

        final Map<RegistryId, float[]> shuffled = new LinkedHashMap<>();
        for(final Map.Entry<Integer, List<RegistryId>> group : idsByLevelCount.entrySet()) {
            final List<RegistryId> ids = group.getValue();
            ids.sort((left, right) -> left.toString().compareTo(right.toString()));
            final List<float[]> curves = new ArrayList<>(ids.size());
            for(final RegistryId additionId : ids) curves.add(getter.apply(additions.get(additionId)));
            Collections.shuffle(curves, new Random(this.seed(characterId, fieldSalt ^ group.getKey())));
            for(int index = 0; index < ids.size(); index++) shuffled.put(ids.get(index), curves.get(index).clone());
        }
        return shuffled;
    }

    private AdditionValueMode effectiveMode(final RegistryId characterId, final RegistryId additionId, final long fieldSalt) {
        if(this.config.additionLevelScaling != AdditionValueMode.RANDOMIZE_RANDOM) return this.config.additionLevelScaling;
        final AdditionValueMode[] choices = {AdditionValueMode.STOCK, AdditionValueMode.SHUFFLE, AdditionValueMode.RANDOMIZE_BOUNDS};
        return choices[this.random(characterId, additionId, fieldSalt ^ 0x12c7_4a9eL).nextInt(choices.length)];
    }

    private Random random(final RegistryId characterId, final RegistryId additionId, final long fieldSalt) {
        return new Random(this.seed(characterId, fieldSalt) ^ Integer.toUnsignedLong(additionId.toString().hashCode()));
    }

    private long seed(final RegistryId characterId, final long fieldSalt) {
        return this.config.seed ^ SEED_SALT ^ fieldSalt ^ Integer.toUnsignedLong(characterId.toString().hashCode());
    }

    private static List<RegistryId> sorted(final Set<RegistryId> ids) {
        final List<RegistryId> sorted = new ArrayList<>(ids);
        sorted.sort((left, right) -> left.toString().compareTo(right.toString()));
        return sorted;
    }
}
