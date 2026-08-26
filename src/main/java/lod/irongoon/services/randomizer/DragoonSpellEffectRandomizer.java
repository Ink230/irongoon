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
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.DragoonSpellEffectKind;
import lod.irongoon.data.DragoonSpellEffects;
import lod.irongoon.models.DragoonSpellProfile;
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

    public List<SpellEffectPlan> resolve(final RegistryId characterId, final RegistryId spellId, final DragoonSpellProfile profile, final List<DragoonSpellProfile> pool, final boolean firstSlot) {
        if(this.config.dragoonSpellEffects == DragoonSpellEffects.STOCK) return profile.stockEffectPlans();
        if(this.config.dragoonSpellEffects == DragoonSpellEffects.RANDOMIZE_RAW) return List.of(SpellEffectPlan.legacy());
        if(!profile.declarativeEffectsSafe() || !profile.deffPresentationOnly()) return profile.stockEffectPlans();

        final Random random = new Random(this.config.seed ^ EFFECT_SEED_SALT ^ characterId.hashCode() ^ Long.rotateLeft(spellId.hashCode(), 23));
        if(this.config.dragoonSpellEffects == DragoonSpellEffects.SHUFFLE_PACKAGES) {
            final List<DragoonSpellProfile> safe = pool.stream().filter(DragoonSpellProfile::declarativeEffectsSafe).filter(DragoonSpellProfile::deffPresentationOnly).toList();
            final List<DragoonSpellProfile> shuffled = new ArrayList<>(safe);
            java.util.Collections.shuffle(shuffled, new Random(this.config.seed ^ EFFECT_SEED_SALT ^ characterId.hashCode()));
            var targetIndex = 0;
            for(var index = 0; index < safe.size(); index++) {
                if(safe.get(index) == profile) {
                    targetIndex = index;
                    break;
                }
            }
            if(firstSlot && shuffled.get(targetIndex).stockEffectPlan().target().lifeState() == TargetLifeState.DEAD) {
                for(var offset = 1; offset < shuffled.size(); offset++) {
                    final int candidate = (targetIndex + offset) % shuffled.size();
                    if(shuffled.get(candidate).stockEffectPlan().target().lifeState() != TargetLifeState.DEAD) {
                        targetIndex = candidate;
                        break;
                    }
                }
            }
            return this.declarative(shuffled.get(targetIndex).stockEffectPlans());
        }

        if(this.config.dragoonSpellEffects == DragoonSpellEffects.RANDOMIZE_ARCHETYPE) {
            return List.of(this.randomArchetype(profile, random, firstSlot));
        }

        return List.of(this.randomIndependent(profile, random, firstSlot));
    }

    public List<SpellEffectPlan> ensureUsable(final List<SpellEffectPlan> plans) {
        if(plans.isEmpty() || plans.stream().anyMatch(this::isUsable)) return plans;

        final SpellEffectPlan primary = plans.getFirst();
        if(primary.executionMode() != ExecutionMode.DECLARATIVE) return plans;

        final int damagePower = Math.max(1, this.config.dragoonSpellPowerLowerPercentBound);
        if(primary.target().scope() == TargetScope.ALL) {
            return List.of(
                this.fallbackDamage(TargetScope.ALL, damagePower),
                this.fallbackHealing(TargetScope.ALL)
            );
        }

        final boolean offensive = primary.target().side() == TargetSide.ENEMIES || primary.effects().stream().anyMatch(this::isOffensive);
        return List.of(offensive ? this.fallbackDamage(TargetScope.SINGLE, damagePower) : this.fallbackHealing(TargetScope.SINGLE));
    }

    private SpellEffectPlan randomArchetype(final DragoonSpellProfile profile, final Random random, final boolean firstSlot) {
        final List<DragoonSpellEffectKind> kinds = this.allowedKinds(profile);
        if(firstSlot) kinds.remove(DragoonSpellEffectKind.REVIVE);
        if(kinds.isEmpty()) throw new IllegalStateException("Dragoon spell effect configuration cannot produce a living-target first spell for " + profile);
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

    private SpellEffectPlan randomIndependent(final DragoonSpellProfile profile, final Random random, final boolean firstSlot) {
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
        if(effects.isEmpty() || offensive == supportive) return this.randomArchetype(profile, random, firstSlot);
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

    private boolean isUsable(final SpellEffectPlan plan) {
        if(plan.executionMode() != ExecutionMode.DECLARATIVE) return true;
        return plan.effects().stream().anyMatch(effect -> switch(effect) {
            case DamageSpellEffect damage -> damage.power() > 0;
            case HealHpSpellEffect heal -> heal.potency() > 0;
            case RestoreMpSpellEffect restore -> restore.potency() > 0;
            case RestoreSpSpellEffect restore -> restore.potency() > 0;
            case ReviveSpellEffect ignored -> true;
            case CleanseSpellEffect cleanse -> cleanse.statusMask() != 0;
            case DrainHpSpellEffect ignored -> false;
            case DrainMpSpellEffect ignored -> false;
            case DrainSpSpellEffect ignored -> false;
            case ApplyStatusSpellEffect status -> status.statusMask() != 0 && status.chance() > 0;
            case StatModifierSpellEffect modifier -> modifier.amount() != 0 && modifier.turns() > 0;
            case RegenHpSpellEffect regen -> regen.potency() > 0 && regen.turns() > 0;
            case RegenMpSpellEffect regen -> regen.potency() > 0 && regen.turns() > 0;
            case RegenSpSpellEffect regen -> regen.potency() > 0 && regen.turns() > 0;
        });
    }

    private boolean isOffensive(final SpellEffect effect) {
        return switch(effect) {
            case DamageSpellEffect ignored -> true;
            case DrainHpSpellEffect ignored -> true;
            case DrainMpSpellEffect ignored -> true;
            case DrainSpSpellEffect ignored -> true;
            case ApplyStatusSpellEffect ignored -> true;
            case StatModifierSpellEffect modifier -> modifier.amount() < 0;
            default -> false;
        };
    }

    private SpellEffectPlan fallbackDamage(final TargetScope scope, final int power) {
        return new SpellEffectPlan(
            new SpellTargetProfile(TargetSide.ENEMIES, scope, TargetLifeState.LIVING),
            List.of(new DamageSpellEffect(power)),
            ExecutionMode.DECLARATIVE
        );
    }

    private SpellEffectPlan fallbackHealing(final TargetScope scope) {
        return new SpellEffectPlan(
            new SpellTargetProfile(TargetSide.ALLIES, scope, TargetLifeState.LIVING),
            List.of(new HealHpSpellEffect(1, false)),
            ExecutionMode.DECLARATIVE
        );
    }

    private void add(final List<DragoonSpellEffectKind> kinds, final DragoonSpellProfile profile, final DragoonSpellEffectKind kind, final boolean configured) {
        if(configured && profile.allowedEffects().contains(kind)) kinds.add(kind);
    }

    private SpellEffectPlan declarative(final SpellEffectPlan plan) {
        return new SpellEffectPlan(plan.target(), plan.effects(), ExecutionMode.DECLARATIVE);
    }

    private List<SpellEffectPlan> declarative(final List<SpellEffectPlan> plans) {
        return plans.stream().map(this::declarative).toList();
    }

    private int power(final Random random) {
        return this.config.dragoonSpellPowerLowerPercentBound + random.nextInt(this.config.dragoonSpellPowerUpperPercentBound - this.config.dragoonSpellPowerLowerPercentBound + 1);
    }

    private int statusChance(final Random random) {
        return this.config.dragoonSpellStatusChanceLowerBound + random.nextInt(this.config.dragoonSpellStatusChanceUpperBound - this.config.dragoonSpellStatusChanceLowerBound + 1);
    }
}
