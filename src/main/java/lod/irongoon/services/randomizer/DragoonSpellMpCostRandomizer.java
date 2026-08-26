package lod.irongoon.services.randomizer;

import legend.game.combat.bent.PlayerBattleEntity;
import lod.irongoon.config.IrongoonConfig;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class DragoonSpellMpCostRandomizer {
    private static final DragoonSpellMpCostRandomizer INSTANCE = new DragoonSpellMpCostRandomizer();
    private static final long MP_COST_SEED_SALT = 0x4453504d50434f53L;
    private static final long UNLOCK_SEED_MULTIPLIER = 0x9e3779b97f4a7c15L;

    public static DragoonSpellMpCostRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final Random random = new Random();
    private final Map<CostKey, Integer> battleCosts = new HashMap<>();
    private final Map<CostKey, Integer> transformCosts = new HashMap<>();

    private DragoonSpellMpCostRandomizer() { }

    public void reset() {
        this.battleCosts.clear();
        this.transformCosts.clear();
    }

    public void beginBattle() {
        this.battleCosts.clear();
        this.transformCosts.clear();
    }

    public void endBattle() {
        this.battleCosts.clear();
        this.transformCosts.clear();
    }

    public void synchronize(final PlayerBattleEntity bent) {
        if(bent.isDragoon()) return;

        final RegistryId characterId = bent.character.template.getRegistryId();
        this.transformCosts.keySet().removeIf(key -> key.characterId().equals(characterId));
    }

    public int resolve(final RegistryId characterId, final int unlockIndex, final int stockMp) {
        if(unlockIndex < 0) return stockMp;

        final CostKey key = new CostKey(characterId, unlockIndex);
        return switch(this.config.dragoonSpellMpCosts) {
            case STOCK -> stockMp;
            case RANDOM_CAMPAIGN_UNLOCK -> this.seededCost(this.unlockSeed(unlockIndex));
            case RANDOM_CAMPAIGN_CHARACTER -> this.seededCost(this.unlockSeed(unlockIndex) ^ Integer.toUnsignedLong(characterId.hashCode()));
            case RANDOM_BATTLE -> this.battleCosts.computeIfAbsent(key, ignored -> this.randomCost(this.random));
            case RANDOM_TRANSFORM -> this.transformCosts.computeIfAbsent(key, ignored -> this.randomCost(this.random));
        };
    }

    private int seededCost(final long modifier) {
        return this.randomCost(new Random(this.config.seed ^ MP_COST_SEED_SALT ^ modifier));
    }

    private long unlockSeed(final int unlockIndex) {
        return (unlockIndex + 1L) * UNLOCK_SEED_MULTIPLIER;
    }

    private int randomCost(final Random rng) {
        final int lower = this.config.dragoonSpellMpCostLowerBound;
        final int upper = this.config.dragoonSpellMpCostUpperBound;
        return lower == upper ? lower : (int)rng.nextLong(lower, (long)upper + 1L);
    }

    private record CostKey(RegistryId characterId, int unlockIndex) { }
}
