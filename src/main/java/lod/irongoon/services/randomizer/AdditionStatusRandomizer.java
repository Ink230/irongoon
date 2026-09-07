package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.AdditionStatuses;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class AdditionStatusRandomizer {
    private static final AdditionStatusRandomizer INSTANCE = new AdditionStatusRandomizer();
    private static final long SEED_SALT = 0x30ca_75f1_b942_68deL;

    public static AdditionStatusRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private AdditionStatusRandomizer() {
    }

    public Map<RegistryId, StatusAssignment> randomize(final Set<RegistryId> assignedAdditionIds) {
        if(this.config.additionStatuses == AdditionStatuses.STOCK) return Map.of();

        final List<Integer> statusMasks = this.allowedStatusMasks();
        if(statusMasks.isEmpty()) throw new IllegalStateException("Addition status randomization has no allowed statuses");

        final List<RegistryId> additionIds = new ArrayList<>(assignedAdditionIds);
        additionIds.sort((left, right) -> left.toString().compareTo(right.toString()));
        final Map<RegistryId, StatusAssignment> assignments = new LinkedHashMap<>();
        for(final RegistryId additionId : additionIds) {
            final Random random = new Random(this.config.seed ^ SEED_SALT ^ Integer.toUnsignedLong(additionId.toString().hashCode()));
            assignments.put(additionId, new StatusAssignment(
                statusMasks.get(random.nextInt(statusMasks.size())),
                random.nextInt(this.config.additionStatusChanceLowerBound, this.config.additionStatusChanceUpperBound + 1)
            ));
        }
        return Map.copyOf(assignments);
    }

    private List<Integer> allowedStatusMasks() {
        final List<Integer> masks = new ArrayList<>(8);
        if(this.config.additionStatusAllowPetrify) masks.add(0x01);
        if(this.config.additionStatusAllowBewitch) masks.add(0x02);
        if(this.config.additionStatusAllowConfuse) masks.add(0x04);
        if(this.config.additionStatusAllowFear) masks.add(0x08);
        if(this.config.additionStatusAllowStun) masks.add(0x10);
        if(this.config.additionStatusAllowWeaponBlock) masks.add(0x20);
        if(this.config.additionStatusAllowDispirit) masks.add(0x40);
        if(this.config.additionStatusAllowPoison) masks.add(0x80);
        return masks;
    }

    public record StatusAssignment(int statusMask, int chance) {
    }
}
