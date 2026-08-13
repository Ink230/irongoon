package lod.irongoon.api;

import org.legendofdragoon.modloader.events.Event;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GatherDragoonSpellProfilesEvent extends Event {
    private final Map<RegistryId, DragoonSpellProfile> profiles = new LinkedHashMap<>();

    public GatherDragoonSpellProfilesEvent() { }

  public void register(final RegistryId spellId, final DragoonSpellProfile profile) {
    if(spellId == null) throw new IllegalArgumentException("Registered Dragoon spell ID cannot be null");
    if(profile == null) throw new IllegalArgumentException("Registered Dragoon spell profile cannot be null");
    if(this.profiles.putIfAbsent(spellId, profile) != null) {
      throw new IllegalStateException("A Dragoon spell profile is already registered for " + spellId);
        }
    }

    public Map<RegistryId, DragoonSpellProfile> profiles() {
        return Map.copyOf(this.profiles);
    }
}
