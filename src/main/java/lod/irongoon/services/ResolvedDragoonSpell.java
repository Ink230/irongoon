package lod.irongoon.services;

import legend.game.combat.Battle;
import legend.game.combat.effects.ScriptDeffEffect;
import legend.game.combat.spells.SpellEffectPlan;
import legend.game.combat.types.BattleObject;
import legend.game.inventory.SpellStats0c;
import legend.game.scripting.ScriptState;
import lod.irongoon.services.compatibility.SpellEffectPlans;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.List;


public final class ResolvedDragoonSpell extends SpellStats0c {
    private final RegistryId spellId;
    private final SpellStats0c baseSpell;
    private final List<SpellEffectPlan> effectPlans;

    public ResolvedDragoonSpell(
        final RegistryId spellId,
        final SpellStats0c baseSpell,
        final int targetType,
        final int flags,
        final int specialEffect,
        final int damageMultiplier,
        final int multi,
        final int accuracy,
        final int mp,
        final int statusChance,
        final org.legendofdragoon.modloader.registries.RegistryDelegate<legend.game.characters.Element> element,
        final int statusType,
        final int buffType,
        final int unknown,
        final SpellEffectPlan effectPlan
    ) {
        this(spellId, baseSpell, targetType, flags, specialEffect, damageMultiplier, multi, accuracy, mp, statusChance, element, statusType, buffType, unknown, List.of(effectPlan));
    }

    public ResolvedDragoonSpell(
        final RegistryId spellId,
        final SpellStats0c baseSpell,
        final int targetType,
        final int flags,
        final int specialEffect,
        final int damageMultiplier,
        final int multi,
        final int accuracy,
        final int mp,
        final int statusChance,
        final org.legendofdragoon.modloader.registries.RegistryDelegate<legend.game.characters.Element> element,
        final int statusType,
        final int buffType,
        final int unknown,
        final List<SpellEffectPlan> effectPlans
    ) {
        super(targetType, flags, specialEffect, damageMultiplier, multi, accuracy, mp, statusChance, element, statusType, buffType, unknown);
        if(spellId == null) throw new IllegalArgumentException("Resolved Dragoon spell ID cannot be null");
        if(baseSpell == null) throw new IllegalArgumentException("Resolved Dragoon base spell cannot be null");
        this.spellId = spellId;
        this.baseSpell = baseSpell;
        this.effectPlans = List.copyOf(effectPlans);
        SpellEffectPlans.set(this, this.effectPlans);
    }

    public ResolvedDragoonSpell withMp(final int mp) {
        if(this.mp_06 == mp) return this;

        return new ResolvedDragoonSpell(
            this.spellId,
            this.baseSpell,
            this.targetType_00,
            this.flags_01,
            this.specialEffect_02,
            this.damageMultiplier_03,
            this.multi_04,
            this.accuracy_05,
            mp,
            this.statusChance_07,
            this.element_08,
            this.statusType_09,
            this.buffType_0a,
            this._0b,
            this.effectPlans
        );
    }

    @Override
    public RegistryId getRegistryId() {
        return this.spellId;
    }

    @Override
    public String getTranslationKey() {
        return this.baseSpell.getTranslationKey();
    }

    @Override
    public String getTranslationKey(final String suffix) {
        return this.baseSpell.getTranslationKey(suffix);
    }

    @Override
    public void loadDeff(final Battle battle, final ScriptState<? extends BattleObject> parent, final ScriptDeffEffect effect, final int flags, final int bentIndex, final int deffParam, final int entrypoint) {
        this.baseSpell.loadDeff(battle, parent, effect, flags, bentIndex, deffParam, entrypoint);
    }

    @Override
    public int getBattleStage() {
        return this.baseSpell.getBattleStage();
    }
}
