package lod.irongoon.models;

import legend.game.combat.spells.SpellEffectPlan;
import legend.game.combat.spells.TargetLifeState;
import legend.game.combat.spells.TargetScope;
import legend.game.combat.spells.TargetSide;
import lod.irongoon.data.DragoonSpellEffectKind;

import java.util.List;
import java.util.Set;

public record DragoonSpellProfile(
    boolean metadataReplacementSafe,
    boolean declarativeEffectsSafe,
    List<SpellEffectPlan> stockEffectPlans,
    boolean deffPresentationOnly,
    boolean usableAsFirstLivingTargetSpell,
    Set<TargetSide> allowedTargetSides,
    Set<TargetScope> allowedTargetScopes,
    Set<TargetLifeState> allowedTargetLifeStates,
    Set<DragoonSpellEffectKind> allowedEffects,
    boolean rawLegacyRandomizationSafe
) {
    public DragoonSpellProfile {
        if(stockEffectPlans == null || stockEffectPlans.isEmpty()) throw new IllegalArgumentException("Dragoon spell stock effect plans cannot be null or empty");
        for(final SpellEffectPlan stockEffectPlan : stockEffectPlans) {
            if(stockEffectPlan == null) throw new IllegalArgumentException("Dragoon spell stock effect plan cannot be null");
        }
        if(allowedTargetSides == null) throw new IllegalArgumentException("Dragoon spell allowed target sides cannot be null");
        if(allowedTargetScopes == null) throw new IllegalArgumentException("Dragoon spell allowed target scopes cannot be null");
        if(allowedTargetLifeStates == null) throw new IllegalArgumentException("Dragoon spell allowed target life states cannot be null");
        if(allowedEffects == null) throw new IllegalArgumentException("Dragoon spell allowed effects cannot be null");
        stockEffectPlans = List.copyOf(stockEffectPlans);
        allowedTargetSides = Set.copyOf(allowedTargetSides);
        allowedTargetScopes = Set.copyOf(allowedTargetScopes);
        allowedTargetLifeStates = Set.copyOf(allowedTargetLifeStates);
        allowedEffects = Set.copyOf(allowedEffects);
    }

    public DragoonSpellProfile(
        final boolean metadataReplacementSafe,
        final boolean declarativeEffectsSafe,
        final SpellEffectPlan stockEffectPlan,
        final boolean deffPresentationOnly,
        final boolean usableAsFirstLivingTargetSpell,
        final Set<TargetSide> allowedTargetSides,
        final Set<TargetScope> allowedTargetScopes,
        final Set<TargetLifeState> allowedTargetLifeStates,
        final Set<DragoonSpellEffectKind> allowedEffects,
        final boolean rawLegacyRandomizationSafe
    ) {
        this(
            metadataReplacementSafe,
            declarativeEffectsSafe,
            List.of(stockEffectPlan),
            deffPresentationOnly,
            usableAsFirstLivingTargetSpell,
            allowedTargetSides,
            allowedTargetScopes,
            allowedTargetLifeStates,
            allowedEffects,
            rawLegacyRandomizationSafe
        );
    }

    public DragoonSpellProfile(
        final boolean metadataReplacementSafe,
        final boolean declarativeEffectsSafe,
        final SpellEffectPlan stockEffectPlan,
        final boolean deffPresentationOnly,
        final boolean rawLegacyRandomizationSafe
    ) {
        this(metadataReplacementSafe, declarativeEffectsSafe, List.of(stockEffectPlan), deffPresentationOnly, rawLegacyRandomizationSafe);
    }

    public DragoonSpellProfile(
        final boolean metadataReplacementSafe,
        final boolean declarativeEffectsSafe,
        final List<SpellEffectPlan> stockEffectPlans,
        final boolean deffPresentationOnly,
        final boolean rawLegacyRandomizationSafe
    ) {
        this(
            metadataReplacementSafe,
            declarativeEffectsSafe,
            stockEffectPlans,
            deffPresentationOnly,
            stockEffectPlans.stream().anyMatch(plan -> plan.target().lifeState() != TargetLifeState.DEAD),
            Set.of(TargetSide.SELF, TargetSide.ALLIES, TargetSide.ENEMIES),
            Set.of(TargetScope.SINGLE, TargetScope.ALL),
            Set.of(TargetLifeState.LIVING, TargetLifeState.DEAD, TargetLifeState.ANY),
            Set.of(DragoonSpellEffectKind.values()),
            rawLegacyRandomizationSafe
        );
    }

    public SpellEffectPlan stockEffectPlan() {
        return this.stockEffectPlans.get(0);
    }
}
