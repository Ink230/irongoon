package lod.irongoon.config;

import lod.irongoon.data.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

/** YAML boundary: parse, normalize and validate everything before runtime state is touched. */
public final class IrongoonConfigCodec {
    private static final int MAX_RANDOM_PERCENT_BOUND = Integer.MAX_VALUE - 1;

    private IrongoonConfigCodec() {}

    public static IrongoonConfigSnapshot readLegacy(final File file) {
        if(!file.isFile()) return fromValues(file.getPath(), Map.of());
        try (InputStream input = new FileInputStream(file)) {
            final Object document = new Yaml().load(input);
            if(document == null) return fromValues(file.getPath(), Map.of());
            if(!(document instanceof Map<?, ?> raw)) throw new IllegalStateException("Irongoon configuration must be a YAML mapping: " + file);
            return fromValues(file.getPath(), raw);
        } catch (final IOException exception) {
            throw new IllegalStateException("Unable to read Irongoon configuration " + file, exception);
        }
    }

    /** Parses one profile without touching the process-wide runtime configuration. */
    public static IrongoonConfigSnapshot read(final Path path) {
        try (InputStream input = Files.newInputStream(path)) {
            final Object document = new Yaml().load(input);
            if(document == null) return fromValues(path.toString(), Map.of());
            if(!(document instanceof Map<?, ?> raw)) throw new IllegalStateException("Irongoon configuration must be a YAML mapping: " + path);
            return fromValues(path.toString(), raw);
        } catch(final IOException exception) {
            throw new IllegalStateException("Unable to read Irongoon configuration " + path, exception);
        }
    }

    /** Parses a UTF-8 campaign snapshot payload after its envelope has been decoded. */
    public static IrongoonConfigSnapshot read(final String source, final String yaml) {
        final Object document = new Yaml().load(yaml);
        if(document == null) return fromValues(source, Map.of());
        if(!(document instanceof Map<?, ?> raw)) {
            throw new IllegalStateException("Irongoon configuration must be a YAML mapping: " + source);
        }
        return fromValues(source, raw);
    }

    public static IrongoonConfigSnapshot fromValues(final String source, final Map<?, ?> raw) {
        final Map<String, Object> values = new LinkedHashMap<>();
        final Set<String> explicitKeys = new HashSet<>();
        for(final String key : IrongoonConfigSchema.KEYS) {
            values.put(key, normalize(key, IrongoonConfigSchema.blueprintValues().get(key), source));
        }
        final List<String> warnings = new ArrayList<>();
        for(final var entry : raw.entrySet()) {
            if(!(entry.getKey() instanceof String key)) throw new IllegalStateException("Irongoon configuration keys must be strings in " + source);
            if(!IrongoonConfigSchema.isKnown(key)) {
                warnings.add("Unknown Irongoon configuration key ignored: " + key);
                continue;
            }
            final String canonical = IrongoonConfigSchema.canonicalKey(key);
            if(explicitKeys.contains(canonical) && !canonical.equals(key)) continue;
            values.put(canonical, normalize(canonical, legacyValue(key, entry.getValue(), source), source));
            explicitKeys.add(canonical);
        }
        validate(values, source);
        return new IrongoonConfigSnapshot(source, values, warnings);
    }

    private static Object legacyValue(final String key, final Object value, final String source) {
        if(!"dragoonSpellRandomizeMpCost".equals(key)) return value;
        if(!(value instanceof Boolean randomize)) throw invalid(source, key, "a boolean");
        return randomize ? "RANDOM_CAMPAIGN_CHARACTER" : "STOCK";
    }

    /** Serializes canonical keys grouped by their stable schema sections. */
    public static String serializeCanonical(final IrongoonConfigSnapshot snapshot) {
        final IrongoonConfigSnapshot validated = fromValues(snapshot.source(), snapshot.values());
        final Map<IrongoonConfigSchema.Section, Map<String, Object>> sections = new LinkedHashMap<>();
        for(final String key : IrongoonConfigSchema.KEYS) {
            final IrongoonConfigSchema.Section section = IrongoonConfigSchema.setting(key).section();
            sections.computeIfAbsent(section, ignored -> new LinkedHashMap<>()).put(key, validated.values().get(key));
        }

        final StringBuilder yaml = new StringBuilder();
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        final Yaml dumper = new Yaml(options);
        for(final var entry : sections.entrySet()) {
            if(!yaml.isEmpty()) yaml.append('\n');
            yaml.append("# ").append(displaySection(entry.getKey())).append('\n');
            for(final var setting : entry.getValue().entrySet()) {
                yaml.append("# ").append(IrongoonConfigSchema.setting(setting.getKey()).help()).append('\n');
                yaml.append(dumper.dump(Map.of(setting.getKey(), setting.getValue())));
            }
        }
        return yaml.toString();
    }

    public static String serialize(final IrongoonConfigSnapshot snapshot) {
        final Map<String, Object> normalized = new LinkedHashMap<>();
        for(final String key : IrongoonConfigSchema.KEYS) {
            if(snapshot.values().containsKey(key)) normalized.put(key, snapshot.values().get(key));
        }
        return new Yaml().dump(normalized);
    }

    /**
     * Validates registry-backed values once the Severed Chains registries are available.
     * The parser intentionally cannot perform this validation before the game starts.
     */
    public static void validateDeferredRegistryReferences(
        final IrongoonConfigSnapshot snapshot,
        final Predicate<String> elementExists,
        final Predicate<String> itemExists,
        final Predicate<String> equipmentExists
    ) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(elementExists, "elementExists");
        Objects.requireNonNull(itemExists, "itemExists");
        Objects.requireNonNull(equipmentExists, "equipmentExists");

        validateRegistryList(snapshot, "characterElementOverride", id -> isElementAlias(id) || elementExists.test(id), "element");
        validateRegistryList(snapshot, "dragoonElementOverride", id -> isElementAlias(id) || elementExists.test(id), "element");
        validateRegistryList(snapshot, "shopContentsItemPool", itemExists, "item");
        validateRegistryList(snapshot, "shopContentsEquipmentPool", equipmentExists, "equipment");
        validateRegistryList(snapshot, "shopContentsRecalled", id -> itemExists.test(id) || equipmentExists.test(id), "item or equipment");
    }

    /** Validates positional and numeric character references once the supported roster size is known. */
    public static void validateCharacterReferences(final IrongoonConfigSnapshot snapshot, final int characterCount) {
        if(characterCount <= 0) throw new IllegalArgumentException("Irongoon character count must be positive");

        validateCharacterList(snapshot, "battlePartyOverride", characterCount, true);
        validateCharacterList(snapshot, "battlePartyPool", characterCount, false);
        validatePositionalList(snapshot, "characterElementOverride", characterCount);
        validatePositionalList(snapshot, "dragoonElementOverride", characterCount);
    }

    private static boolean isElementAlias(final String id) {
        return switch(id.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "skip", "none", "noelement", "dark", "water", "fire", "wind", "earth", "light", "thunder", "divine" -> true;
            default -> false;
        };
    }

    private static String displaySection(final IrongoonConfigSchema.Section section) {
        final String name = section.name().replace('_', ' ').toLowerCase(java.util.Locale.ROOT);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private static Object normalize(final String key, final Object value, final String source) {
        if(IrongoonConfigSchema.BOOLEAN_KEYS.contains(key)) {
            if(!(value instanceof Boolean)) throw invalid(source, key, "a boolean");
            return value;
        }
        if(IrongoonConfigSchema.INTEGER_KEYS.contains(key)) return integer(value, source, key);
        if(IrongoonConfigSchema.INTEGER_LIST_KEYS.contains(key)) return integerList(value, source, key);
        if(IrongoonConfigSchema.STRING_LIST_KEYS.contains(key)) return stringList(value, source, key);
        if("publicSeed".equals(key)) {
            if(!(value instanceof String seed) || !seed.matches("[0-9a-fA-F]+")) throw invalid(source, key, "a hexadecimal seed");
            try { Long.parseLong(seed, 16); } catch(final NumberFormatException exception) { throw invalid(source, key, "a signed 64-bit hexadecimal seed"); }
            return seed;
        }
        if(!(value instanceof String enumValue) || enumValue.isBlank()) throw invalid(source, key, "an enum name");
        validateEnum(key, enumValue, source);
        return enumValue;
    }

    private static List<Integer> integerList(final Object value, final String source, final String key) {
        if(!(value instanceof List<?> list)) throw invalid(source, key, "a list of integers");
        final List<Integer> result = new ArrayList<>();
        for(final Object item : list) result.add(integer(item, source, key));
        return List.copyOf(result);
    }

    private static List<String> stringList(final Object value, final String source, final String key) {
        if(!(value instanceof List<?> list)) throw invalid(source, key, "a list of strings");
        final List<String> result = new ArrayList<>();
        for(final Object item : list) {
            if(!(item instanceof String text) || text.isBlank()) throw invalid(source, key, "a list of non-blank strings");
            result.add(text);
        }
        return List.copyOf(result);
    }

    private static void validateRegistryList(final IrongoonConfigSnapshot snapshot, final String key, final Predicate<String> exists, final String registryType) {
        final Object value = snapshot.values().get(key);
        if(!(value instanceof List<?> values)) throw new IllegalStateException(snapshot.source() + ": " + key + " must be a list before deferred registry validation");

        for(final Object entry : values) {
            if(!(entry instanceof String id) || id.isBlank() || !exists.test(id)) {
                throw new IllegalStateException(snapshot.source() + ": " + key + " contains an unknown " + registryType + " registry id " + entry);
            }
        }
    }

    private static void validateCharacterList(final IrongoonConfigSnapshot snapshot, final String key, final int characterCount, final boolean allowSkip) {
        final List<?> values = (List<?>) snapshot.values().get(key);
        for(final Object value : values) {
            if(!(value instanceof Integer characterId) || characterId >= characterCount || characterId < (allowSkip ? -1 : 0)) {
                throw new IllegalStateException(snapshot.source() + ": " + key + " contains invalid character ID " + value);
            }
        }
    }

    private static void validatePositionalList(final IrongoonConfigSnapshot snapshot, final String key, final int characterCount) {
        final List<?> values = (List<?>) snapshot.values().get(key);
        if(values.size() > characterCount) {
            throw new IllegalStateException(snapshot.source() + ": " + key + " contains " + values.size() + " entries for a " + characterCount + " character roster");
        }
    }

    private static int integer(final Object value, final String source, final String key) {
        if(!(value instanceof Number number)) throw invalid(source, key, "a 32-bit integer");

        try {
            return new BigDecimal(number.toString()).intValueExact();
        } catch(final NumberFormatException | ArithmeticException exception) {
            throw invalid(source, key, "a lossless 32-bit integer");
        }
    }

    private static void validate(final Map<String, Object> values, final String source) {
        validateRange(values, source, "additionUnlockLevelLowerBound", "additionUnlockLevelUpperBound", 2, 60);
        validateRange(values, source, "additionDamageLowerPercentBound", "additionDamageUpperPercentBound", 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange(values, source, "additionSpLowerPercentBound", "additionSpUpperPercentBound", 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange(values, source, "additionDamageScalingLowerPercentBound", "additionDamageScalingUpperPercentBound", 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange(values, source, "additionSpScalingLowerPercentBound", "additionSpScalingUpperPercentBound", 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange(values, source, "additionHitTimingLowerPercentBound", "additionHitTimingUpperPercentBound", 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange(values, source, "additionStatusChanceLowerBound", "additionStatusChanceUpperBound", 0, 100);
        validateRange(values, source, "dragoonSpellPowerLowerPercentBound", "dragoonSpellPowerUpperPercentBound", 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange(values, source, "dragoonSpellMpCostLowerBound", "dragoonSpellMpCostUpperBound", 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange(values, source, "dragoonSpellAccuracyLowerBound", "dragoonSpellAccuracyUpperBound", 0, 100);
        validateRange(values, source, "dragoonSpellStatusChanceLowerBound", "dragoonSpellStatusChanceUpperBound", 0, 100);
        validateRange(values, source, "hpStatLowerPercentBound", "hpStatUpperPercentBound", 0, Integer.MAX_VALUE - 20);
        validateRange(values, source, "speedStatLowerPercentBound", "speedStatUpperPercentBound", 0, Integer.MAX_VALUE - 20);
        validateRange(values, source, "totalStatsMonstersLowerPercentBound", "totalStatsMonstersUpperPercentBound", 0, Integer.MAX_VALUE - 20);
        validateRange(values, source, "hpStatMonstersLowerPercentBound", "hpStatMonstersUpperPercentBound", 0, Integer.MAX_VALUE - 20);
        validateRange(values, source, "escapeChanceLowerBound", "escapeChanceUpperBound", 0, 100);
        validateRange(values, source, "shopQuantityLowerBound", "shopQuantityUpperBound", 0, Integer.MAX_VALUE - 1);
        validateRange(values, source, "speedStatMonstersLowerBound", "speedStatMonstersUpperBound", 0, Integer.MAX_VALUE - 1);
        for(final String key : List.of("monsterDefenseFloor", "monsterMagicDefenseFloor", "itemCarryLimit")) {
            if(values.containsKey(key) && (Integer) values.get(key) < 0) throw new IllegalStateException(source + ": " + key + " must be non-negative");
        }
        if(values.containsKey("battlePartySize")) {
            final int size = (Integer) values.get("battlePartySize");
            if(size < 1 || size > 3) throw new IllegalStateException(source + ": battlePartySize must be between 1 and 3");
        }
        if(values.containsKey("battleStageList")) for(final int stage : (List<Integer>) values.get("battleStageList")) if(stage < 0 || stage >= 95) throw new IllegalStateException(source + ": battleStageList entries must be between 0 and 94");

        validatePartyLists(values, source);
        validateAllowedModes(values, source);
    }

    @SuppressWarnings("unchecked")
    private static void validatePartyLists(final Map<String, Object> values, final String source) {
        final int partySize = (Integer) values.get("battlePartySize");
        final List<Integer> overrides = (List<Integer>) values.get("battlePartyOverride");
        final List<Integer> pool = (List<Integer>) values.get("battlePartyPool");
        if(overrides.size() > partySize) throw new IllegalStateException(source + ": battlePartyOverride cannot contain more entries than battlePartySize");
        if(overrides.stream().anyMatch(characterId -> characterId < -1)) throw new IllegalStateException(source + ": battlePartyOverride character IDs must be -1 or greater");
        if(pool.stream().anyMatch(characterId -> characterId < 0)) throw new IllegalStateException(source + ": battlePartyPool character IDs must be non-negative");

        if(!(Boolean) values.get("battlePartyDuplicates")) {
            final Set<Integer> explicitOverrides = new HashSet<>();
            for(final int characterId : overrides) {
                if(characterId >= 0 && !explicitOverrides.add(characterId)) throw new IllegalStateException(source + ": battlePartyOverride contains duplicate character " + characterId);
            }
            if(new HashSet<>(pool).size() != pool.size()) throw new IllegalStateException(source + ": battlePartyPool contains duplicate characters");
        }
    }

    private static void validateAllowedModes(final Map<String, Object> values, final String source) {
        if(AdditionStatuses.RANDOMIZE.name().equals(values.get("additionStatuses")) && !anyEnabled(values,
            "additionStatusAllowPetrify", "additionStatusAllowBewitch", "additionStatusAllowConfuse", "additionStatusAllowFear",
            "additionStatusAllowStun", "additionStatusAllowWeaponBlock", "additionStatusAllowDispirit", "additionStatusAllowPoison"
        )) {
            throw new IllegalStateException(source + ": addition status randomization requires at least one additionStatusAllow* entry");
        }

        final String spellEffects = (String) values.get("dragoonSpellEffects");
        if(!DragoonSpellEffects.STOCK.name().equals(spellEffects)
            && !DragoonSpellEffects.RANDOMIZE_RAW.name().equals(spellEffects)
            && !anyEnabled(values,
                "dragoonSpellAllowDamage", "dragoonSpellAllowHealHp", "dragoonSpellAllowRestoreMp", "dragoonSpellAllowRestoreSp",
                "dragoonSpellAllowCleanse", "dragoonSpellAllowDrainHp", "dragoonSpellAllowDrainMp", "dragoonSpellAllowDrainSp",
                "dragoonSpellAllowStatus", "dragoonSpellAllowBuff", "dragoonSpellAllowDebuff", "dragoonSpellAllowRegenHp",
                "dragoonSpellAllowRegenMp", "dragoonSpellAllowRegenSp"
            )) {
            throw new IllegalStateException(source + ": dragoon spell effect configuration cannot produce a living-target spell");
        }
    }

    private static boolean anyEnabled(final Map<String, Object> values, final String... keys) {
        for(final String key : keys) {
            if((Boolean) values.get(key)) return true;
        }
        return false;
    }

    private static void validateRange(final Map<String, Object> values, final String source, final String lowerKey, final String upperKey, final int minimum, final int maximum) {
        if(!values.containsKey(lowerKey) || !values.containsKey(upperKey)) return;
        final int lower = (Integer) values.get(lowerKey);
        final int upper = (Integer) values.get(upperKey);
        if(lower < minimum || upper > maximum || lower > upper) throw new IllegalStateException(source + ": " + lowerKey + " and " + upperKey + " must satisfy " + minimum + " <= lower <= upper <= " + maximum);
    }

    private static void validateEnum(final String key, final String value, final String source) {
        try {
            switch(key) {
                case "additionUnlocks" -> AdditionUnlocks.valueOf(value);
                case "additionBaseStats", "additionLevelScaling" -> AdditionValueMode.valueOf(value);
                case "additionHitTiming" -> AdditionHitTiming.valueOf(value);
                case "additionElements" -> AdditionElements.valueOf(value);
                case "additionStatuses" -> AdditionStatuses.valueOf(value);
                case "bodyTotalStatsPerLevel", "dragoonTotalStatsPerLevel" -> TotalStatsPerLevel.valueOf(value);
                case "bodyTotalStatsBounds", "dragoonStatsBounds" -> TotalStatsBounds.valueOf(value);
                case "bodyTotalStatsDistributionPerLevel", "dragoonTotalStatsDistributionPerLevel" -> TotalStatsDistributionPerLevel.valueOf(value);
                case "hpStatPerLevel" -> HPStatPerLevel.valueOf(value);
                case "speedStatPerLevel" -> SpeedStatPerLevel.valueOf(value);
                case "characterElements" -> CharacterElements.valueOf(value);
                case "enableAllCharacters" -> EnableAllCharacters.valueOf(value);
                case "battleParty" -> BattleParty.valueOf(value);
                case "enableAllDragoons" -> EnableAllDragoons.valueOf(value);
                case "dragoonElements" -> DragoonElements.valueOf(value);
                case "dragoonSpellUnlocks" -> DragoonSpellUnlocks.valueOf(value);
                case "dragoonSpellRandomizationPool" -> DragoonSpellRandomizationPool.valueOf(value);
                case "dragoonSpellStats" -> DragoonSpellStats.valueOf(value);
                case "dragoonSpellMpCosts" -> DragoonSpellMpCosts.valueOf(value);
                case "dragoonSpellElements" -> DragoonSpellElements.valueOf(value);
                case "dragoonSpellEffects" -> DragoonSpellEffects.valueOf(value);
                case "monsterTotalStatsPerLevel" -> TotalStatsMonsters.valueOf(value);
                case "hpStatMonsters" -> HPStatMonsters.valueOf(value);
                case "speedStatMonsters" -> SpeedStatMonsters.valueOf(value);
                case "statsVarianceMonsters" -> StatsVarianceMonsters.valueOf(value);
                case "monsterElements" -> ElementsMonsters.valueOf(value);
                case "noElementMonsters" -> NoElementMonsters.valueOf(value);
                case "shopAvailability" -> ShopAvailability.valueOf(value);
                case "shopQuantity" -> ShopQuantity.valueOf(value);
                case "shopQuantityLogic" -> ShopQuantityLogic.valueOf(value);
                case "shopContents" -> ShopContents.valueOf(value);
                case "shopDuplicates" -> ShopDuplicates.valueOf(value);
                case "battleStage" -> BattleStage.valueOf(value);
                case "battleMusic" -> BattleMusic.valueOf(value);
                case "escapeChance" -> EscapeChance.valueOf(value);
                default -> throw invalid(source, key, "a supported enum name");
            }
        } catch(final IllegalArgumentException exception) {
            throw new IllegalStateException(source + ": " + key + " has invalid value " + value, exception);
        }
    }

    private static IllegalStateException invalid(final String source, final String key, final String expected) {
        return new IllegalStateException(source + ": " + key + " must be " + expected);
    }
}
