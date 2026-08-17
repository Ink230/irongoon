package lod.irongoon.services;

import legend.core.GameEngine;
import legend.game.additions.Addition;
import legend.game.additions.UnlockState;
import legend.game.characters.AdditionUnlockCriterion;
import legend.game.characters.CharacterAdditionInfo;
import legend.game.characters.CharacterData2c;
import legend.game.characters.Element;
import legend.game.types.GameState52c;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.AdditionHitTiming;
import lod.irongoon.data.AdditionUnlocks;
import lod.irongoon.events.GatherAdditionProfilesEvent;
import lod.irongoon.models.AdditionHitOverride;
import lod.irongoon.models.AdditionProfile;
import lod.irongoon.parse.external.AdditionHitOverrideParser;
import lod.irongoon.parse.game.AdditionUnlockParser;
import lod.irongoon.services.randomizer.AdditionElementRandomizer;
import lod.irongoon.services.randomizer.AdditionHitTimingRandomizer;
import lod.irongoon.services.randomizer.AdditionScalingRandomizer;
import lod.irongoon.services.randomizer.AdditionStatsRandomizer;
import lod.irongoon.services.randomizer.AdditionStatusRandomizer;
import lod.irongoon.services.randomizer.AdditionUnlockRandomizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Additions {
    private static final Additions INSTANCE = new Additions();
    private static final Logger LOGGER = LogManager.getFormatterLogger(Additions.class);
    private static final int MASTERY_UNLOCK_LEVEL = 255;
    private static final Set<String> STOCK_ADDITIONS = Set.of(
        "double_slash", "volcano", "burning_rush", "crush_dance", "madness_hero", "moon_strike", "blazing_dynamo",
        "harpoon", "spinning_cane", "rod_typhoon", "gust_of_wind_dance", "flower_storm",
        "whip_smack", "more_more", "hard_blade", "demons_dance",
        "double_punch", "ferry_of_styx", "summon_4_gods", "five_ring_shattering", "hex_hammer", "omni_sweep",
        "albert_harpoon", "albert_spinning_cane", "albert_rod_typhoon", "albert_gust_of_wind_dance", "albert_flower_storm",
        "double_smack", "hammer_spin", "cool_boogie", "cats_cradle",
        "pursuit", "inferno", "bone_crush"
    );

    public static Additions getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final AdditionUnlockParser additionUnlockParser = AdditionUnlockParser.getInstance();
    private final AdditionHitOverrideParser additionHitOverrideParser = AdditionHitOverrideParser.getInstance();
    private final AdditionUnlockRandomizer unlockRandomizer = AdditionUnlockRandomizer.getInstance();
    private final AdditionStatsRandomizer statsRandomizer = AdditionStatsRandomizer.getInstance();
    private final AdditionScalingRandomizer scalingRandomizer = AdditionScalingRandomizer.getInstance();
    private final AdditionHitTimingRandomizer timingRandomizer = AdditionHitTimingRandomizer.getInstance();
    private final AdditionElementRandomizer elementRandomizer = AdditionElementRandomizer.getInstance();
    private final AdditionStatusRandomizer statusRandomizer = AdditionStatusRandomizer.getInstance();
    private final Map<Integer, AdditionUnlock> unlocks = new HashMap<>();
    private final Map<RegistryId, AdditionProfile> profiles = new LinkedHashMap<>();
    private final Map<CacheKey, ResolvedAddition> resolvedAdditions = new HashMap<>();
    private final Map<RegistryId, AdditionUnlockRandomizer.UnlockSequence> unlockSequences = new HashMap<>();
    private final Map<RegistryId, CharacterAdditionSaveState> campaignAdditionStates = new HashMap<>();
    private Map<RegistryId, Element> elementAssignments = Map.of();
    private Map<RegistryId, AdditionStatusRandomizer.StatusAssignment> statusAssignments = Map.of();
    private GameState52c campaignGameState;

    private Additions() {
    }

    public void initialize() {
        this.unlocks.clear();
        final int additionCount = this.additionUnlockParser.getTotalAdditions();
        for(int index = 0; index < additionCount; index++) {
            final int id = this.additionUnlockParser.getAdditionId(index);
            final String name = this.additionUnlockParser.getAdditionName(index).trim();
            final int unlockLevel = this.additionUnlockParser.getAdditionUnlockLevel(index);
            this.unlocks.put(id, new AdditionUnlock(id, name, unlockLevel));
        }
    }

    public void initializeCampaign(final GameState52c gameState) {
        this.snapshotCampaignAdditions(gameState);
        this.gatherProfiles();
        this.resolvedAdditions.clear();
        this.unlockSequences.clear();

        final Set<RegistryId> assignedAdditionIds = new HashSet<>();
        final Map<RegistryId, ResolvedAddition> availableAdditions = new LinkedHashMap<>();
        final Map<RegistryId, Map<RegistryId, ResolvedAddition>> stockByCharacter = new LinkedHashMap<>();

        for(final CharacterData2c character : gameState.charData_32c) {
            this.applyUnlockSequence(character);

            final Map<RegistryId, ResolvedAddition> stockAdditions = new LinkedHashMap<>();
            for(final RegistryId additionId : character.getAllAdditions()) {
                if(!this.profiles.containsKey(additionId)) continue;
                assignedAdditionIds.add(additionId);
                final CharacterAdditionInfo additionInfo = character.getAdditionInfo(additionId);
                final Addition baseAddition = GameEngine.REGISTRIES.additions.getEntry(additionId).get();
                final ResolvedAddition resolved = ResolvedAddition.stock(additionId, baseAddition, character, additionInfo);
                stockAdditions.put(additionId, resolved);
                availableAdditions.putIfAbsent(additionId, resolved);
            }
            stockByCharacter.put(character.template.getRegistryId(), stockAdditions);
        }

        final List<AdditionHitOverride> overrides = this.loadTimingOverrides();
        this.validateTimingOverrides(availableAdditions, overrides);

        for(final CharacterData2c character : gameState.charData_32c) {
            final RegistryId characterId = character.template.getRegistryId();
            final Map<RegistryId, ResolvedAddition> stock = stockByCharacter.get(characterId);
            Map<RegistryId, ResolvedAddition> resolved = stock;
            resolved = this.statsRandomizer.randomize(characterId, resolved, this.capableIds(character, AdditionProfile::statReplacementSafe));
            resolved = this.scalingRandomizer.randomize(characterId, resolved, this.capableIds(character, AdditionProfile::scalingReplacementSafe));
            resolved = this.timingRandomizer.randomize(
                characterId,
                resolved,
                this.capableIds(character, AdditionProfile::hitTimingReplacementSafe),
                overrides
            );

            for(final Map.Entry<RegistryId, ResolvedAddition> entry : resolved.entrySet()) {
                this.validateResolvedAddition(entry.getKey(), entry.getValue());
                if(entry.getValue() != stock.get(entry.getKey())) {
                    this.resolvedAdditions.put(new CacheKey(characterId, entry.getKey()), entry.getValue());
                }
            }
        }

        this.elementAssignments = this.elementRandomizer.randomize(assignedAdditionIds);
        this.statusAssignments = this.statusRandomizer.randomize(assignedAdditionIds);

        for(final CharacterData2c character : gameState.charData_32c) {
            this.applyRuntimeUnlockProjection(character);
        }
    }

    public CharacterAdditionSaveState getCampaignAdditionSaveState(final CharacterData2c character) {
        if(character.gameState != this.campaignGameState) return null;
        if(this.config.additionUnlocks == AdditionUnlocks.STOCK) return null;
        if(!this.unlockSequences.containsKey(character.template.getRegistryId())) return null;

        final CharacterAdditionSaveState state = this.campaignAdditionStates.get(character.template.getRegistryId());
        return state == null ? null : state.withProgress(character);
    }

    public Addition resolve(final CharacterData2c character, final RegistryId additionId, final Addition baseAddition) {
        final ResolvedAddition resolved = this.resolvedAdditions.get(new CacheKey(character.template.getRegistryId(), additionId));
        return resolved != null ? resolved : baseAddition;
    }

    public Element resolveElement(final RegistryId additionId) {
        return this.elementAssignments.get(additionId);
    }

    public AdditionStatusRandomizer.StatusAssignment resolveStatus(final RegistryId additionId) {
        return this.statusAssignments.get(additionId);
    }

    public AdditionProfile profile(final RegistryId additionId) {
        return this.profiles.get(additionId);
    }

    public boolean usesRandomizedUnlock(final CharacterData2c character, final RegistryId additionId) {
        final AdditionUnlockRandomizer.UnlockSequence sequence = this.unlockSequences.get(character.template.getRegistryId());
        return sequence != null && sequence.criteria().containsKey(additionId);
    }

    public int getUnlockLevelById(final int additionId) {
        final AdditionUnlock addition = this.unlocks.get(additionId);
        return addition != null ? addition.unlockLevel : -1;
    }

    public int getUnlockLevelByName(final String additionName) {
        final AdditionUnlock addition = this.unlocks.values().stream()
            .filter(candidate -> candidate.name.equals(additionName.trim()))
            .findFirst()
            .orElse(null);
        return addition != null ? addition.unlockLevel : -1;
    }

    public AdditionUnlock getAdditionById(final int additionId) {
        return this.unlocks.get(additionId);
    }

    public AdditionUnlock getAdditionByName(final String additionName) {
        return this.unlocks.values().stream()
            .filter(candidate -> candidate.name.equals(additionName.trim()))
            .findFirst()
            .orElse(null);
    }

    private void applyRuntimeUnlockProjection(final CharacterData2c character) {
        final AdditionUnlockRandomizer.UnlockSequence sequence = this.unlockSequences.get(character.template.getRegistryId());
        if(sequence == null) return;

        int unlockTimestamp = character.gameState.timestamp_a0;
        for(final RegistryId additionId : sequence.criteria().keySet()) {
            final CharacterAdditionInfo info = character.getAdditionInfo(additionId);

            if(additionId.equals(sequence.starterAdditionId())) {
                info.setUnlockState(UnlockState.UNLOCKED, unlockTimestamp++);
            } else if(additionId.equals(sequence.masteryAdditionId())) {
                info.setUnlockState(UnlockState.UNLOCKABLE, -1);
            } else if(info.checkUnlockCriteria(character)) {
                info.setUnlockState(UnlockState.UNLOCKED, unlockTimestamp++);
            } else {
                info.setUnlockState(UnlockState.LOCKED, -1);
            }
        }

        character.selectedAddition_19 = sequence.starterAdditionId();
    }

    public void unlockEligibleAdditions(final CharacterData2c character) {
        if(this.unlocks.isEmpty()) return;

        final AdditionUnlockRandomizer.UnlockSequence sequence = this.unlockSequences.get(character.template.getRegistryId());
        int unlockTimestamp = character.gameState.timestamp_a0;
        for(final RegistryId additionId : character.getAllAdditions()) {
            final CharacterAdditionInfo info = character.getAdditionInfo(additionId);
            if(info.getUnlockState().isUsable()) continue;

            if(sequence != null && sequence.criteria().containsKey(additionId)) {
                if(info.checkUnlockCriteria(character)) info.unlock(unlockTimestamp++);
                continue;
            }

            final AdditionUnlock stockUnlock = this.getAdditionByName(additionId.entryId().toString());
            if(stockUnlock == null) {
                if(info.checkUnlockCriteria(character)) info.unlock(unlockTimestamp++);
                continue;
            }

            final int unlockLevel = stockUnlock.unlockLevel;
            final boolean levelUnlocked = unlockLevel < MASTERY_UNLOCK_LEVEL && character.level_12 >= unlockLevel;
            final boolean masteryUnlocked = unlockLevel == MASTERY_UNLOCK_LEVEL && info.checkUnlockCriteria(character);
            if(levelUnlocked || masteryUnlocked) info.unlock(unlockTimestamp++);
        }
    }

    private void gatherProfiles() {
        this.profiles.clear();
        final GatherAdditionProfilesEvent gather = GameEngine.EVENTS.postEvent(new GatherAdditionProfilesEvent());
        this.profiles.putAll(gather.profiles());

        for(final String entryId : STOCK_ADDITIONS) {
            final RegistryId additionId = new RegistryId("lod", entryId);
            if(this.profiles.putIfAbsent(additionId, AdditionProfile.stock()) != null) {
                throw new IllegalStateException("A stock addition profile is already registered for " + additionId);
            }
        }

        final Set<RegistryId> registeredAdditionIds = new HashSet<>();
        for(final RegistryId additionId : GameEngine.REGISTRIES.additions) registeredAdditionIds.add(additionId);
        for(final RegistryId additionId : this.profiles.keySet()) {
            if(!registeredAdditionIds.contains(additionId)) {
                throw new IllegalStateException("Addition profile references unavailable registry ID " + additionId);
            }
        }
    }

    private void applyUnlockSequence(final CharacterData2c character) {
        if(this.config.additionUnlocks == AdditionUnlocks.STOCK) return;

        final List<RegistryId> eligibleIds = new ArrayList<>(this.capableIds(character, AdditionProfile::unlockReorderSafe));
        final AdditionUnlockRandomizer.UnlockSequence sequence = this.unlockRandomizer.randomize(character, eligibleIds);
        if(sequence.isStock()) {
            LOGGER.warn("Preserving stock addition unlocks for %s because fewer than two compatible additions are assigned",
                character.template.getRegistryId());
            return;
        }

        this.unlockSequences.put(character.template.getRegistryId(), sequence);
        for(final Map.Entry<RegistryId, AdditionUnlockCriterion> entry : sequence.criteria().entrySet()) {
            final CharacterAdditionInfo current = character.getAdditionInfo(entry.getKey());
            final CharacterAdditionInfo replacement = new CharacterAdditionInfo(List.of(entry.getValue()));
            replacement.level = current.level;
            replacement.xp = current.xp;
            character.addAddition(entry.getKey(), replacement);
            replacement.setUnlockState(UnlockState.LOCKED, -1);
        }
    }

    private void snapshotCampaignAdditions(final GameState52c gameState) {
        if(this.campaignGameState == gameState) return;

        this.campaignGameState = gameState;
        this.campaignAdditionStates.clear();
        for(final CharacterData2c character : gameState.charData_32c) {
            final Map<RegistryId, CharacterAdditionInfo> additions = new LinkedHashMap<>();
            for(final RegistryId additionId : character.getAllAdditions()) {
                additions.put(additionId, new CharacterAdditionInfo(character.getAdditionInfo(additionId)));
            }
            this.campaignAdditionStates.put(
                character.template.getRegistryId(),
                new CharacterAdditionSaveState(character.selectedAddition_19, additions)
            );
        }
    }

    private Set<RegistryId> capableIds(final CharacterData2c character, final Capability capability) {
        final Set<RegistryId> eligible = new HashSet<>();
        for(final RegistryId additionId : character.getAllAdditions()) {
            final AdditionProfile profile = this.profiles.get(additionId);
            if(profile != null && capability.supported(profile)) eligible.add(additionId);
        }
        return Set.copyOf(eligible);
    }

    private List<AdditionHitOverride> loadTimingOverrides() {
        if(this.config.additionHitTiming != AdditionHitTiming.OVERRIDES
            && this.config.additionHitTiming != AdditionHitTiming.RANDOMIZE_WITH_OVERRIDES) {
            return List.of();
        }

        final List<AdditionHitOverride> overrides = this.additionHitOverrideParser.load();
        if(overrides.isEmpty()) {
            throw new IllegalStateException("Addition timing override mode requires at least one override row");
        }
        return overrides;
    }

    private void validateTimingOverrides(
        final Map<RegistryId, ResolvedAddition> availableAdditions,
        final List<AdditionHitOverride> overrides
    ) {
        this.timingRandomizer.validateOverrides(availableAdditions, overrides);
        for(final AdditionHitOverride override : overrides) {
            final AdditionProfile profile = this.profiles.get(override.additionId());
            if(profile == null || !profile.hitTimingReplacementSafe()) {
                throw new IllegalStateException("Addition timing override is unsupported by the profile for " + override.additionId());
            }
        }
    }

    private void validateResolvedAddition(final RegistryId additionId, final ResolvedAddition addition) {
        final var hits = addition.copyHits();
        this.timingRandomizer.validateResolved(additionId, hits);
        for(int hitIndex = 0; hitIndex < hits.length; hitIndex++) {
            if(hits[hitIndex].damageMultiplier_04 < 0 || hits[hitIndex].sp_05 < 0) {
                throw new IllegalStateException("Invalid resolved addition stats for " + additionId + " hit " + (hitIndex + 1)
                    + ": damage and SP must be nonnegative");
            }
        }
        validateMultipliers(additionId, "damage", addition.copyDamageMultipliers());
        validateMultipliers(additionId, "SP", addition.copySpMultipliers());
    }

    private static void validateMultipliers(final RegistryId additionId, final String field, final float[] multipliers) {
        for(int level = 0; level < multipliers.length; level++) {
            if(!Float.isFinite(multipliers[level]) || multipliers[level] < 0.0f) {
                throw new IllegalStateException("Invalid resolved addition " + field + " multiplier for " + additionId
                    + " level " + (level + 1) + ": " + multipliers[level]);
            }
        }
    }

    @FunctionalInterface
    private interface Capability {
        boolean supported(AdditionProfile profile);
    }

    private record CacheKey(RegistryId characterId, RegistryId additionId) {
    }

    public record CharacterAdditionSaveState(RegistryId selectedAddition, Map<RegistryId, CharacterAdditionInfo> additions) {
        public CharacterAdditionSaveState {
            final Map<RegistryId, CharacterAdditionInfo> copy = new LinkedHashMap<>();
            for(final Map.Entry<RegistryId, CharacterAdditionInfo> entry : additions.entrySet()) {
                copy.put(entry.getKey(), new CharacterAdditionInfo(entry.getValue()));
            }
            additions = Collections.unmodifiableMap(copy);
        }

        private CharacterAdditionSaveState withProgress(final CharacterData2c character) {
            if(!character.getAllAdditions().equals(this.additions.keySet())) {
                throw new IllegalStateException("Campaign addition topology changed after Irongoon initialization for "
                    + character.template.getRegistryId());
            }

            final Map<RegistryId, CharacterAdditionInfo> additions = new LinkedHashMap<>();
            for(final Map.Entry<RegistryId, CharacterAdditionInfo> entry : this.additions.entrySet()) {
                final CharacterAdditionInfo saved = new CharacterAdditionInfo(entry.getValue());
                final CharacterAdditionInfo live = character.getAdditionInfo(entry.getKey());
                saved.level = live.level;
                saved.xp = live.xp;
                additions.put(entry.getKey(), saved);
            }
            return new CharacterAdditionSaveState(this.selectedAddition, additions);
        }
    }

    public static class AdditionUnlock {
        public final int id;
        public final String name;
        public final int unlockLevel;

        public AdditionUnlock(final int id, final String name, final int unlockLevel) {
            this.id = id;
            this.name = name;
            this.unlockLevel = unlockLevel;
        }
    }
}
