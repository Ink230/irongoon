package lod.irongoon.services.randomizer;

import legend.core.GameEngine;
import legend.game.characters.Element;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.lodmod.LodMod;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.DragoonElements;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class DragoonElementRandomizer {
    private static final DragoonElementRandomizer INSTANCE = new DragoonElementRandomizer();
    private static final int DRAGOON_COUNT = 9;
    private static final long DRAGOON_ELEMENT_SEED_SALT = 0x445241474f4f4eL;

    public static DragoonElementRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final Random random = new Random();
    private final IdentityHashMap<PlayerBattleEntity, RegistryDelegate<Element>> transformAssignments = new IdentityHashMap<>();
    private RegistryDelegate<Element>[] campaignAssignment;
    private RegistryDelegate<Element>[] battleAssignment;

    private DragoonElementRandomizer() {}

    public void reset() {
        this.campaignAssignment = null;
        this.battleAssignment = null;
        this.transformAssignments.clear();
    }

    public void beginBattle() {
        this.battleAssignment = null;
        this.transformAssignments.clear();

        if(this.config.dragoonElements == DragoonElements.RANDOM_BATTLE) {
            this.battleAssignment = this.buildAssignment(this.random);
        }
    }

    public void endBattle() {
        this.battleAssignment = null;
        this.transformAssignments.clear();
    }

    public void synchronize(final PlayerBattleEntity bent) {
        if(!bent.isDragoon()) this.transformAssignments.remove(bent);
    }

    public RegistryDelegate<Element> resolve(final int characterId, final PlayerBattleEntity bent) {
        if(characterId < 0 || characterId >= DRAGOON_COUNT) return null;

        return switch(this.config.dragoonElements) {
            case STOCK -> null;
            case RANDOM_CAMPAIGN -> {
                if(this.campaignAssignment == null) {
                    this.campaignAssignment = this.buildAssignment(new Random(this.config.seed ^ DRAGOON_ELEMENT_SEED_SALT));
                }

                yield this.campaignAssignment[characterId];
            }
            case RANDOM_BATTLE -> {
                if(this.battleAssignment == null) {
                    this.battleAssignment = this.buildAssignment(this.random);
                }

                yield this.battleAssignment[characterId];
            }
            case RANDOM_TRANSFORM -> {
                final var override = this.resolveOverride(characterId);
                if(override != null) yield override;
                yield this.transformAssignments.computeIfAbsent(bent, ignored -> this.rollTransformElement());
            }
        };
    }

    @SuppressWarnings("unchecked")
    private RegistryDelegate<Element>[] buildAssignment(final Random rng) {
        final RegistryDelegate<Element>[] assignment = new RegistryDelegate[DRAGOON_COUNT];
        final var overrides = this.extractOverrides(assignment);
        final var normalElements = this.buildNormalElementPool();
        final var availableElements = new ArrayList<>(normalElements);
        availableElements.removeAll(overrides.values());

        var shuffledElements = new ArrayList<>(availableElements.isEmpty() ? normalElements : availableElements);
        Collections.shuffle(shuffledElements, rng);
        var elementIndex = 0;

        for(var characterId = 0; characterId < assignment.length; characterId++) {
            if(assignment[characterId] != null) continue;

            if(elementIndex >= shuffledElements.size()) {
                shuffledElements = new ArrayList<>(normalElements);
                Collections.shuffle(shuffledElements, rng);
                elementIndex = 0;
            }

            if(shuffledElements.isEmpty()) {
                throw new IllegalStateException("No eligible Dragoon elements remain after applying overrides");
            }

            assignment[characterId] = shuffledElements.get(elementIndex++);
        }

        this.applyNoElement(assignment, rng, overrides.keySet());
        this.applyDivineElement(assignment, rng, overrides.keySet());
        return assignment;
    }

    private Map<Integer, RegistryDelegate<Element>> extractOverrides(final RegistryDelegate<Element>[] assignment) {
        final Map<Integer, RegistryDelegate<Element>> overrides = new HashMap<>();

        for(var characterId = 0; characterId < this.config.dragoonElementOverride.size() && characterId < assignment.length; characterId++) {
            final var element = this.resolveOverride(characterId);
            if(element == null) continue;

            assignment[characterId] = element;
            overrides.put(characterId, element);
        }

        return overrides;
    }

    private RegistryDelegate<Element> resolveOverride(final int characterId) {
        if(characterId >= this.config.dragoonElementOverride.size()) return null;

        final var configuredElement = this.config.dragoonElementOverride.get(characterId);
        if(configuredElement == null || configuredElement.isBlank() || configuredElement.trim().equalsIgnoreCase("skip")) return null;

        final var normalizedElement = configuredElement.trim().toLowerCase(Locale.ROOT);
        final var alias = switch(normalizedElement) {
            case "none", "noelement" -> LodMod.NO_ELEMENT;
            case "dark" -> LodMod.DARK_ELEMENT;
            case "water" -> LodMod.WATER_ELEMENT;
            case "fire" -> LodMod.FIRE_ELEMENT;
            case "wind" -> LodMod.WIND_ELEMENT;
            case "earth" -> LodMod.EARTH_ELEMENT;
            case "light" -> LodMod.LIGHT_ELEMENT;
            case "thunder" -> LodMod.THUNDER_ELEMENT;
            case "divine" -> LodMod.DIVINE_ELEMENT;
            default -> null;
        };

        if(alias != null) return alias;

        final RegistryId registryId;
        try {
            registryId = new RegistryId(normalizedElement);
        } catch(final IllegalArgumentException exception) {
            throw this.invalidOverride(characterId, configuredElement, exception);
        }

        if(!GameEngine.REGISTRIES.elements.hasEntry(registryId)) {
            throw this.invalidOverride(characterId, configuredElement, null);
        }

        return GameEngine.REGISTRIES.elements.getEntry(registryId);
    }

    private IllegalStateException invalidOverride(final int characterId, final String configuredElement, final Exception cause) {
        final var message = "Invalid Dragoon element override at character index %d: '%s' is not a registered element or supported alias"
            .formatted(characterId, configuredElement);
        return cause == null ? new IllegalStateException(message) : new IllegalStateException(message, cause);
    }

    private List<RegistryDelegate<Element>> buildNormalElementPool() {
        final var elements = new ArrayList<RegistryDelegate<Element>>();

        for(final var elementId : GameEngine.REGISTRIES.elements) {
            if(elementId.equals(LodMod.NO_ELEMENT.getId()) || elementId.equals(LodMod.DIVINE_ELEMENT.getId())) continue;
            elements.add(GameEngine.REGISTRIES.elements.getEntry(elementId));
        }

        elements.sort((left, right) -> left.getId().toString().compareTo(right.getId().toString()));
        return elements;
    }

    private RegistryDelegate<Element> rollTransformElement() {
        final var elements = this.buildNormalElementPool();
        elements.add(LodMod.DIVINE_ELEMENT);
        if(this.config.dragoonNoElement) elements.add(LodMod.NO_ELEMENT);

        if(elements.isEmpty()) {
            throw new IllegalStateException("No registered elements are available for Dragoon transformation randomization");
        }

        return elements.get(this.random.nextInt(elements.size()));
    }

    private void applyNoElement(final RegistryDelegate<Element>[] assignment, final Random rng, final Set<Integer> lockedIndices) {
        if(this.config.dragoonNoElement && rng.nextBoolean()) {
            this.replaceRandomUnlockedElement(assignment, LodMod.NO_ELEMENT, rng, lockedIndices);
        }
    }

    private void applyDivineElement(final RegistryDelegate<Element>[] assignment, final Random rng, final Set<Integer> lockedIndices) {
        if(rng.nextBoolean()) {
            this.replaceRandomUnlockedElement(assignment, LodMod.DIVINE_ELEMENT, rng, lockedIndices);
        }
    }

    private void replaceRandomUnlockedElement(
        final RegistryDelegate<Element>[] assignment,
        final RegistryDelegate<Element> element,
        final Random rng,
        final Set<Integer> lockedIndices
    ) {
        final var availableIndices = new ArrayList<Integer>();

        for(var characterId = 0; characterId < assignment.length; characterId++) {
            if(!lockedIndices.contains(characterId)) availableIndices.add(characterId);
        }

        if(!availableIndices.isEmpty()) {
            assignment[availableIndices.get(rng.nextInt(availableIndices.size()))] = element;
        }
    }
}
