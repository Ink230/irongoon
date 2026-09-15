package lod.irongoon.services.randomizer;

import legend.game.additions.AdditionHitProperties10;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.AdditionValueMode;
import lod.irongoon.services.ResolvedAddition;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.ToIntFunction;

public final class AdditionStatsRandomizer {
    private static final AdditionStatsRandomizer INSTANCE = new AdditionStatsRandomizer();
    private static final long SEED_SALT = 0x1c72_884b_51ef_a209L;
    private static final long DAMAGE_SALT = 0x1a03_7821L;
    private static final long SP_SALT = 0x6d25_f0c3L;

    public static AdditionStatsRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private AdditionStatsRandomizer() {
    }

    public Map<RegistryId, ResolvedAddition> randomize(
        final RegistryId characterId,
        final Map<RegistryId, ResolvedAddition> additions,
        final Set<RegistryId> eligibleAdditionIds
    ) {
        if(this.config.additionBaseStats == AdditionValueMode.STOCK || eligibleAdditionIds.isEmpty()) return additions;

        final List<RegistryId> sortedIds = sorted(eligibleAdditionIds);
        final Map<RegistryId, Integer> shuffledDamage = this.shuffledTotals(characterId, sortedIds, additions, DAMAGE_SALT, hit -> hit.damageMultiplier_04);
        final Map<RegistryId, Integer> shuffledSp = this.shuffledTotals(characterId, sortedIds, additions, SP_SALT, hit -> hit.sp_05);
        final Map<RegistryId, ResolvedAddition> resolved = new LinkedHashMap<>(additions);

        for(final RegistryId additionId : sortedIds) {
            final ResolvedAddition addition = additions.get(additionId);
            final AdditionHitProperties10[] hits = addition.copyHits();
            boolean resolvedDamage = addition.hasResolvedDamage();
            boolean resolvedSp = addition.hasResolvedSp();

            if(this.config.additionRandomizeDamage) {
                final AdditionValueMode mode = this.effectiveMode(characterId, additionId, DAMAGE_SALT);
                final int target = this.targetTotal(mode, characterId, additionId, hits, shuffledDamage,
                    this.config.additionDamageLowerPercentBound, this.config.additionDamageUpperPercentBound,
                    DAMAGE_SALT, hit -> hit.damageMultiplier_04);
                redistribute(hits, target, hit -> hit.damageMultiplier_04, (hit, value) -> hit.damageMultiplier_04 = value);
                resolvedDamage |= mode != AdditionValueMode.STOCK;
            }

            if(this.config.additionRandomizeSp) {
                final AdditionValueMode mode = this.effectiveMode(characterId, additionId, SP_SALT);
                final int target = this.targetTotal(mode, characterId, additionId, hits, shuffledSp,
                    this.config.additionSpLowerPercentBound, this.config.additionSpUpperPercentBound,
                    SP_SALT, hit -> hit.sp_05);
                redistribute(hits, target, hit -> hit.sp_05, (hit, value) -> hit.sp_05 = value);
                resolvedSp |= mode != AdditionValueMode.STOCK;
            }

            resolved.put(additionId, new ResolvedAddition(
                additionId,
                addition.baseAddition(),
                hits,
                addition.copyDamageMultipliers(),
                addition.copySpMultipliers(),
                resolvedDamage,
                resolvedSp
            ));
        }

        return Map.copyOf(resolved);
    }

    private int targetTotal(
        final AdditionValueMode mode,
        final RegistryId characterId,
        final RegistryId additionId,
        final AdditionHitProperties10[] hits,
        final Map<RegistryId, Integer> shuffled,
        final int lowerBound,
        final int upperBound,
        final long fieldSalt,
        final ToIntFunction<AdditionHitProperties10> getter
    ) {
        final int stockTotal = total(hits, getter);
        return switch(mode) {
            case STOCK -> stockTotal;
            case SHUFFLE -> shuffled.get(additionId);
            case RANDOMIZE_BOUNDS -> percentageTarget(stockTotal, lowerBound, upperBound,
                this.random(characterId, additionId, fieldSalt));
            case RANDOMIZE_RANDOM -> throw new IllegalStateException("RANDOMIZE_RANDOM must be resolved before calculating addition totals");
        };
    }

    private Map<RegistryId, Integer> shuffledTotals(
        final RegistryId characterId,
        final List<RegistryId> sortedIds,
        final Map<RegistryId, ResolvedAddition> additions,
        final long fieldSalt,
        final ToIntFunction<AdditionHitProperties10> getter
    ) {
        final List<Integer> totals = new ArrayList<>(sortedIds.size());
        for(final RegistryId additionId : sortedIds) totals.add(total(additions.get(additionId).copyHits(), getter));
        Collections.shuffle(totals, new Random(this.seed(characterId, fieldSalt)));

        final Map<RegistryId, Integer> shuffled = new LinkedHashMap<>();
        for(int index = 0; index < sortedIds.size(); index++) shuffled.put(sortedIds.get(index), totals.get(index));
        return shuffled;
    }

    private AdditionValueMode effectiveMode(final RegistryId characterId, final RegistryId additionId, final long fieldSalt) {
        if(this.config.additionBaseStats != AdditionValueMode.RANDOMIZE_RANDOM) return this.config.additionBaseStats;
        final AdditionValueMode[] choices = {AdditionValueMode.STOCK, AdditionValueMode.SHUFFLE, AdditionValueMode.RANDOMIZE_BOUNDS};
        return choices[this.random(characterId, additionId, fieldSalt ^ 0x57ad_115eL).nextInt(choices.length)];
    }

    private Random random(final RegistryId characterId, final RegistryId additionId, final long fieldSalt) {
        return new Random(this.seed(characterId, fieldSalt) ^ Integer.toUnsignedLong(additionId.toString().hashCode()));
    }

    private long seed(final RegistryId characterId, final long fieldSalt) {
        return this.config.seed ^ SEED_SALT ^ fieldSalt ^ Integer.toUnsignedLong(characterId.toString().hashCode());
    }

    private static int percentageTarget(final int stockTotal, final int lowerBound, final int upperBound, final Random random) {
        final int percentage = random.nextInt(lowerBound, upperBound + 1);
        return Math.max(0, (int)Math.round(stockTotal * percentage / 100.0));
    }

    private static int total(final AdditionHitProperties10[] hits, final ToIntFunction<AdditionHitProperties10> getter) {
        int total = 0;
        for(final AdditionHitProperties10 hit : hits) total += getter.applyAsInt(hit);
        return total;
    }

    private static void redistribute(
        final AdditionHitProperties10[] hits,
        final int targetTotal,
        final ToIntFunction<AdditionHitProperties10> getter,
        final HitValueSetter setter
    ) {
        final int sourceTotal = total(hits, getter);
        int assigned = 0;
        for(int hitIndex = 0; hitIndex < hits.length - 1; hitIndex++) {
            final int value = sourceTotal == 0 ? 0 : Math.max(0, (int)Math.round(targetTotal * getter.applyAsInt(hits[hitIndex]) / (double)sourceTotal));
            final int boundedValue = Math.min(value, targetTotal - assigned);
            setter.set(hits[hitIndex], boundedValue);
            assigned += boundedValue;
        }
        setter.set(hits[hits.length - 1], targetTotal - assigned);
    }

    private static List<RegistryId> sorted(final Set<RegistryId> ids) {
        final List<RegistryId> sorted = new ArrayList<>(ids);
        sorted.sort((left, right) -> left.toString().compareTo(right.toString()));
        return sorted;
    }

    @FunctionalInterface
    private interface HitValueSetter {
        void set(AdditionHitProperties10 hit, int value);
    }
}
