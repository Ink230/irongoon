package lod.irongoon.services.randomizer;

import legend.game.inventory.SpellStats0c;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.DragoonSpellStats;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.List;
import java.util.Random;

public final class DragoonSpellStatsRandomizer {
    private static final DragoonSpellStatsRandomizer INSTANCE = new DragoonSpellStatsRandomizer();
    private static final long STATS_SEED_SALT = 0x4453505354415453L;

    public static DragoonSpellStatsRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private DragoonSpellStatsRandomizer() { }

    public ScalarStats resolve(final RegistryId characterId, final RegistryId spellId, final SpellStats0c baseSpell, final List<SpellStats0c> pool) {
        if(this.config.dragoonSpellStats == DragoonSpellStats.STOCK) return ScalarStats.from(baseSpell);

        final Random random = new Random(this.seed(characterId, spellId));
        final DragoonSpellStats mode = this.config.dragoonSpellStats == DragoonSpellStats.RANDOMIZE_RANDOM
            ? DragoonSpellStats.values()[random.nextInt(3)]
            : this.config.dragoonSpellStats;
        if(mode == DragoonSpellStats.STOCK) return ScalarStats.from(baseSpell);

        final SpellStats0c source = mode == DragoonSpellStats.SHUFFLE ? pool.get(random.nextInt(pool.size())) : baseSpell;
        final int power = this.config.dragoonSpellRandomizePower
            ? mode == DragoonSpellStats.SHUFFLE ? source.multi_04 : this.percentBound(baseSpell.multi_04, random)
            : baseSpell.multi_04;
        final int mp = this.config.dragoonSpellRandomizeMpCost
            ? mode == DragoonSpellStats.SHUFFLE ? source.mp_06 : this.between(this.config.dragoonSpellMpCostLowerBound, this.config.dragoonSpellMpCostUpperBound, random)
            : baseSpell.mp_06;
        final int accuracy = this.config.dragoonSpellRandomizeAccuracy
            ? mode == DragoonSpellStats.SHUFFLE ? source.accuracy_05 : this.between(this.config.dragoonSpellAccuracyLowerBound, this.config.dragoonSpellAccuracyUpperBound, random)
            : baseSpell.accuracy_05;
        final int statusChance = this.config.dragoonSpellRandomizeStatusChance
            ? mode == DragoonSpellStats.SHUFFLE ? source.statusChance_07 : this.between(this.config.dragoonSpellStatusChanceLowerBound, this.config.dragoonSpellStatusChanceUpperBound, random)
            : baseSpell.statusChance_07;
        return new ScalarStats(power, Math.max(0, mp), accuracy, statusChance);
    }

    private int percentBound(final int source, final Random random) {
        return Math.max(0, source * this.between(this.config.dragoonSpellPowerLowerPercentBound, this.config.dragoonSpellPowerUpperPercentBound, random) / 100);
    }

    private int between(final int lower, final int upper, final Random random) {
        return lower == upper ? lower : lower + random.nextInt(upper - lower + 1);
    }

    private long seed(final RegistryId characterId, final RegistryId spellId) {
        return this.config.seed ^ STATS_SEED_SALT ^ Integer.toUnsignedLong(characterId.hashCode()) ^ Long.rotateLeft(Integer.toUnsignedLong(spellId.hashCode()), 19);
    }

    public record ScalarStats(int power, int mp, int accuracy, int statusChance) {
        public static ScalarStats from(final SpellStats0c spell) {
            return new ScalarStats(spell.multi_04, spell.mp_06, spell.accuracy_05, spell.statusChance_07);
        }
    }
}
