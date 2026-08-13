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

        final int power = this.config.dragoonSpellRandomizePower
            ? this.resolvePower(characterId, spellId, baseSpell, pool)
            : baseSpell.multi_04;
        final int mp = this.config.dragoonSpellRandomizeMpCost
            ? this.resolveField(characterId, spellId, baseSpell.mp_06, pool, spell -> spell.mp_06, this.config.dragoonSpellMpCostLowerBound, this.config.dragoonSpellMpCostUpperBound, 0x4d50L)
            : baseSpell.mp_06;
        final int accuracy = this.config.dragoonSpellRandomizeAccuracy
            ? this.resolveField(characterId, spellId, baseSpell.accuracy_05, pool, spell -> spell.accuracy_05, this.config.dragoonSpellAccuracyLowerBound, this.config.dragoonSpellAccuracyUpperBound, 0x414343L)
            : baseSpell.accuracy_05;
        final int statusChance = this.config.dragoonSpellRandomizeStatusChance
            ? this.resolveField(characterId, spellId, baseSpell.statusChance_07, pool, spell -> spell.statusChance_07, this.config.dragoonSpellStatusChanceLowerBound, this.config.dragoonSpellStatusChanceUpperBound, 0x53544154L)
            : baseSpell.statusChance_07;
        return new ScalarStats(power, Math.max(0, mp), accuracy, statusChance);
    }

    private int resolvePower(final RegistryId characterId, final RegistryId spellId, final SpellStats0c baseSpell, final List<SpellStats0c> pool) {
        final long salt = 0x504f574552L;
        final DragoonSpellStats mode = this.fieldMode(characterId, spellId, salt);
        if(mode == DragoonSpellStats.STOCK) return this.spellPower(baseSpell);
        if(mode == DragoonSpellStats.SHUFFLE) return this.spellPower(this.shuffledSource(characterId, spellId, pool, salt));
        return this.percentBound(this.spellPower(baseSpell), new Random(this.seed(characterId, spellId) ^ salt));
    }

    private int resolveField(
        final RegistryId characterId,
        final RegistryId spellId,
        final int stock,
        final List<SpellStats0c> pool,
        final java.util.function.ToIntFunction<SpellStats0c> field,
        final int lower,
        final int upper,
        final long salt
    ) {
        final DragoonSpellStats mode = this.fieldMode(characterId, spellId, salt);
        if(mode == DragoonSpellStats.STOCK) return stock;
        if(mode == DragoonSpellStats.SHUFFLE) return field.applyAsInt(this.shuffledSource(characterId, spellId, pool, salt));
        return this.between(lower, upper, new Random(this.seed(characterId, spellId) ^ salt));
    }

    private DragoonSpellStats fieldMode(final RegistryId characterId, final RegistryId spellId, final long salt) {
        if(this.config.dragoonSpellStats != DragoonSpellStats.RANDOMIZE_RANDOM) return this.config.dragoonSpellStats;
        return DragoonSpellStats.values()[new Random(this.seed(characterId, spellId) ^ salt).nextInt(3)];
    }

    private SpellStats0c shuffledSource(final RegistryId characterId, final RegistryId spellId, final List<SpellStats0c> pool, final long salt) {
        final List<SpellStats0c> sorted = new java.util.ArrayList<>(pool);
        sorted.sort(java.util.Comparator.comparing(spell -> spell.getRegistryId().toString()));
        final int targetIndex = Math.max(0, java.util.stream.IntStream.range(0, sorted.size())
            .filter(index -> sorted.get(index).getRegistryId().equals(spellId))
            .findFirst()
            .orElse(Math.floorMod(spellId.hashCode(), sorted.size())));
        final List<SpellStats0c> shuffled = new java.util.ArrayList<>(sorted);
        java.util.Collections.shuffle(shuffled, new Random(this.config.seed ^ STATS_SEED_SALT ^ characterId.hashCode() ^ salt));
        return shuffled.get(targetIndex);
    }

    private int spellPower(final SpellStats0c spell) {
        for(final var effect : spell.getEffectPlan().effects()) {
            if(effect instanceof final legend.game.combat.spells.DamageSpellEffect damage) return damage.power();
            if(effect instanceof final legend.game.combat.spells.HealHpSpellEffect heal) return heal.potency();
            if(effect instanceof final legend.game.combat.spells.RestoreMpSpellEffect restore) return restore.potency();
            if(effect instanceof final legend.game.combat.spells.RestoreSpSpellEffect restore) return restore.potency();
            if(effect instanceof final legend.game.combat.spells.ReviveSpellEffect revive) return revive.hpPercent();
            if(effect instanceof final legend.game.combat.spells.RegenHpSpellEffect regen) return regen.potency();
            if(effect instanceof final legend.game.combat.spells.RegenMpSpellEffect regen) return regen.potency();
            if(effect instanceof final legend.game.combat.spells.RegenSpSpellEffect regen) return regen.potency();
        }
        return spell.multi_04;
    }

    public int normalizedPower(final SpellStats0c spell) {
        return this.spellPower(spell);
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
            return new ScalarStats(INSTANCE.spellPower(spell), spell.mp_06, spell.accuracy_05, spell.statusChance_07);
        }
    }
}
