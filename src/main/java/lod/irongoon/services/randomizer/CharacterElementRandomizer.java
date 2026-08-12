package lod.irongoon.services.randomizer;

import legend.game.characters.Element;
import legend.lodmod.LodMod;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.CharacterElements;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CharacterElementRandomizer {
  private static final CharacterElementRandomizer INSTANCE = new CharacterElementRandomizer();

  @SuppressWarnings("unchecked")
  private static final RegistryDelegate<Element>[] STOCK_ELEMENTS = new RegistryDelegate[] {
    LodMod.FIRE_ELEMENT,
    LodMod.WIND_ELEMENT,
    LodMod.LIGHT_ELEMENT,
    LodMod.DARK_ELEMENT,
    LodMod.THUNDER_ELEMENT,
    LodMod.WIND_ELEMENT,
    LodMod.WATER_ELEMENT,
    LodMod.EARTH_ELEMENT,
    LodMod.LIGHT_ELEMENT,
  };

  public static CharacterElementRandomizer getInstance() {
    return INSTANCE;
  }

  private CharacterElementRandomizer() { }

  private final IrongoonConfig config = IrongoonConfig.getInstance();
  private final Random random = new Random();
  private RegistryDelegate<Element>[] campaignAssignment;
  private RegistryDelegate<Element>[] battleAssignment;

  public void reset() {
    this.campaignAssignment = null;
    this.battleAssignment = null;
  }

  public void beginBattle() {
    this.battleAssignment = null;

    if(this.config.characterElements == CharacterElements.RANDOM_BATTLE) {
      this.battleAssignment = this.buildAssignment(this.random);
    }
  }

  public RegistryDelegate<Element> resolve(final int characterId) {
    if(characterId < 0 || characterId >= STOCK_ELEMENTS.length) return null;

    return switch(this.config.characterElements) {
      case STOCK -> null;
      case RANDOM_CAMPAIGN -> {
        if(this.campaignAssignment == null) {
          this.campaignAssignment = this.buildAssignment(new Random(this.config.seed));
        }

        yield this.campaignAssignment[characterId];
      }
      case RANDOM_BATTLE -> {
        if(this.battleAssignment == null) {
          this.battleAssignment = this.buildAssignment(this.random);
        }

        yield this.battleAssignment[characterId];
      }
    };
  }

  @SuppressWarnings("unchecked")
  private RegistryDelegate<Element>[] buildAssignment(final Random rng) {
    final var elementList = new ArrayList<>(Arrays.asList(STOCK_ELEMENTS));
    final var overrides = this.extractOverrides(elementList);

    Collections.shuffle(elementList, rng);
    this.restoreOverrides(elementList, overrides);
    this.applyNoElement(elementList, rng, overrides.keySet());
    this.applyDivineElement(elementList, rng, overrides.keySet());

    return elementList.toArray(new RegistryDelegate[0]);
  }

  private Map<Integer, RegistryDelegate<Element>> extractOverrides(final List<RegistryDelegate<Element>> elementList) {
    final Map<Integer, RegistryDelegate<Element>> overrides = new HashMap<>();

    for(int i = 0; i < this.config.characterElementOverride.size() && i < elementList.size(); i++) {
      final var override = this.config.characterElementOverride.get(i);

      if(override != null && !override.trim().isEmpty() && !override.trim().equalsIgnoreCase("skip")) {
        final var element = this.parseElement(override);

        if(element != null) {
          overrides.put(i, element);
        }
      }
    }

    return overrides;
  }

  private void restoreOverrides(final List<RegistryDelegate<Element>> elementList, final Map<Integer, RegistryDelegate<Element>> overrides) {
    for(final var entry : overrides.entrySet()) {
      elementList.set(entry.getKey(), entry.getValue());
    }
  }

  private RegistryDelegate<Element> parseElement(final String elementName) {
    return switch(elementName.toLowerCase().trim()) {
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

  private void applyNoElement(final List<RegistryDelegate<Element>> elementList, final Random rng, final Set<Integer> lockedIndices) {
    if(this.config.characterNoElement && rng.nextBoolean()) {
      final var availableIndices = new ArrayList<Integer>();

      for(var i = 0; i < elementList.size(); i++) {
        if(!lockedIndices.contains(i)) availableIndices.add(i);
      }

      if(!availableIndices.isEmpty()) {
        final var swapIndex = availableIndices.get(rng.nextInt(availableIndices.size()));
        elementList.set(swapIndex, LodMod.NO_ELEMENT);
      }
    }
  }

  private void applyDivineElement(final List<RegistryDelegate<Element>> elementList, final Random rng, final Set<Integer> lockedIndices) {
    if(rng.nextBoolean()) {
      final var availableIndices = new ArrayList<Integer>();

      for(var i = 0; i < elementList.size(); i++) {
        if(!lockedIndices.contains(i)) availableIndices.add(i);
      }

      if(!availableIndices.isEmpty()) {
        final var swapIndex = availableIndices.get(rng.nextInt(availableIndices.size()));
        elementList.set(swapIndex, LodMod.DIVINE_ELEMENT);
      }
    }
  }
}
