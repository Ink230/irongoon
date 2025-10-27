package lod.irongoon.services.randomizer;

import legend.game.characters.Element;
import legend.lodmod.LodMod;
import lod.irongoon.config.IrongoonConfig;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import java.util.*;

public class CharacterElementRandomizer {
    private static final CharacterElementRandomizer INSTANCE = new CharacterElementRandomizer();
    public static CharacterElementRandomizer getInstance() { return INSTANCE; }

    private CharacterElementRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final Random random = new Random();

    public RegistryDelegate<Element>[] randomizeCampaign(RegistryDelegate<Element>[] characterElements) {
        var elementList = new ArrayList<>(Arrays.asList(characterElements));
        var overrides = extractOverrides(elementList);
        var seededRandom = new Random(config.seed);
        Collections.shuffle(elementList, seededRandom);
        restoreOverrides(elementList, overrides);
        applyNoElement(elementList, seededRandom, overrides.keySet());
        applyDivineElement(elementList, seededRandom, overrides.keySet());
        return elementList.toArray(new RegistryDelegate[0]);
    }

    public RegistryDelegate<Element>[] randomizeBattle(RegistryDelegate<Element>[] characterElements) {
        var elementList = new ArrayList<>(Arrays.asList(characterElements));
        var overrides = extractOverrides(elementList);
        Collections.shuffle(elementList, random);
        restoreOverrides(elementList, overrides);
        applyNoElement(elementList, random, overrides.keySet());
        applyDivineElement(elementList, random, overrides.keySet());
        return elementList.toArray(new RegistryDelegate[0]);
    }

    private Map<Integer, RegistryDelegate<Element>> extractOverrides(List<RegistryDelegate<Element>> elementList) {
        Map<Integer, RegistryDelegate<Element>> overrides = new HashMap<>();

        for (int i = 0; i < config.characterElementOverride.size() && i < elementList.size(); i++) {
            var override = config.characterElementOverride.get(i);
            if (override != null && !override.trim().isEmpty() && !override.trim().equalsIgnoreCase("skip")) {
                var element = parseElement(override);
                if (element != null) {
                    overrides.put(i, element);
                }
            }
        }

        return overrides;
    }

    private void restoreOverrides(List<RegistryDelegate<Element>> elementList, Map<Integer, RegistryDelegate<Element>> overrides) {
        for (var entry : overrides.entrySet()) {
            elementList.set(entry.getKey(), entry.getValue());
        }
    }

    private RegistryDelegate<Element> parseElement(String elementName) {
        return switch (elementName.toLowerCase().trim()) {
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
    }

    private void applyNoElement(List<RegistryDelegate<Element>> elementList, Random rng, Set<Integer> lockedIndices) {
        if (config.characterNoElement && rng.nextBoolean()) {
            var availableIndices = new ArrayList<>();
            for (var i = 0; i < elementList.size(); i++) {
                if (!lockedIndices.contains(i)) {
                    availableIndices.add(i);
                }
            }

            if (!availableIndices.isEmpty()) {
                var swapIndex = (int) availableIndices.get(rng.nextInt(availableIndices.size()));
                elementList.set(swapIndex, LodMod.NO_ELEMENT);
            }
        }
    }

    private void applyDivineElement(List<RegistryDelegate<Element>> elementList, Random rng, Set<Integer> lockedIndices) {
        if (rng.nextBoolean()) {
            var availableIndices = new ArrayList<>();
            for (var i = 0; i < elementList.size(); i++) {
                if (!lockedIndices.contains(i)) {
                    availableIndices.add(i);
                }
            }

            if (!availableIndices.isEmpty()) {
                var swapIndex = (int) availableIndices.get(rng.nextInt(availableIndices.size()));
                elementList.set(swapIndex, LodMod.DIVINE_ELEMENT);
            }
        }
    }
}