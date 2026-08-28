package lod.irongoon.config;

import lod.irongoon.data.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.yaml.snakeyaml.Yaml;

/** YAML boundary: parse, normalize and validate everything before runtime state is touched. */
public final class IrongoonConfigCodec {
    private static final int MAX_RANDOM_PERCENT_BOUND = Integer.MAX_VALUE - 1;

    private IrongoonConfigCodec() {}

    public static IrongoonConfigSnapshot readLegacy(final File file) {
        if(!file.isFile()) return new IrongoonConfigSnapshot(file.getPath(), Map.of(), List.of("Legacy config.yaml was not found; runtime defaults are active"));
        try (InputStream input = new FileInputStream(file)) {
            final Object document = new Yaml().load(input);
            if(document == null) return new IrongoonConfigSnapshot(file.getPath(), Map.of(), List.of());
            if(!(document instanceof Map<?, ?> raw)) throw new IllegalStateException("Irongoon configuration must be a YAML mapping: " + file);
            return fromValues(file.getPath(), raw);
        } catch (final IOException exception) {
            throw new IllegalStateException("Unable to read Irongoon configuration " + file, exception);
        }
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

    /** Serializes only canonical keys in the schema's stable blueprint order. */
    public static String serialize(final IrongoonConfigSnapshot snapshot) {
        final Map<String, Object> normalized = new LinkedHashMap<>();
        for(final String key : IrongoonConfigSchema.KEYS) {
            if(snapshot.values().containsKey(key)) normalized.put(key, snapshot.values().get(key));
        }
        return new Yaml().dump(normalized);
    }

    private static Object normalize(final String key, final Object value, final String source) {
        if(IrongoonConfigSchema.BOOLEAN_KEYS.contains(key)) {
            if(!(value instanceof Boolean)) throw invalid(source, key, "a boolean");
            return value;
        }
        if(IrongoonConfigSchema.INTEGER_KEYS.contains(key)) {
            if(!(value instanceof Number number) || number.longValue() < Integer.MIN_VALUE || number.longValue() > Integer.MAX_VALUE) throw invalid(source, key, "a 32-bit integer");
            return number.intValue();
        }
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
        for(final Object item : list) {
            if(!(item instanceof Number number) || number.longValue() < Integer.MIN_VALUE || number.longValue() > Integer.MAX_VALUE) throw invalid(source, key, "a list of 32-bit integers");
            result.add(number.intValue());
        }
        return List.copyOf(result);
    }

    private static List<String> stringList(final Object value, final String source, final String key) {
        if(!(value instanceof List<?> list)) throw invalid(source, key, "a list of strings");
        final List<String> result = new ArrayList<>();
        for(final Object item : list) {
            if(!(item instanceof String text)) throw invalid(source, key, "a list of strings");
            result.add(text);
        }
        return List.copyOf(result);
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
