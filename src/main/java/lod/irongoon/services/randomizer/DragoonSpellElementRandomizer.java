package lod.irongoon.services.randomizer;

import legend.core.GameEngine;
import legend.game.characters.Element;
import legend.game.inventory.SpellStats0c;
import legend.lodmod.LodMod;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.DragoonSpellElements;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class DragoonSpellElementRandomizer {
    private static final DragoonSpellElementRandomizer INSTANCE = new DragoonSpellElementRandomizer();
    private static final long ELEMENT_SEED_SALT = 0x445350454c454d54L;

    public static DragoonSpellElementRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private DragoonSpellElementRandomizer() { }

    public RegistryDelegate<Element> resolve(final RegistryId characterId, final RegistryId spellId, final SpellStats0c baseSpell, final List<SpellStats0c> pool) {
        if(this.config.dragoonSpellElements == DragoonSpellElements.STOCK) return baseSpell.element_08;

        final Random random = new Random(this.config.seed ^ ELEMENT_SEED_SALT ^ characterId.hashCode() ^ Long.rotateLeft(spellId.hashCode(), 17));
        if(this.config.dragoonSpellElements == DragoonSpellElements.SHUFFLE) {
            final List<SpellStats0c> sorted = new ArrayList<>(pool);
            sorted.sort(Comparator.comparing(spell -> spell.getRegistryId().toString()));
            final int targetIndex = java.util.stream.IntStream.range(0, sorted.size())
                .filter(index -> sorted.get(index).getRegistryId().equals(spellId))
                .findFirst()
                .orElse(Math.floorMod(spellId.hashCode(), sorted.size()));
            final List<RegistryDelegate<Element>> elements = sorted.stream().map(spell -> spell.element_08).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            java.util.Collections.shuffle(elements, new Random(this.config.seed ^ ELEMENT_SEED_SALT ^ characterId.hashCode()));
            return elements.get(targetIndex);
        }

        final List<RegistryDelegate<Element>> elements = new ArrayList<>();
        for(final RegistryId elementId : GameEngine.REGISTRIES.elements) {
            if(!this.config.dragoonSpellNoElement && elementId.equals(LodMod.NO_ELEMENT.getId())) continue;
            elements.add(GameEngine.REGISTRIES.elements.getEntry(elementId));
        }
        elements.sort(Comparator.comparing(element -> element.getId().toString()));
        if(elements.isEmpty()) throw new IllegalStateException("No registered elements are available for Dragoon spell randomization");
        return elements.get(random.nextInt(elements.size()));
    }
}
