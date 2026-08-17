package lod.irongoon.services.randomizer;

import legend.game.additions.AdditionHitProperties10;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.AdditionHitTiming;
import lod.irongoon.models.AdditionHitOverride;
import lod.irongoon.services.ResolvedAddition;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class AdditionHitTimingRandomizer {
    private static final AdditionHitTimingRandomizer INSTANCE = new AdditionHitTimingRandomizer();
    private static final long SEED_SALT = 0x6b92_0dc1_4e75_af38L;
    private static final int MAX_GENERATION_ATTEMPTS = 32;

    public static AdditionHitTimingRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private AdditionHitTimingRandomizer() {
    }

    public Map<RegistryId, ResolvedAddition> randomize(
        final RegistryId characterId,
        final Map<RegistryId, ResolvedAddition> additions,
        final Set<RegistryId> eligibleAdditionIds,
        final List<AdditionHitOverride> overrides
    ) {
        if(this.config.additionHitTiming == AdditionHitTiming.STOCK || eligibleAdditionIds.isEmpty()) return additions;

        final Map<OverrideKey, AdditionHitOverride> overridesByHit = indexOverrides(overrides);
        final List<RegistryId> sortedIds = new ArrayList<>(eligibleAdditionIds);
        sortedIds.sort((left, right) -> left.toString().compareTo(right.toString()));
        final Map<RegistryId, ResolvedAddition> resolved = new LinkedHashMap<>(additions);

        for(final RegistryId additionId : sortedIds) {
            final ResolvedAddition addition = additions.get(additionId);
            final AdditionHitProperties10[] hits = addition.copyHits();
            for(int hitIndex = 0; hitIndex < hits.length; hitIndex++) {
                if(this.usesGeneratedTiming()) {
                    hits[hitIndex] = this.randomizeHit(characterId, additionId, hitIndex, hits[hitIndex]);
                }
                if(this.usesOverrides()) {
                    final AdditionHitOverride override = overridesByHit.get(new OverrideKey(additionId, hitIndex + 1));
                    if(override != null) applyOverride(hits[hitIndex], override);
                }
                validateHit(additionId, hitIndex, hits[hitIndex], this.usesOverrides());
            }

            resolved.put(additionId, new ResolvedAddition(
                additionId,
                addition.baseAddition(),
                hits,
                addition.copyDamageMultipliers(),
                addition.copySpMultipliers(),
                addition.hasResolvedDamage(),
                addition.hasResolvedSp()
            ));
        }

        return Map.copyOf(resolved);
    }

    public void validateOverrides(final Map<RegistryId, ResolvedAddition> additions, final List<AdditionHitOverride> overrides) {
        for(final AdditionHitOverride override : overrides) {
            final ResolvedAddition addition = additions.get(override.additionId());
            if(addition == null) {
                throw new IllegalStateException("Addition timing override references unavailable addition " + override.additionId());
            }
            if(override.hitNumber() > addition.copyHits().length) {
                throw new IllegalStateException("Addition timing override references invalid hit " + override.hitNumber()
                    + " for " + override.additionId());
            }
        }
    }

    public void validateResolved(final RegistryId additionId, final AdditionHitProperties10[] hits) {
        if(hits.length == 0) throw new IllegalStateException("Resolved addition has no hits: " + additionId);
        for(int hitIndex = 0; hitIndex < hits.length; hitIndex++) validateHit(additionId, hitIndex, hits[hitIndex], false);
    }

    private AdditionHitProperties10 randomizeHit(
        final RegistryId characterId,
        final RegistryId additionId,
        final int hitIndex,
        final AdditionHitProperties10 stockHit
    ) {
        final Random random = new Random(this.seed(characterId, additionId, hitIndex));
        for(int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            final int percentage = random.nextInt(
                this.config.additionHitTimingLowerPercentBound,
                this.config.additionHitTimingUpperPercentBound + 1
            );
            final AdditionHitProperties10 generated = new AdditionHitProperties10(stockHit);
            generated.totalFrames_01 = scaled(stockHit.totalFrames_01, percentage);
            generated.overlayHitFrameOffset_02 = scaled(stockHit.overlayHitFrameOffset_02, percentage);
            generated.totalSuccessFrames_03 = scaled(stockHit.totalSuccessFrames_03, percentage);
            generated.overlayStartingFrameOffset_0f = scaled(stockHit.overlayStartingFrameOffset_0f, percentage);
            if(isValid(generated)) return generated;
        }

        throw new IllegalStateException("Could not generate valid addition timing for " + additionId
            + " hit " + (hitIndex + 1) + " after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    private boolean usesGeneratedTiming() {
        return this.config.additionHitTiming == AdditionHitTiming.RANDOMIZE_BOUNDS
            || this.config.additionHitTiming == AdditionHitTiming.RANDOMIZE_WITH_OVERRIDES;
    }

    private boolean usesOverrides() {
        return this.config.additionHitTiming == AdditionHitTiming.OVERRIDES
            || this.config.additionHitTiming == AdditionHitTiming.RANDOMIZE_WITH_OVERRIDES;
    }

    private long seed(final RegistryId characterId, final RegistryId additionId, final int hitIndex) {
        return this.config.seed ^ SEED_SALT
            ^ Integer.toUnsignedLong(characterId.toString().hashCode())
            ^ (Integer.toUnsignedLong(additionId.toString().hashCode()) << 1)
            ^ hitIndex;
    }

    private static int scaled(final int value, final int percentage) {
        return Math.max(0, (int)Math.round(value * percentage / 100.0));
    }

    private static void applyOverride(final AdditionHitProperties10 hit, final AdditionHitOverride override) {
        if(override.totalFrames() != null) hit.totalFrames_01 = override.totalFrames();
        if(override.overlayHitFrameOffset() != null) hit.overlayHitFrameOffset_02 = override.overlayHitFrameOffset();
        if(override.totalSuccessFrames() != null) hit.totalSuccessFrames_03 = override.totalSuccessFrames();
        if(override.overlayStartingFrameOffset() != null) hit.overlayStartingFrameOffset_0f = override.overlayStartingFrameOffset();
    }

    private static void validateHit(
        final RegistryId additionId,
        final int hitIndex,
        final AdditionHitProperties10 hit,
        final boolean exactOverrideMode
    ) {
        if(isValid(hit)) return;
        final String source = exactOverrideMode ? "timing override" : "resolved timing";
        throw new IllegalStateException("Invalid addition " + source + " for " + additionId + " hit " + (hitIndex + 1)
            + ": expected nonnegative values and totalFrames >= overlayHitFrameOffset + totalSuccessFrames + 1, got "
            + hit.totalFrames_01 + "," + hit.overlayHitFrameOffset_02 + "," + hit.totalSuccessFrames_03 + ","
            + hit.overlayStartingFrameOffset_0f);
    }

    private static boolean isValid(final AdditionHitProperties10 hit) {
        return hit.totalFrames_01 >= 0
            && hit.overlayHitFrameOffset_02 >= 0
            && hit.totalSuccessFrames_03 >= 0
            && hit.overlayStartingFrameOffset_0f >= 0
            && hit.totalFrames_01 >= (long)hit.overlayHitFrameOffset_02 + hit.totalSuccessFrames_03 + 1;
    }

    private static Map<OverrideKey, AdditionHitOverride> indexOverrides(final List<AdditionHitOverride> overrides) {
        final Map<OverrideKey, AdditionHitOverride> indexed = new HashMap<>();
        for(final AdditionHitOverride override : overrides) {
            indexed.put(new OverrideKey(override.additionId(), override.hitNumber()), override);
        }
        return indexed;
    }

    private record OverrideKey(RegistryId additionId, int hitNumber) {
    }
}
