package lod.irongoon.services;

import legend.core.GameEngine;
import legend.game.characters.CharacterData2c;
import legend.game.combat.spells.ApplyStatusSpellEffect;
import legend.game.combat.spells.DamageSpellEffect;
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
import legend.game.combat.spells.TargetLifeState;
import legend.game.combat.spells.TargetScope;
import legend.game.combat.spells.TargetSide;
import legend.game.inventory.SpellStats0c;
import legend.game.types.GameState52c;
import legend.lodmod.characters.DartCharacterData;
import lod.irongoon.api.DragoonSpellProfile;
import lod.irongoon.api.GatherDragoonSpellProfilesEvent;
import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.DragoonSpellEffects;
import lod.irongoon.data.DragoonSpellElements;
import lod.irongoon.data.DragoonSpellRandomizationPool;
import lod.irongoon.data.DragoonSpellStats;
import lod.irongoon.services.randomizer.DragoonSpellEffectRandomizer;
import lod.irongoon.services.randomizer.DragoonSpellElementRandomizer;
import lod.irongoon.services.randomizer.DragoonSpellStatsRandomizer;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.IntUnaryOperator;

public final class DragoonSpells {
    private static final DragoonSpells INSTANCE = new DragoonSpells();
    private static final long RAW_SEED_SALT = 0x4453505241574644L;
    private static final Set<String> STOCK_SPELLS = Set.of(
        "flameshot", "explosion", "final_burst", "red_eyed_dragon", "divine_dg_cannon", "divine_dg_ball",
        "wing_blaster", "gaspless", "blossom_storm", "jade_dragon", "albert_wing_blaster", "albert_gaspless", "rose_storm",
        "star_children", "moon_light", "gates_of_heaven", "white_silver_dragon", "miranda_star_children", "miranda_moon_light", "miranda_gates_of_heaven",
        "astral_drain", "death_dimension", "demons_gate", "dark_dragon",
        "atomic_mind", "thunder_kid", "thunder_god", "violet_dragon",
        "freezing_ring", "rainbow_breath", "diamond_dust", "blue_sea_dragon",
        "grand_stream", "meteor_strike", "golden_dragon"
    );

    public static DragoonSpells getInstance() {
        return INSTANCE;
    }

    private final IrongoonConfig config = IrongoonConfig.getInstance();
    private final DragoonSpellStatsRandomizer statsRandomizer = DragoonSpellStatsRandomizer.getInstance();
    private final DragoonSpellElementRandomizer elementRandomizer = DragoonSpellElementRandomizer.getInstance();
    private final DragoonSpellEffectRandomizer effectRandomizer = DragoonSpellEffectRandomizer.getInstance();
    private final Map<RegistryId, DragoonSpellProfile> profiles = new LinkedHashMap<>();
    private final Map<CacheKey, SpellStats0c> resolvedSpells = new HashMap<>();
    private final Map<RegistryId, RegistryId> originalFirstSpells = new HashMap<>();

    private DragoonSpells() { }

    public void initialize(final GameState52c gameState, final IntUnaryOperator dragoonLevelOneMp) {
        this.validateSafeEffectPool();
        this.profiles.clear();
        this.resolvedSpells.clear();
        this.originalFirstSpells.clear();
        final GatherDragoonSpellProfilesEvent gather = GameEngine.EVENTS.postEvent(new GatherDragoonSpellProfilesEvent());
        this.profiles.putAll(gather.profiles());
        this.registerStockProfiles();

        final List<SpellStats0c> globalSpellPool = this.profiles.keySet().stream()
            .sorted(Comparator.comparing(RegistryId::toString))
            .map(this::baseSpell)
            .toList();
        final List<DragoonSpellProfile> globalProfilePool = this.profiles.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(RegistryId::toString)))
            .map(Map.Entry::getValue)
            .toList();

        for(var characterIndex = 0; characterIndex < gameState.charData_32c.size(); characterIndex++) {
            final CharacterData2c character = gameState.charData_32c.get(characterIndex);
            final RegistryId characterId = character.template.getRegistryId();
            final List<RegistryId> eligibleIds = character.getAllSpells().stream().filter(this.profiles::containsKey).toList();
            if(!eligibleIds.isEmpty()) this.originalFirstSpells.put(characterId, eligibleIds.getFirst());
            final List<SpellStats0c> spellPool = this.config.dragoonSpellRandomizationPool == DragoonSpellRandomizationPool.GLOBAL
                ? globalSpellPool
                : eligibleIds.stream().sorted(Comparator.comparing(RegistryId::toString)).map(this::baseSpell).toList();
            final List<DragoonSpellProfile> profilePool = this.config.dragoonSpellRandomizationPool == DragoonSpellRandomizationPool.GLOBAL
                ? globalProfilePool
                : eligibleIds.stream().sorted(Comparator.comparing(RegistryId::toString)).map(this.profiles::get).toList();
            final RegistryId firstEligible = eligibleIds.isEmpty() ? null : eligibleIds.getFirst();

            for(final RegistryId spellId : eligibleIds) {
                final boolean firstSlot = spellId.equals(firstEligible);
                final SpellStats0c resolved = this.resolve(characterId, spellId, spellPool, profilePool, firstSlot, dragoonLevelOneMp.applyAsInt(characterIndex));
                this.resolvedSpells.put(new CacheKey(characterId, spellId), resolved);
            }

            if(character instanceof final DartCharacterData dart) {
                final List<RegistryId> alternateIds = new ArrayList<>();
                alternateIds.addAll(dart.getRedEyeSpells());
                alternateIds.addAll(dart.getDivineSpells());
                for(final RegistryId spellId : alternateIds.stream().distinct().filter(this.profiles::containsKey).toList()) {
                    final SpellStats0c resolved = this.resolve(characterId, spellId, globalSpellPool, globalProfilePool, false, dragoonLevelOneMp.applyAsInt(characterIndex));
                    this.resolvedSpells.putIfAbsent(new CacheKey(characterId, spellId), resolved);
                }
            }
        }
    }

    public boolean isProfiled(final RegistryId spellId) {
        return this.profiles.containsKey(spellId);
    }

    public boolean isUsableAsFirstSpell(final RegistryId spellId) {
        final DragoonSpellProfile profile = this.profiles.get(spellId);
        return profile != null && profile.usableAsFirstLivingTargetSpell();
    }

    public RegistryId originalFirstSpell(final CharacterData2c character) {
        return this.originalFirstSpells.get(character.template.getRegistryId());
    }

    public SpellStats0c resolve(final CharacterData2c character, final RegistryId spellId, final SpellStats0c baseSpell) {
        return this.resolvedSpells.getOrDefault(new CacheKey(character.template.getRegistryId(), spellId), baseSpell);
    }

    public String describe(final CharacterData2c character, final RegistryId spellId, final SpellStats0c spell, final String baseDescription) {
        if(!this.resolvedSpells.containsKey(new CacheKey(character.template.getRegistryId(), spellId))) return baseDescription;
        final SpellEffectPlan plan = spell.getEffectPlan();
        final String target = plan.target().scope() == TargetScope.ALL ? "All " : "One ";
        final String side = plan.target().side() == TargetSide.ENEMIES ? "enemy" : plan.target().side() == TargetSide.SELF ? "self" : "ally";
        final String effects = plan.effects().isEmpty() ? "legacy effects" : plan.effects().stream().map(this::effectName).distinct().reduce((left, right) -> left + ", " + right).orElse("effects");
        return "%s%s; %s; %d MP".formatted(target, side, effects, spell.mp_06);
    }

    private void registerStockProfiles() {
        for(final RegistryId spellId : GameEngine.REGISTRIES.spells) {
            if(!spellId.toString().startsWith("lod:") || !STOCK_SPELLS.contains(spellId.entryId().toString())) continue;
            final SpellStats0c spell = this.baseSpell(spellId);
            this.profiles.putIfAbsent(spellId, new DragoonSpellProfile(true, true, spell.getEffectPlan(), true, true));
        }
    }

    private SpellStats0c resolve(
        final RegistryId characterId,
        final RegistryId spellId,
        final List<SpellStats0c> spellPool,
        final List<DragoonSpellProfile> profilePool,
        final boolean firstSlot,
        final int dragoonLevelOneMp
    ) {
        final SpellStats0c baseSpell = this.baseSpell(spellId);
        if(this.metadataStock()) return baseSpell;

        final DragoonSpellProfile profile = this.profiles.get(spellId);
        final DragoonSpellStatsRandomizer.ScalarStats scalar = profile.metadataReplacementSafe()
            ? this.statsRandomizer.resolve(characterId, spellId, baseSpell, spellPool)
            : DragoonSpellStatsRandomizer.ScalarStats.from(baseSpell);
        final int mp = firstSlot && this.config.dragoonSpellEffects != DragoonSpellEffects.RANDOMIZE_RAW
            ? Math.min(scalar.mp(), Math.max(0, dragoonLevelOneMp))
            : scalar.mp();
        final var element = profile.metadataReplacementSafe()
            ? this.elementRandomizer.resolve(characterId, spellId, baseSpell, spellPool)
            : baseSpell.element_08;
        SpellEffectPlan plan = this.effectRandomizer.resolve(characterId, spellId, profile, profilePool);
        plan = this.withScalarMetadata(plan, scalar.power(), scalar.statusChance());

        if(this.config.dragoonSpellEffects == DragoonSpellEffects.RANDOMIZE_RAW && profile.rawLegacyRandomizationSafe()) {
            return this.raw(characterId, spellId, baseSpell, element, mp, scalar.accuracy(), scalar.statusChance());
        }

        final int targetType = this.targetType(baseSpell.targetType_00, plan);
        return new ResolvedDragoonSpell(spellId, baseSpell, targetType, baseSpell.flags_01, baseSpell.specialEffect_02, baseSpell.damageMultiplier_03, scalar.power(), scalar.accuracy(), mp, scalar.statusChance(), element, baseSpell.statusType_09, baseSpell.buffType_0a, baseSpell._0b, plan);
    }

    private ResolvedDragoonSpell raw(final RegistryId characterId, final RegistryId spellId, final SpellStats0c baseSpell, final org.legendofdragoon.modloader.registries.RegistryDelegate<legend.game.characters.Element> element, final int mp, final int accuracy, final int statusChance) {
        final Random random = new Random(this.config.seed ^ RAW_SEED_SALT ^ characterId.hashCode() ^ Long.rotateLeft(spellId.hashCode(), 29));
        final SpellEffectPlan rawPlan = new SpellEffectPlan(
            new legend.game.combat.spells.SpellTargetProfile(TargetSide.ANY, TargetScope.SINGLE, TargetLifeState.ANY),
            List.of(),
            ExecutionMode.LEGACY_RAW
        );
        return new ResolvedDragoonSpell(spellId, baseSpell, random.nextInt(256), random.nextInt(256), random.nextInt(256), 1 << random.nextInt(8), random.nextInt(256), accuracy, mp, statusChance, element, random.nextInt(256), random.nextInt(256), random.nextInt(256), rawPlan);
    }

    private SpellEffectPlan withScalarMetadata(final SpellEffectPlan plan, final int power, final int statusChance) {
        if(plan.executionMode() != ExecutionMode.DECLARATIVE) return plan;
        final List<SpellEffect> effects = plan.effects().stream().map(effect -> switch(effect) {
            case DamageSpellEffect ignored -> new DamageSpellEffect(power);
            case HealHpSpellEffect heal -> new HealHpSpellEffect(power, heal.percentage());
            case RestoreMpSpellEffect restore -> new RestoreMpSpellEffect(power, restore.percentage());
            case RestoreSpSpellEffect restore -> new RestoreSpSpellEffect(power, restore.percentage());
            case ReviveSpellEffect ignored -> new ReviveSpellEffect(power);
            case RegenHpSpellEffect regen -> new RegenHpSpellEffect(power, regen.turns(), regen.percentage());
            case RegenMpSpellEffect regen -> new RegenMpSpellEffect(power, regen.turns(), regen.percentage());
            case RegenSpSpellEffect regen -> new RegenSpSpellEffect(power, regen.turns(), regen.percentage());
            case ApplyStatusSpellEffect status -> new ApplyStatusSpellEffect(status.statusMask(), statusChance);
            default -> effect;
        }).toList();
        return new SpellEffectPlan(plan.target(), effects, plan.executionMode());
    }

    private int targetType(final int baseTargetType, final SpellEffectPlan plan) {
        if(plan.executionMode() != ExecutionMode.DECLARATIVE) return baseTargetType;
        var targetType = baseTargetType & 0x80;
        if(plan.target().scope() == TargetScope.ALL) targetType |= 0x08;
        if(plan.target().side() == TargetSide.ENEMIES) targetType |= 0x40;
        return targetType;
    }

    private boolean metadataStock() {
        return this.config.dragoonSpellStats == DragoonSpellStats.STOCK
            && this.config.dragoonSpellElements == DragoonSpellElements.STOCK
            && this.config.dragoonSpellEffects == DragoonSpellEffects.STOCK;
    }

    private void validateSafeEffectPool() {
        if(this.config.dragoonSpellEffects == DragoonSpellEffects.STOCK || this.config.dragoonSpellEffects == DragoonSpellEffects.RANDOMIZE_RAW) return;
        final boolean livingTarget = this.config.dragoonSpellAllowDamage
            || this.config.dragoonSpellAllowHealHp
            || this.config.dragoonSpellAllowRestoreMp
            || this.config.dragoonSpellAllowRestoreSp
            || this.config.dragoonSpellAllowCleanse
            || this.config.dragoonSpellAllowDrainHp
            || this.config.dragoonSpellAllowDrainMp
            || this.config.dragoonSpellAllowDrainSp
            || this.config.dragoonSpellAllowStatus
            || this.config.dragoonSpellAllowBuff
            || this.config.dragoonSpellAllowDebuff
            || this.config.dragoonSpellAllowRegenHp
            || this.config.dragoonSpellAllowRegenMp
            || this.config.dragoonSpellAllowRegenSp;
        if(!livingTarget) {
            throw new IllegalStateException("Dragoon spell effect configuration cannot produce a living-target first spell; enable at least one non-revive effect");
        }
    }

    private SpellStats0c baseSpell(final RegistryId spellId) {
        return GameEngine.REGISTRIES.spells.getEntry(spellId).get();
    }

    private String effectName(final SpellEffect effect) {
        return effect.getClass().getSimpleName().replace("SpellEffect", "");
    }

    private record CacheKey(RegistryId characterId, RegistryId spellId) { }
}
