package lod.irongoon.services;

import legend.game.combat.Battle;
import legend.game.combat.effects.ScriptDeffEffect;
import legend.game.combat.spells.SpellEffectPlan;
import legend.game.combat.types.BattleObject;
import legend.game.inventory.SpellStats0c;
import legend.game.scripting.ScriptState;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.Objects;

public final class ResolvedDragoonSpell extends SpellStats0c {
    private final RegistryId spellId;
    private final SpellStats0c baseSpell;

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
        super(targetType, flags, specialEffect, damageMultiplier, multi, accuracy, mp, statusChance, element, statusType, buffType, unknown);
        this.spellId = Objects.requireNonNull(spellId, "spellId");
        this.baseSpell = Objects.requireNonNull(baseSpell, "baseSpell");
        this.setEffectPlan(effectPlan);
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
