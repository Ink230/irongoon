package lod.irongoon.services.randomizer;

import legend.game.types.CharacterData2c;
import legend.game.types.GameState52c;
import lod.irongoon.config.IrongoonConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class BattlePartyRandomizer {
    private static final BattlePartyRandomizer INSTANCE = new BattlePartyRandomizer();
    public static BattlePartyRandomizer getInstance() { return INSTANCE; }

    private BattlePartyRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final StatsRandomizer statRandomizer = StatsRandomizer.getInstance();

    public int[] maintainStock(final CharacterData2c[] characterData, final int[] battleParty) {
        return battleParty;
    }

    public int[] randomizeCampaign(final CharacterData2c[] characterData, final int[] battleParty) {
        return battlePartyRandomizer(characterData, battleParty, true);
    }

    public int[] randomizeBattle(final CharacterData2c[] characterData, final int[] battleParty) {
        return battlePartyRandomizer(characterData, battleParty, false);
    }

    private int[] battlePartyRandomizer(final CharacterData2c[] characterData, final int[] battleParty, final boolean seeded) {
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


        final var randomizedBattleParty = new int[battlePartySize];
        final var random = seeded ? new Random(config.seed) : new Random();
        final var availablePool = new ArrayList<Integer>();

        for (var index : battlePartyPool) {
            availablePool.add(index);
        }

        for (var slot = 0; slot < battlePartySize; slot++) {
            if (availablePool.isEmpty()) {
                randomizedBattleParty[slot] = -1;
                continue;
            }

            final var selectedIndex = random.nextInt(availablePool.size());
            randomizedBattleParty[slot] = availablePool.get(selectedIndex);

            if (!battlePartyDuplicates) {
                availablePool.remove(selectedIndex);
            }
        }

        for (var slot = 0; slot < config.battlePartyOverride.size() && slot < battlePartySize; slot++) {
            final var override = config.battlePartyOverride.get(slot);
            if (override != null && override >= 0) {
                randomizedBattleParty[slot] = override;
            }
        }

        return randomizedBattleParty;
    }
}
