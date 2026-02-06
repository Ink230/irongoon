package lod.irongoon.services.randomizer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import legend.game.types.CharacterData2c;
import lod.irongoon.config.IrongoonConfig;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class BattlePartyRandomizer {
    private static final BattlePartyRandomizer INSTANCE = new BattlePartyRandomizer();
    public static BattlePartyRandomizer getInstance() { return INSTANCE; }

    private BattlePartyRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public IntList maintainStock(final IntList battleParty) {
        return battleParty;
    }

    public IntList randomizeCampaign(final CharacterData2c[] characterData) {
        return battlePartyRandomizer(characterData, true);
    }

    public IntList randomizeBattle(final CharacterData2c[] characterData) {
        return battlePartyRandomizer(characterData, false);
    }

    private IntList battlePartyRandomizer(final CharacterData2c[] characterData, final boolean seeded) {
        final var battlePartyDuplicates = config.battlePartyDuplicates;
        final var battlePartySize = config.battlePartySize;

        final var battlePartyPool = config.battlePartyPool.isEmpty()
                ? IntStream.range(0, characterData.length)
                    .filter(i -> (characterData[i].partyFlags_04 & 0x1) != 0)
                    .toArray()
                : config.battlePartyPool.stream()
                    .mapToInt(Integer::intValue)
                    .filter(i -> i >= 0 && i < characterData.length)
                    .filter(i -> (characterData[i].partyFlags_04 & 0x1) != 0)
                    .toArray();

        final var random = seeded ? new Random(config.seed) : new Random();

        final var availablePool = new ArrayList<Integer>();
        for (var index : battlePartyPool) {
            availablePool.add(index);
        }

        final var randomizedBattleParty = new IntArrayList();
        for (var slot = 0; slot < battlePartySize; slot++) {
            if (availablePool.isEmpty()) {
                continue;
            }

            final var selectedIndex = random.nextInt(availablePool.size());
            randomizedBattleParty.add(availablePool.get(selectedIndex));

            if (!battlePartyDuplicates) {
                availablePool.remove(selectedIndex);
            }
        }

        for (var slot = 0; slot < config.battlePartyOverride.size() && slot < battlePartySize; slot++) {
            final var override = config.battlePartyOverride.get(slot);
            if (override == null || override < 0) continue;

            if (slot < randomizedBattleParty.size()) {
                randomizedBattleParty.removeInt(slot);
                randomizedBattleParty.add(slot, override);
            } else {
                randomizedBattleParty.add(override);
            }
        }


        return randomizedBattleParty;
    }
}
