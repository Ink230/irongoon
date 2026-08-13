package lod.irongoon.api;

import legend.game.combat.spells.SpellEffectPlan;
import legend.game.combat.spells.TargetLifeState;
import legend.game.combat.spells.TargetScope;
import legend.game.combat.spells.TargetSide;

import java.util.Set;

public record DragoonSpellProfile(
    boolean metadataReplacementSafe,
    boolean declarativeEffectsSafe,
    SpellEffectPlan stockEffectPlan,
    boolean deffPresentationOnly,
    boolean usableAsFirstLivingTargetSpell,
    Set<TargetSide> allowedTargetSides,
    Set<TargetScope> allowedTargetScopes,
    Set<TargetLifeState> allowedTargetLifeStates,
    Set<DragoonSpellEffectKind> allowedEffects,
    boolean rawLegacyRandomizationSafe
) {
    public DragoonSpellProfile {
    if(stockEffectPlan == null) throw new IllegalArgumentException("Dragoon spell stock effect plan cannot be null");
    if(allowedTargetSides == null) throw new IllegalArgumentException("Dragoon spell allowed target sides cannot be null");
    if(allowedTargetScopes == null) throw new IllegalArgumentException("Dragoon spell allowed target scopes cannot be null");
    if(allowedTargetLifeStates == null) throw new IllegalArgumentException("Dragoon spell allowed target life states cannot be null");
    if(allowedEffects == null) throw new IllegalArgumentException("Dragoon spell allowed effects cannot be null");
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
        final boolean rawLegacyRandomizationSafe
    ) {
        this(
            metadataReplacementSafe,
            declarativeEffectsSafe,
            stockEffectPlan,
            deffPresentationOnly,
            stockEffectPlan.target().lifeState() != TargetLifeState.DEAD,
            Set.of(TargetSide.SELF, TargetSide.ALLIES, TargetSide.ENEMIES),
            Set.of(TargetScope.SINGLE, TargetScope.ALL),
            Set.of(TargetLifeState.LIVING, TargetLifeState.DEAD, TargetLifeState.ANY),
            Set.of(DragoonSpellEffectKind.values()),
            rawLegacyRandomizationSafe
        );
    }
}
