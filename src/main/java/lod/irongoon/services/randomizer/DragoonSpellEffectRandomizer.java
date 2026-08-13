package lod.irongoon.services.randomizer;

import legend.game.combat.spells.ApplyStatusSpellEffect;
import legend.game.combat.spells.CleanseSpellEffect;
import legend.game.combat.spells.DamageSpellEffect;
import legend.game.combat.spells.DrainHpSpellEffect;
import legend.game.combat.spells.DrainMpSpellEffect;
import legend.game.combat.spells.DrainSpSpellEffect;
import legend.game.combat.spells.ExecutionMode;
import legend.game.combat.spells.HealHpSpellEffect;
import legend.game.combat.spells.RegenHpSpellEffect;
import legend.game.combat.spells.RegenMpSpellEffect;
import legend.game.combat.spells.RegenSpSpellEffect;
import legend.game.combat.spells.RestoreMpSpellEffect;
import legend.game.combat.spells.RestoreSpSpellEffect;
import legend.game.combat.spells.ReviveSpellEffect;
import legend.game.combat.spells.SpellEffect;
import legend.game.combat.spells.SpellEffectPlan;
import legend.game.combat.spells.SpellStat;
import legend.game.combat.spells.SpellTargetProfile;
import legend.game.combat.spells.StatModifierSpellEffect;
import legend.game.combat.spells.TargetLifeState;
import legend.game.combat.spells.TargetScope;
import legend.game.combat.spells.TargetSide;
import lod.irongoon.api.DragoonSpellEffectKind;
import lod.irongoon.api.DragoonSpellProfile;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.DragoonSpellEffects;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class DragoonSpellEffectRandomizer {
    private static final DragoonSpellEffectRandomizer INSTANCE = new DragoonSpellEffectRandomizer();
    private static final long EFFECT_SEED_SALT = 0x4453504546464543L;

    public static DragoonSpellEffectRandomizer getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    private DragoonSpellEffectRandomizer() { }

    public SpellEffectPlan resolve(final RegistryId characterId, final RegistryId spellId, final DragoonSpellProfile profile, final List<DragoonSpellProfile> pool) {
        if(this.config.dragoonSpellEffects == DragoonSpellEffects.STOCK) return profile.stockEffectPlan();
        if(this.config.dragoonSpellEffects == DragoonSpellEffects.RANDOMIZE_RAW) return SpellEffectPlan.legacy();
        if(!profile.declarativeEffectsSafe() || !profile.deffPresentationOnly()) return profile.stockEffectPlan();

        final Random random = new Random(this.config.seed ^ EFFECT_SEED_SALT ^ characterId.hashCode() ^ Long.rotateLeft(spellId.hashCode(), 23));
        if(this.config.dragoonSpellEffects == DragoonSpellEffects.SHUFFLE_PACKAGES) {
            final List<DragoonSpellProfile> safe = pool.stream().filter(DragoonSpellProfile::declarativeEffectsSafe).filter(DragoonSpellProfile::deffPresentationOnly).toList();
            final List<DragoonSpellProfile> shuffled = new ArrayList<>(safe);
            java.util.Collections.shuffle(shuffled, new Random(this.config.seed ^ EFFECT_SEED_SALT ^ characterId.hashCode()));
            final int targetIndex = Math.floorMod(spellId.hashCode(), shuffled.size());
            return this.declarative(shuffled.get(targetIndex).stockEffectPlan());
        }

        if(this.config.dragoonSpellEffects == DragoonSpellEffects.RANDOMIZE_ARCHETYPE) {
            return this.randomArchetype(profile, random);
        }

        return this.randomIndependent(profile, random);
    }

    private SpellEffectPlan randomArchetype(final DragoonSpellProfile profile, final Random random) {
        final List<DragoonSpellEffectKind> kinds = this.allowedKinds(profile);
        final DragoonSpellEffectKind kind = kinds.get(random.nextInt(kinds.size()));
        final List<SpellEffect> effects = new ArrayList<>();
        TargetSide side = TargetSide.ALLIES;
        TargetLifeState lifeState = TargetLifeState.LIVING;
        switch(kind) {
            case DAMAGE -> { effects.add(new DamageSpellEffect(this.power(random))); side = TargetSide.ENEMIES; lifeState = TargetLifeState.LIVING; }
            case HEAL_HP -> { effects.add(new HealHpSpellEffect(this.power(random), true)); side = TargetSide.ALLIES; lifeState = TargetLifeState.LIVING; }
            case RESTORE_MP -> { effects.add(new RestoreMpSpellEffect(this.power(random), true)); side = TargetSide.ALLIES; lifeState = TargetLifeState.LIVING; }
            case RESTORE_SP -> { effects.add(new RestoreSpSpellEffect(this.power(random), true)); side = TargetSide.ALLIES; lifeState = TargetLifeState.LIVING; }
            case REVIVE -> { effects.add(new ReviveSpellEffect(Math.max(1, this.power(random)))); side = TargetSide.ALLIES; lifeState = TargetLifeState.DEAD; }
            case CLEANSE -> { effects.add(new CleanseSpellEffect(0xff)); side = TargetSide.ALLIES; lifeState = TargetLifeState.LIVING; }
            case DRAIN_HP -> { effects.add(new DamageSpellEffect(this.power(random))); effects.add(new DrainHpSpellEffect(50)); side = TargetSide.ENEMIES; lifeState = TargetLifeState.LIVING; }
            case DRAIN_MP -> { effects.add(new DamageSpellEffect(this.power(random))); effects.add(new DrainMpSpellEffect(50)); side = TargetSide.ENEMIES; lifeState = TargetLifeState.LIVING; }
            case DRAIN_SP -> { effects.add(new DamageSpellEffect(this.power(random))); effects.add(new DrainSpSpellEffect(50)); side = TargetSide.ENEMIES; lifeState = TargetLifeState.LIVING; }
            case STATUS -> { effects.add(new ApplyStatusSpellEffect(1 << random.nextInt(8), this.statusChance(random))); side = TargetSide.ENEMIES; lifeState = TargetLifeState.LIVING; }
            case BUFF -> { effects.add(new StatModifierSpellEffect(SpellStat.values()[random.nextInt(4)], 50, 3)); side = TargetSide.ALLIES; lifeState = TargetLifeState.LIVING; }
            case DEBUFF -> { effects.add(new StatModifierSpellEffect(SpellStat.values()[random.nextInt(4)], -50, 3)); side = TargetSide.ENEMIES; lifeState = TargetLifeState.LIVING; }
            case REGEN_HP -> { effects.add(new RegenHpSpellEffect(this.power(random), 3, true)); side = TargetSide.ALLIES; lifeState = TargetLifeState.LIVING; }
            case REGEN_MP -> { effects.add(new RegenMpSpellEffect(this.power(random), 3, true)); side = TargetSide.ALLIES; lifeState = TargetLifeState.LIVING; }
            case REGEN_SP -> { effects.add(new RegenSpSpellEffect(this.power(random), 3, true)); side = TargetSide.ALLIES; lifeState = TargetLifeState.LIVING; }
        }
        return new SpellEffectPlan(new SpellTargetProfile(side, random.nextBoolean() ? TargetScope.SINGLE : TargetScope.ALL, lifeState), effects, ExecutionMode.DECLARATIVE);
    }

    private SpellEffectPlan randomIndependent(final DragoonSpellProfile profile, final Random random) {
        final List<SpellEffect> effects = new ArrayList<>();
        boolean offensive = false;
        boolean supportive = false;
        for(final DragoonSpellEffectKind kind : this.allowedKinds(profile)) {
            if(!random.nextBoolean()) continue;
            switch(kind) {
                case DAMAGE -> { effects.add(new DamageSpellEffect(this.power(random))); offensive = true; }
                case HEAL_HP -> { effects.add(new HealHpSpellEffect(this.power(random), true)); supportive = true; }
                case RESTORE_MP -> { effects.add(new RestoreMpSpellEffect(this.power(random), true)); supportive = true; }
                case RESTORE_SP -> { effects.add(new RestoreSpSpellEffect(this.power(random), true)); supportive = true; }
                case CLEANSE -> { effects.add(new CleanseSpellEffect(0xff)); supportive = true; }
                case DRAIN_HP -> { if(offensive) effects.add(new DrainHpSpellEffect(50)); }
                case DRAIN_MP -> { if(offensive) effects.add(new DrainMpSpellEffect(50)); }
                case DRAIN_SP -> { if(offensive) effects.add(new DrainSpSpellEffect(50)); }
                case STATUS -> { effects.add(new ApplyStatusSpellEffect(1 << random.nextInt(8), this.statusChance(random))); offensive = true; }
                case BUFF -> { effects.add(new StatModifierSpellEffect(SpellStat.values()[random.nextInt(4)], 50, 3)); supportive = true; }
                case DEBUFF -> { effects.add(new StatModifierSpellEffect(SpellStat.values()[random.nextInt(4)], -50, 3)); offensive = true; }
                case REGEN_HP -> { effects.add(new RegenHpSpellEffect(this.power(random), 3, true)); supportive = true; }
                case REGEN_MP -> { effects.add(new RegenMpSpellEffect(this.power(random), 3, true)); supportive = true; }
                case REGEN_SP -> { effects.add(new RegenSpSpellEffect(this.power(random), 3, true)); supportive = true; }
                case REVIVE -> { }
            }
        }
        if(effects.isEmpty() || offensive == supportive) return this.randomArchetype(profile, random);
        final TargetSide side = offensive ? TargetSide.ENEMIES : TargetSide.ALLIES;
        return new SpellEffectPlan(new SpellTargetProfile(side, random.nextBoolean() ? TargetScope.SINGLE : TargetScope.ALL, TargetLifeState.LIVING), effects, ExecutionMode.DECLARATIVE);
    }

    private List<DragoonSpellEffectKind> allowedKinds(final DragoonSpellProfile profile) {
        final List<DragoonSpellEffectKind> kinds = new ArrayList<>();
        this.add(kinds, profile, DragoonSpellEffectKind.DAMAGE, this.config.dragoonSpellAllowDamage);
        this.add(kinds, profile, DragoonSpellEffectKind.HEAL_HP, this.config.dragoonSpellAllowHealHp);
        this.add(kinds, profile, DragoonSpellEffectKind.RESTORE_MP, this.config.dragoonSpellAllowRestoreMp);
        this.add(kinds, profile, DragoonSpellEffectKind.RESTORE_SP, this.config.dragoonSpellAllowRestoreSp);
        this.add(kinds, profile, DragoonSpellEffectKind.REVIVE, this.config.dragoonSpellAllowRevive);
        this.add(kinds, profile, DragoonSpellEffectKind.CLEANSE, this.config.dragoonSpellAllowCleanse);
        this.add(kinds, profile, DragoonSpellEffectKind.DRAIN_HP, this.config.dragoonSpellAllowDrainHp && this.config.dragoonSpellAllowDamage);
        this.add(kinds, profile, DragoonSpellEffectKind.DRAIN_MP, this.config.dragoonSpellAllowDrainMp && this.config.dragoonSpellAllowDamage);
        this.add(kinds, profile, DragoonSpellEffectKind.DRAIN_SP, this.config.dragoonSpellAllowDrainSp && this.config.dragoonSpellAllowDamage);
        this.add(kinds, profile, DragoonSpellEffectKind.STATUS, this.config.dragoonSpellAllowStatus);
        this.add(kinds, profile, DragoonSpellEffectKind.BUFF, this.config.dragoonSpellAllowBuff);
        this.add(kinds, profile, DragoonSpellEffectKind.DEBUFF, this.config.dragoonSpellAllowDebuff);
        this.add(kinds, profile, DragoonSpellEffectKind.REGEN_HP, this.config.dragoonSpellAllowRegenHp);
        this.add(kinds, profile, DragoonSpellEffectKind.REGEN_MP, this.config.dragoonSpellAllowRegenMp);
        this.add(kinds, profile, DragoonSpellEffectKind.REGEN_SP, this.config.dragoonSpellAllowRegenSp);
        if(kinds.isEmpty()) throw new IllegalStateException("Dragoon spell effect configuration cannot produce a supported effect for " + profile);
        return kinds;
    }

    private void add(final List<DragoonSpellEffectKind> kinds, final DragoonSpellProfile profile, final DragoonSpellEffectKind kind, final boolean configured) {
        if(configured && profile.allowedEffects().contains(kind)) kinds.add(kind);
    }

    private SpellEffectPlan declarative(final SpellEffectPlan plan) {
        return new SpellEffectPlan(plan.target(), plan.effects(), ExecutionMode.DECLARATIVE);
    }

    private int power(final Random random) {
        return this.config.dragoonSpellPowerLowerPercentBound + random.nextInt(this.config.dragoonSpellPowerUpperPercentBound - this.config.dragoonSpellPowerLowerPercentBound + 1);
    }

    private int statusChance(final Random random) {
        return this.config.dragoonSpellStatusChanceLowerBound + random.nextInt(this.config.dragoonSpellStatusChanceUpperBound - this.config.dragoonSpellStatusChanceLowerBound + 1);
    }
}
