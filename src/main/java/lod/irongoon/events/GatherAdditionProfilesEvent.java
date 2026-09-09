package lod.irongoon.events;

import lod.irongoon.models.AdditionProfile;
import org.legendofdragoon.modloader.events.Event;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GatherAdditionProfilesEvent extends Event {
    private final Map<RegistryId, AdditionProfile> profiles = new LinkedHashMap<>();

    public void register(final RegistryId additionId, final AdditionProfile profile) {
        if(additionId == null) throw new IllegalArgumentException("Registered addition ID cannot be null");
        if(profile == null) throw new IllegalArgumentException("Registered addition profile cannot be null");
        if(this.profiles.putIfAbsent(additionId, profile) != null) {
            throw new IllegalStateException("An addition profile is already registered for " + additionId);
        }
    }

    public Map<RegistryId, AdditionProfile> profiles() {
        return Map.copyOf(this.profiles);
    }
}
