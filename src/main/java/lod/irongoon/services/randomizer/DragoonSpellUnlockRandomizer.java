package lod.irongoon.services.randomizer;

import legend.game.additions.UnlockState;
import legend.game.characters.CharacterData2c;
import legend.game.characters.CharacterSpellInfo;
import lod.irongoon.config.IrongoonConfig;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

public final class DragoonSpellUnlockRandomizer {
    private static final DragoonSpellUnlockRandomizer INSTANCE = new DragoonSpellUnlockRandomizer();
    private static final long UNLOCK_SEQUENCE_SEED_SALT = 0x445350454c4c554eL;

    public static DragoonSpellUnlockRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private DragoonSpellUnlockRandomizer() { }

    public void randomize(final CharacterData2c character, final Predicate<RegistryId> eligible, final Predicate<RegistryId> usableFirst) {
        final List<RegistryId> slotIds = new ArrayList<>(character.getAllSpells());
        final List<RegistryId> eligibleIds = slotIds.stream().filter(eligible).toList();
        if(eligibleIds.size() < 2) return;

        final List<RegistryId> shuffledIds = new ArrayList<>(eligibleIds);
        shuffledIds.sort(Comparator.comparing(RegistryId::toString));
        Collections.shuffle(shuffledIds, new Random(this.seedFor(character)));
        if(!usableFirst.test(shuffledIds.getFirst())) {
            for(var index = 1; index < shuffledIds.size(); index++) {
                if(usableFirst.test(shuffledIds.get(index))) {
                    Collections.swap(shuffledIds, 0, index);
                    break;
                }
            }
        }

        final Map<RegistryId, CharacterSpellInfo> slots = new LinkedHashMap<>();
        for(final RegistryId spellId : slotIds) {
            slots.put(spellId, character.getSpellInfo(spellId));
        }

        for(final RegistryId spellId : slotIds) {
            character.removeSpell(spellId);
        }

        var eligibleIndex = 0;
        var slotIndex = 0;
        for(final Map.Entry<RegistryId, CharacterSpellInfo> slot : slots.entrySet()) {
            final RegistryId spellId = eligible.test(slot.getKey()) ? shuffledIds.get(eligibleIndex++) : slot.getKey();
            final CharacterSpellInfo info = slot.getValue();
            if(eligible.test(slot.getKey())) {
                info.setUnlockState(UnlockState.UNLOCKABLE, -1);
                if(info.checkUnlockCriteria(character)) {
                    info.unlock(character.gameState.timestamp_a0 + slotIndex);
                }
            }
            character.addSpell(spellId, info);
            slotIndex++;
        }
    }

    private long seedFor(final CharacterData2c character) {
        return this.config.seed
            ^ UNLOCK_SEQUENCE_SEED_SALT
            ^ Integer.toUnsignedLong(character.template.getRegistryId().toString().hashCode());
    }
}
