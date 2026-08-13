package lod.irongoon.api;

import org.legendofdragoon.modloader.events.Event;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class GatherDragoonSpellProfilesEvent extends Event {
    private final Map<RegistryId, DragoonSpellProfile> profiles = new LinkedHashMap<>();

    public GatherDragoonSpellProfilesEvent() { }

    public void register(final RegistryId spellId, final DragoonSpellProfile profile) {
        final RegistryId checkedSpellId = Objects.requireNonNull(spellId, "spellId");
        if(this.profiles.putIfAbsent(checkedSpellId, Objects.requireNonNull(profile, "profile")) != null) {
            throw new IllegalStateException("A Dragoon spell profile is already registered for " + checkedSpellId);
        }
    }

    public Map<RegistryId, DragoonSpellProfile> profiles() {
        return Map.copyOf(this.profiles);
    }
}
