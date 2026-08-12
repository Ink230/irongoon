package lod.irongoon.services.randomizer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import legend.game.characters.CharacterData2c;
import lod.irongoon.config.IrongoonConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class BattlePartyRandomizer {
    private static final BattlePartyRandomizer INSTANCE = new BattlePartyRandomizer();
    private static final int MAXIMUM_BATTLE_PARTY_SIZE = 3;

    public static BattlePartyRandomizer getInstance() { return INSTANCE; }

    private BattlePartyRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public IntList maintainStock(final IntList battleParty) {
        return battleParty;
    }

    public IntList randomizeCampaign(final List<CharacterData2c> characterData) {
        return this.randomize(characterData, true);
    }

    public IntList randomizeBattle(final List<CharacterData2c> characterData) {
        return this.randomize(characterData, false);
    }

    private IntList randomize(final List<CharacterData2c> characterData, final boolean seeded) {
        final int battlePartySize = this.config.battlePartySize;
        if (battlePartySize < 1 || battlePartySize > MAXIMUM_BATTLE_PARTY_SIZE) {
            throw new IllegalStateException(
                    "Battle party size " + battlePartySize + " is invalid; expected 1 to " + MAXIMUM_BATTLE_PARTY_SIZE
            );
        }

        final int[] battlePartyPool;
        if (this.config.battlePartyPool.isEmpty()) {
            battlePartyPool = IntStream.range(0, characterData.size())
                    .filter(i -> (characterData.get(i).partyFlags_04 & 0x1) != 0)
                    .toArray();
        } else {
            final Set<Integer> configuredCharacters = new HashSet<>();
            for (final Integer characterId : this.config.battlePartyPool) {
                if (characterId == null || characterId < 0 || characterId >= characterData.size()) {
                    throw new IllegalStateException("Battle party pool contains invalid character " + characterId);
                }

                if ((characterData.get(characterId).partyFlags_04 & 0x1) == 0) {
                    throw new IllegalStateException("Battle party pool contains unavailable character " + characterId);
                }

                if (!this.config.battlePartyDuplicates && !configuredCharacters.add(characterId)) {
                    throw new IllegalStateException("Battle party pool contains duplicate character " + characterId);
                }
            }

            battlePartyPool = this.config.battlePartyPool.stream().mapToInt(Integer::intValue).toArray();
        }

        final var availablePool = new ArrayList<Integer>();
        for (final int characterId : battlePartyPool) {
            availablePool.add(characterId);
        }

        final Set<Integer> overriddenCharacters = new HashSet<>();
        final var overrides = new Integer[battlePartySize];
        for (var slot = 0; slot < this.config.battlePartyOverride.size() && slot < battlePartySize; slot++) {
            final Integer override = this.config.battlePartyOverride.get(slot);
            if (override == null || override < 0) continue;

            if (override >= characterData.size() || (characterData.get(override).partyFlags_04 & 0x1) == 0) {
                throw new IllegalStateException(
                        "Battle party override for slot " + slot + " uses unavailable character " + override
                );
            }

            if (!this.config.battlePartyDuplicates && !overriddenCharacters.add(override)) {
                throw new IllegalStateException("Battle party override contains duplicate character " + override);
            }

            overrides[slot] = override;
            if (!this.config.battlePartyDuplicates) {
                availablePool.remove(override);
            }
        }

        final Random random = seeded ? new Random(this.config.seed) : new Random();
        final var randomizedBattleParty = new IntArrayList();
        for (var slot = 0; slot < battlePartySize; slot++) {
            if (overrides[slot] != null) {
                randomizedBattleParty.add(overrides[slot]);
                continue;
            }

            if (availablePool.isEmpty()) break;

            final int selectedIndex = random.nextInt(availablePool.size());
            randomizedBattleParty.add(availablePool.get(selectedIndex).intValue());
            if (!this.config.battlePartyDuplicates) {
                availablePool.remove(selectedIndex);
            }
        }

        if (randomizedBattleParty.isEmpty()) {
            throw new IllegalStateException("Battle party has no available characters; expected at least one");
        }

        return randomizedBattleParty;
    }
}
