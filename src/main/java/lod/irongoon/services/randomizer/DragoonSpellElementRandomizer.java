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
            return pool.get(random.nextInt(pool.size())).element_08;
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
