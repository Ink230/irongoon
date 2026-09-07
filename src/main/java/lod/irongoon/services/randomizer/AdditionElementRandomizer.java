package lod.irongoon.services.randomizer;

import legend.core.GameEngine;
import legend.game.characters.Element;
import legend.lodmod.LodMod;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.AdditionElements;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class AdditionElementRandomizer {
    private static final AdditionElementRandomizer INSTANCE = new AdditionElementRandomizer();
    private static final long SEED_SALT = 0x29b4_d8e1_73a6_0c5fL;

    public static AdditionElementRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private AdditionElementRandomizer() {
    }

    public Map<RegistryId, Element> randomize(final Set<RegistryId> assignedAdditionIds) {
        if(this.config.additionElements == AdditionElements.STOCK) return Map.of();

        final List<RegistryId> elementIds = new ArrayList<>();
        for(final RegistryId elementId : GameEngine.REGISTRIES.elements) {
            if(!this.config.additionNoElement && elementId.equals(LodMod.NO_ELEMENT.getId())) continue;
            elementIds.add(elementId);
        }
        elementIds.sort((left, right) -> left.toString().compareTo(right.toString()));
        if(elementIds.isEmpty()) throw new IllegalStateException("Addition element randomization has no eligible registered elements");

        final List<RegistryId> additionIds = new ArrayList<>(assignedAdditionIds);
        additionIds.sort((left, right) -> left.toString().compareTo(right.toString()));
        final Map<RegistryId, Element> assignments = new LinkedHashMap<>();
        for(final RegistryId additionId : additionIds) {
            final Random random = new Random(this.config.seed ^ SEED_SALT ^ Integer.toUnsignedLong(additionId.toString().hashCode()));
            final RegistryId elementId = elementIds.get(random.nextInt(elementIds.size()));
            assignments.put(additionId, GameEngine.REGISTRIES.elements.getEntry(elementId).get());
        }
        return Map.copyOf(assignments);
    }
}
