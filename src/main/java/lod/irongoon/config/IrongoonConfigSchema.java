package lod.irongoon.config;

import lod.irongoon.data.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.Yaml;

/**
 * Canonical public configuration contract. Ordering is the normalized YAML ordering and
 * therefore deliberately follows the shipped blueprint rather than Java field order.
 */
public final class IrongoonConfigSchema {
    private static final String BLUEPRINT_YAML = """
# Seed
publicSeed: 2F055604

# Additions
additionUnlocks: RANDOMIZE_SEQUENCE
additionUnlockLevelLowerBound: 2
additionUnlockLevelUpperBound: 30
additionBaseStats: RANDOMIZE_BOUNDS
additionRandomizeDamage: TRUE
additionDamageLowerPercentBound: 50
additionDamageUpperPercentBound: 150
additionRandomizeSp: TRUE
additionSpLowerPercentBound: 50
additionSpUpperPercentBound: 250
additionLevelScaling: RANDOMIZE_BOUNDS
additionRandomizeDamageScaling: TRUE
additionDamageScalingLowerPercentBound: 50
additionDamageScalingUpperPercentBound: 150
additionRandomizeSpScaling: TRUE
additionSpScalingLowerPercentBound: 50
additionSpScalingUpperPercentBound: 250
additionHitTiming: STOCK
additionHitTimingLowerPercentBound: 50
additionHitTimingUpperPercentBound: 150
additionElements: RANDOMIZE
additionNoElement: FALSE
additionStatuses: RANDOMIZE
additionStatusChanceLowerBound: 35
additionStatusChanceUpperBound: 100
additionStatusAllowPetrify: TRUE
additionStatusAllowBewitch: TRUE
additionStatusAllowConfuse: TRUE
additionStatusAllowFear: TRUE
additionStatusAllowStun: TRUE
additionStatusAllowWeaponBlock: TRUE
additionStatusAllowDispirit: TRUE
additionStatusAllowPoison: TRUE
# Characters
bodyTotalStatsPerLevel: RANDOMIZE_BOUNDS_PER_LEVEL
bodyTotalStatsBounds: STOCK
bodyTotalStatsDistributionPerLevel: RANDOM
hpStatPerLevel: RANDOMIZE_BOUNDS_PER_LEVEL
hpStatUpperPercentBound: 150
hpStatLowerPercentBound: 75
speedStatPerLevel: RANDOMIZE_BOUNDS
speedStatUpperPercentBound: 150
speedStatLowerPercentBound: 30
characterElements: RANDOM_CAMPAIGN
characterNoElement: FALSE
characterElementOverride: [] # positional (ex dart first): ["skip", "fire", "water", "wind", "earth", "dark", "light", "thunder", "noelement", "divine"]
# Party
enableAllCharacters: PERMANENTLY # only works on new campaign start
battleParty: RANDOM_BATTLE
battlePartyOverride: [] # slot0: rose, slot1: meru, slot2: randomized would be ex: [3, 6]
battlePartySize: 3
battlePartyPool: [] # list of char ids to randomize from ex: [4,0,2,5], empty is all available
battlePartyDuplicates: TRUE
# Dragoons
enableAllDragoons: PERMANENTLY # only works on new campaign start
dragoonTotalStatsPerLevel: RANDOMIZE_BOUNDS_PER_LEVEL
dragoonStatsBounds: STOCK
dragoonTotalStatsDistributionPerLevel: RANDOM
dragoonElements: RANDOM_CAMPAIGN
dragoonNoElement: FALSE
dragoonElementOverride: [] # positional by character id; built-in aliases or full registry ids such as mod_id:element_id
dragoonSpellUnlocks: RANDOMIZE_SEQUENCE
dragoonSpellRandomizationPool: GLOBAL
dragoonSpellStats: RANDOMIZE_BOUNDS
dragoonSpellRandomizePower: TRUE
dragoonSpellPowerLowerPercentBound: 50
dragoonSpellPowerUpperPercentBound: 250
dragoonSpellMpCosts: RANDOM_CAMPAIGN_CHARACTER
dragoonSpellMpCostLowerBound: 5
dragoonSpellMpCostUpperBound: 120
dragoonSpellRandomizeAccuracy: FALSE
dragoonSpellAccuracyLowerBound: 90
dragoonSpellAccuracyUpperBound: 100
dragoonSpellRandomizeStatusChance: TRUE
dragoonSpellStatusChanceLowerBound: 35
dragoonSpellStatusChanceUpperBound: 100
dragoonSpellElements: SHUFFLE
dragoonSpellNoElement: FALSE
dragoonSpellEffects: RANDOMIZE_INDEPENDENT
dragoonSpellAllowDamage: TRUE
dragoonSpellAllowHealHp: TRUE
dragoonSpellAllowRestoreMp: TRUE
dragoonSpellAllowRestoreSp: TRUE
dragoonSpellAllowRevive: TRUE
dragoonSpellAllowCleanse: TRUE
dragoonSpellAllowDrainHp: TRUE
dragoonSpellAllowDrainMp: TRUE
dragoonSpellAllowDrainSp: TRUE
dragoonSpellAllowStatus: TRUE
dragoonSpellAllowBuff: TRUE
dragoonSpellAllowDebuff: TRUE
dragoonSpellAllowRegenHp: TRUE
dragoonSpellAllowRegenMp: TRUE
dragoonSpellAllowRegenSp: TRUE
# Monsters
monsterTotalStatsPerLevel: RANDOMIZE_BOUNDS
totalStatsMonstersUpperPercentBound: 150
totalStatsMonstersLowerPercentBound: 50
monsterDefenseFloor: 50
monsterMagicDefenseFloor: 50
hpStatMonsters: RANDOMIZE_BOUNDS
hpStatMonstersUpperPercentBound: 150
hpStatMonstersLowerPercentBound: 50
speedStatMonsters: RANDOMIZE_BOUNDS
speedStatMonstersUpperBound: 70
speedStatMonstersLowerBound: 30
statsVarianceMonsters: RANDOM_PERCENT_BOUNDS
monsterElements: RANDOMIZE
noElementMonsters: EXCLUDE
# Shops
shopAvailability: STOCK
shopQuantity: RANDOMIZE_BOUNDS
shopQuantityUpperBound: 8
shopQuantityLowerBound: 1
shopQuantityLogic: RESPECT_SHOP_CONTENTS
shopContents: RANDOMIZE_ALL
shopContentsItemPool: []
shopContentsEquipmentPool: []
shopContentsRecalled: [
  "lod:sachet", "lod:enemy_healing_potion", "lod:psyche_bomb",
  "lod:psyche_bomb_x", "lod:soul_eater", "lod:ultimate_wargod",
  "lod:legend_casque", "lod:armor_of_legend", "lod:phantom_shield"
]
shopDuplicates: NONE
# Chests
# Drops
# Items
itemCarryLimit: 2
# Enemies
# Sound
# Data sources
csvDataOverrides: FALSE
# Options
# Custom
# Scaling
# Additions
# Randomizer
useRandomSeedOnNewCampaign: TRUE
# Encounters
battleStage: RANDOM
battleStageList: []
battleMusic: RANDOM
escapeChance: RANDOMIZE_BOUNDS
escapeChanceUpperBound: 99
escapeChanceLowerBound: 1
        """;
    public enum Section {
        GENERAL, ADDITIONS, CHARACTER_STATS, CHARACTER_ELEMENTS, PARTY, DRAGOON_STATS, DRAGOON_ACCESS_AND_ELEMENTS, DRAGOON_SPELLS, MONSTER_STATS_AND_ELEMENTS, SHOPS, ITEMS, ENCOUNTERS
    }

    public enum ControlKind {
        CHECKBOX, DROPDOWN, NUMBER_SPINNER, TEXTBOX, INTEGER_LIST, STRING_LIST
    }

    public enum Lifecycle {
        NEW_CAMPAIGN_ONLY, REBUILT_ON_LOAD, NEXT_OWNING_EVENT, INACTIVE
    }

    public enum ListItemKind {
        NONE, INTEGER, STRING
    }

    public enum EditorCategory {
        NONE, HEXADECIMAL_SEED, ENUM, BATTLE_PARTY_CHARACTER, BATTLE_STAGE, CHARACTER_ELEMENT, DRAGOON_ELEMENT, SHOP_ITEM, SHOP_EQUIPMENT, SHOP_RECALLED
    }

    public record Setting(
        String key,
        List<String> aliases,
        Section section,
        ControlKind control,
        Lifecycle lifecycle,
        Object blueprintValue,
        Class<? extends Enum<?>> enumType,
        List<String> choices,
        Integer minimum,
        Integer maximum,
        String pairedKey,
        ListItemKind listItemKind,
        EditorCategory editorCategory,
        String help
    ) {}
    public static final List<String> KEYS = List.of(("""
        publicSeed,useRandomSeedOnNewCampaign,csvDataOverrides,
        additionUnlocks,additionUnlockLevelLowerBound,additionUnlockLevelUpperBound,additionBaseStats,additionRandomizeDamage,additionDamageLowerPercentBound,additionDamageUpperPercentBound,additionRandomizeSp,additionSpLowerPercentBound,additionSpUpperPercentBound,additionLevelScaling,additionRandomizeDamageScaling,additionDamageScalingLowerPercentBound,additionDamageScalingUpperPercentBound,additionRandomizeSpScaling,additionSpScalingLowerPercentBound,additionSpScalingUpperPercentBound,additionHitTiming,additionHitTimingLowerPercentBound,additionHitTimingUpperPercentBound,additionElements,additionNoElement,additionStatuses,additionStatusChanceLowerBound,additionStatusChanceUpperBound,additionStatusAllowPetrify,additionStatusAllowBewitch,additionStatusAllowConfuse,additionStatusAllowFear,additionStatusAllowStun,additionStatusAllowWeaponBlock,additionStatusAllowDispirit,additionStatusAllowPoison,
        bodyTotalStatsPerLevel,bodyTotalStatsBounds,bodyTotalStatsDistributionPerLevel,hpStatPerLevel,hpStatUpperPercentBound,hpStatLowerPercentBound,speedStatPerLevel,speedStatUpperPercentBound,speedStatLowerPercentBound,characterElements,characterNoElement,characterElementOverride,enableAllCharacters,battleParty,battlePartyOverride,battlePartySize,battlePartyPool,battlePartyDuplicates,
        enableAllDragoons,dragoonTotalStatsPerLevel,dragoonStatsBounds,dragoonTotalStatsDistributionPerLevel,dragoonElements,dragoonNoElement,dragoonElementOverride,dragoonSpellUnlocks,dragoonSpellRandomizationPool,dragoonSpellStats,dragoonSpellRandomizePower,dragoonSpellPowerLowerPercentBound,dragoonSpellPowerUpperPercentBound,dragoonSpellMpCosts,dragoonSpellMpCostLowerBound,dragoonSpellMpCostUpperBound,dragoonSpellRandomizeAccuracy,dragoonSpellAccuracyLowerBound,dragoonSpellAccuracyUpperBound,dragoonSpellRandomizeStatusChance,dragoonSpellStatusChanceLowerBound,dragoonSpellStatusChanceUpperBound,dragoonSpellElements,dragoonSpellNoElement,dragoonSpellEffects,dragoonSpellAllowDamage,dragoonSpellAllowHealHp,dragoonSpellAllowRestoreMp,dragoonSpellAllowRestoreSp,dragoonSpellAllowRevive,dragoonSpellAllowCleanse,dragoonSpellAllowDrainHp,dragoonSpellAllowDrainMp,dragoonSpellAllowDrainSp,dragoonSpellAllowStatus,dragoonSpellAllowBuff,dragoonSpellAllowDebuff,dragoonSpellAllowRegenHp,dragoonSpellAllowRegenMp,dragoonSpellAllowRegenSp,
        monsterTotalStatsPerLevel,totalStatsMonstersUpperPercentBound,totalStatsMonstersLowerPercentBound,monsterDefenseFloor,monsterMagicDefenseFloor,hpStatMonsters,hpStatMonstersUpperPercentBound,hpStatMonstersLowerPercentBound,speedStatMonsters,speedStatMonstersUpperBound,speedStatMonstersLowerBound,statsVarianceMonsters,monsterElements,noElementMonsters,
        shopAvailability,shopQuantity,shopQuantityUpperBound,shopQuantityLowerBound,shopQuantityLogic,shopContents,shopContentsItemPool,shopContentsEquipmentPool,shopContentsRecalled,shopDuplicates,itemCarryLimit,battleStage,battleStageList,battleMusic,escapeChance,escapeChanceUpperBound,escapeChanceLowerBound
        """).replaceAll("\\s", "").split(","));
    public static final Set<String> KEY_SET = Set.copyOf(new LinkedHashSet<>(KEYS));
    public static final Set<String> BOOLEAN_KEYS = Set.of(("""
        useRandomSeedOnNewCampaign,csvDataOverrides,additionRandomizeDamage,additionRandomizeSp,additionRandomizeDamageScaling,additionRandomizeSpScaling,additionNoElement,additionStatusAllowPetrify,additionStatusAllowBewitch,additionStatusAllowConfuse,additionStatusAllowFear,additionStatusAllowStun,additionStatusAllowWeaponBlock,additionStatusAllowDispirit,additionStatusAllowPoison,characterNoElement,battlePartyDuplicates,dragoonNoElement,dragoonSpellRandomizePower,dragoonSpellRandomizeAccuracy,dragoonSpellRandomizeStatusChance,dragoonSpellNoElement,dragoonSpellAllowDamage,dragoonSpellAllowHealHp,dragoonSpellAllowRestoreMp,dragoonSpellAllowRestoreSp,dragoonSpellAllowRevive,dragoonSpellAllowCleanse,dragoonSpellAllowDrainHp,dragoonSpellAllowDrainMp,dragoonSpellAllowDrainSp,dragoonSpellAllowStatus,dragoonSpellAllowBuff,dragoonSpellAllowDebuff,dragoonSpellAllowRegenHp,dragoonSpellAllowRegenMp,dragoonSpellAllowRegenSp
        """).replaceAll("\\s", "").split(","));
    public static final Set<String> INTEGER_KEYS = Set.of(("""
        additionUnlockLevelLowerBound,additionUnlockLevelUpperBound,additionDamageLowerPercentBound,additionDamageUpperPercentBound,additionSpLowerPercentBound,additionSpUpperPercentBound,additionDamageScalingLowerPercentBound,additionDamageScalingUpperPercentBound,additionSpScalingLowerPercentBound,additionSpScalingUpperPercentBound,additionHitTimingLowerPercentBound,additionHitTimingUpperPercentBound,additionStatusChanceLowerBound,additionStatusChanceUpperBound,hpStatUpperPercentBound,hpStatLowerPercentBound,speedStatUpperPercentBound,speedStatLowerPercentBound,battlePartySize,dragoonSpellPowerLowerPercentBound,dragoonSpellPowerUpperPercentBound,dragoonSpellMpCostLowerBound,dragoonSpellMpCostUpperBound,dragoonSpellAccuracyLowerBound,dragoonSpellAccuracyUpperBound,dragoonSpellStatusChanceLowerBound,dragoonSpellStatusChanceUpperBound,totalStatsMonstersUpperPercentBound,totalStatsMonstersLowerPercentBound,monsterDefenseFloor,monsterMagicDefenseFloor,hpStatMonstersUpperPercentBound,hpStatMonstersLowerPercentBound,speedStatMonstersUpperBound,speedStatMonstersLowerBound,shopQuantityUpperBound,shopQuantityLowerBound,itemCarryLimit,escapeChanceUpperBound,escapeChanceLowerBound
        """).replaceAll("\\s", "").split(","));
    public static final Set<String> INTEGER_LIST_KEYS = Set.of("battlePartyOverride", "battlePartyPool", "battleStageList");
    public static final Set<String> STRING_LIST_KEYS = Set.of("characterElementOverride", "dragoonElementOverride", "shopContentsItemPool", "shopContentsEquipmentPool", "shopContentsRecalled");
    public static final Set<String> LEGACY_KEYS = Set.of("hpStatMonster", "speedStatMonster", "dragoonSpellRandomizeMpCost");
    private static final Map<String, Object> BLUEPRINT_VALUES = loadBlueprint();
    private static final Map<String, Setting> SETTINGS = buildSettings();

    static {
        if(KEYS.size() != 125 || KEY_SET.size() != 125) throw new IllegalStateException("Irongoon config schema must contain exactly 125 unique keys");
        if(BLUEPRINT_VALUES.size() != 125 || !BLUEPRINT_VALUES.keySet().equals(KEY_SET)) throw new IllegalStateException("Irongoon config blueprint must contain every canonical setting exactly once");
        if(SETTINGS.size() != 125 || !SETTINGS.keySet().equals(KEY_SET)) throw new IllegalStateException("Irongoon config metadata must contain every canonical setting exactly once");
    }

    private IrongoonConfigSchema() {}

    public static String canonicalKey(final String key) {
        return switch(key) {
            case "hpStatMonster" -> "hpStatMonsters";
            case "speedStatMonster" -> "speedStatMonsters";
            case "dragoonSpellRandomizeMpCost" -> "dragoonSpellMpCosts";
            default -> key;
        };
    }

    public static boolean isKnown(final String key) {
        return KEY_SET.contains(key) || LEGACY_KEYS.contains(key);
    }

    public static Setting setting(final String key) {
        final String canonical = canonicalKey(key);
        if(!KEY_SET.contains(canonical)) throw new IllegalArgumentException("Unknown Irongoon setting " + key);
        return SETTINGS.get(canonical);
    }

    public static List<Setting> settings() {
        return KEYS.stream().map(SETTINGS::get).toList();
    }

    public static Map<String, Object> blueprintValues() {
        return BLUEPRINT_VALUES;
    }

    private static Section section(final String key) {
        if(key.startsWith("addition")) return Section.ADDITIONS;
        if(key.startsWith("dragoonSpell")) return Section.DRAGOON_SPELLS;
        if(key.equals("enableAllDragoons") || key.equals("dragoonElements") || key.equals("dragoonNoElement") || key.equals("dragoonElementOverride")) return Section.DRAGOON_ACCESS_AND_ELEMENTS;
        if(key.startsWith("dragoon")) return Section.DRAGOON_STATS;
        if(key.startsWith("monster") || key.startsWith("hpStatMonsters") || key.startsWith("speedStatMonsters") || key.startsWith("totalStatsMonsters") || key.startsWith("statsVariance") || key.startsWith("noElementMonsters")) return Section.MONSTER_STATS_AND_ELEMENTS;
        if(key.startsWith("shop")) return Section.SHOPS;
        if(key.equals("itemCarryLimit")) return Section.ITEMS;
        if(key.startsWith("battleStage") || key.startsWith("battleMusic") || key.startsWith("escapeChance")) return Section.ENCOUNTERS;
        if(key.startsWith("character")) return Section.CHARACTER_ELEMENTS;
        if(key.startsWith("battleParty") || key.equals("enableAllCharacters")) return Section.PARTY;
        if(key.contains("Stat") || key.startsWith("bodyTotal") || key.startsWith("speed")) return Section.CHARACTER_STATS;
        return Section.GENERAL;
    }

    private static ControlKind control(final String key) {
        if(BOOLEAN_KEYS.contains(key)) return ControlKind.CHECKBOX;
        if(INTEGER_KEYS.contains(key)) return ControlKind.NUMBER_SPINNER;
        if(INTEGER_LIST_KEYS.contains(key)) return ControlKind.INTEGER_LIST;
        if(STRING_LIST_KEYS.contains(key)) return ControlKind.STRING_LIST;
        if(key.equals("publicSeed")) return ControlKind.TEXTBOX;
        return ControlKind.DROPDOWN;
    }

    private static Lifecycle lifecycle(final String key) {
        if(key.equals("enableAllCharacters") || key.equals("enableAllDragoons")) return Lifecycle.NEW_CAMPAIGN_ONLY;
        if(key.startsWith("escapeChance")) return Lifecycle.INACTIVE;
        if(section(key) == Section.MONSTER_STATS_AND_ELEMENTS || key.startsWith("shop") || key.startsWith("item") || key.startsWith("battle")) return Lifecycle.NEXT_OWNING_EVENT;
        return Lifecycle.REBUILT_ON_LOAD;
    }

    private static Map<String, Setting> buildSettings() {
        final Map<String, Setting> settings = new LinkedHashMap<>();
        for(final String key : KEYS) {
            final Class<? extends Enum<?>> enumType = enumType(key);
            settings.put(key, new Setting(
                key,
                aliases(key),
                section(key),
                control(key),
                lifecycle(key),
                BLUEPRINT_VALUES.get(key),
                enumType,
                enumType == null ? List.of() : Arrays.stream(enumType.getEnumConstants()).map(Enum::name).toList(),
                minimum(key),
                maximum(key),
                pairedKey(key),
                listItemKind(key),
                editorCategory(key),
                help(key)
            ));
        }
        return Map.copyOf(settings);
    }

    private static List<String> aliases(final String key) {
        return LEGACY_KEYS.stream().filter(alias -> canonicalKey(alias).equals(key)).sorted().toList();
    }

    private static Class<? extends Enum<?>> enumType(final String key) {
        return switch(key) {
            case "additionUnlocks" -> AdditionUnlocks.class;
            case "additionBaseStats", "additionLevelScaling" -> AdditionValueMode.class;
            case "additionHitTiming" -> AdditionHitTiming.class;
            case "additionElements" -> AdditionElements.class;
            case "additionStatuses" -> AdditionStatuses.class;
            case "bodyTotalStatsPerLevel", "dragoonTotalStatsPerLevel" -> TotalStatsPerLevel.class;
            case "bodyTotalStatsBounds", "dragoonStatsBounds" -> TotalStatsBounds.class;
            case "bodyTotalStatsDistributionPerLevel", "dragoonTotalStatsDistributionPerLevel" -> TotalStatsDistributionPerLevel.class;
            case "hpStatPerLevel" -> HPStatPerLevel.class;
            case "speedStatPerLevel" -> SpeedStatPerLevel.class;
            case "characterElements" -> CharacterElements.class;
            case "enableAllCharacters" -> EnableAllCharacters.class;
            case "battleParty" -> BattleParty.class;
            case "enableAllDragoons" -> EnableAllDragoons.class;
            case "dragoonElements" -> DragoonElements.class;
            case "dragoonSpellUnlocks" -> DragoonSpellUnlocks.class;
            case "dragoonSpellRandomizationPool" -> DragoonSpellRandomizationPool.class;
            case "dragoonSpellStats" -> DragoonSpellStats.class;
            case "dragoonSpellMpCosts" -> DragoonSpellMpCosts.class;
            case "dragoonSpellElements" -> DragoonSpellElements.class;
            case "dragoonSpellEffects" -> DragoonSpellEffects.class;
            case "monsterTotalStatsPerLevel" -> TotalStatsMonsters.class;
            case "hpStatMonsters" -> HPStatMonsters.class;
            case "speedStatMonsters" -> SpeedStatMonsters.class;
            case "statsVarianceMonsters" -> StatsVarianceMonsters.class;
            case "monsterElements" -> ElementsMonsters.class;
            case "noElementMonsters" -> NoElementMonsters.class;
            case "shopAvailability" -> ShopAvailability.class;
            case "shopQuantity" -> ShopQuantity.class;
            case "shopQuantityLogic" -> ShopQuantityLogic.class;
            case "shopContents" -> ShopContents.class;
            case "shopDuplicates" -> ShopDuplicates.class;
            case "battleStage" -> BattleStage.class;
            case "battleMusic" -> BattleMusic.class;
            case "escapeChance" -> EscapeChance.class;
            default -> null;
        };
    }

    private static Integer minimum(final String key) {
        if(!INTEGER_KEYS.contains(key)) return null;
        if(key.startsWith("additionUnlockLevel")) return 2;
        if(key.equals("battlePartySize")) return 1;
        return 0;
    }

    private static Integer maximum(final String key) {
        if(!INTEGER_KEYS.contains(key)) return null;
        if(key.startsWith("additionUnlockLevel")) return 60;
        if(key.equals("battlePartySize")) return 3;
        if(key.contains("Accuracy") || key.contains("StatusChance") || key.startsWith("escapeChance")) return 100;
        if(key.startsWith("hpStat") || key.startsWith("speedStat") || key.startsWith("totalStatsMonsters")) return Integer.MAX_VALUE - 20;
        if(key.endsWith("DefenseFloor")) return Integer.MAX_VALUE;
        return Integer.MAX_VALUE - 1;
    }

    private static String pairedKey(final String key) {
        final String candidate = key.contains("Lower") ? key.replace("Lower", "Upper") : key.contains("Upper") ? key.replace("Upper", "Lower") : null;
        return candidate != null && KEY_SET.contains(candidate) ? candidate : null;
    }

    private static ListItemKind listItemKind(final String key) {
        if(INTEGER_LIST_KEYS.contains(key)) return ListItemKind.INTEGER;
        if(STRING_LIST_KEYS.contains(key)) return ListItemKind.STRING;
        return ListItemKind.NONE;
    }

    private static EditorCategory editorCategory(final String key) {
        return switch(key) {
            case "publicSeed" -> EditorCategory.HEXADECIMAL_SEED;
            case "battlePartyOverride", "battlePartyPool" -> EditorCategory.BATTLE_PARTY_CHARACTER;
            case "battleStageList" -> EditorCategory.BATTLE_STAGE;
            case "characterElementOverride" -> EditorCategory.CHARACTER_ELEMENT;
            case "dragoonElementOverride" -> EditorCategory.DRAGOON_ELEMENT;
            case "shopContentsItemPool" -> EditorCategory.SHOP_ITEM;
            case "shopContentsEquipmentPool" -> EditorCategory.SHOP_EQUIPMENT;
            case "shopContentsRecalled" -> EditorCategory.SHOP_RECALLED;
            default -> enumType(key) == null ? EditorCategory.NONE : EditorCategory.ENUM;
        };
    }

    private static String help(final String key) {
        final String label = key.replaceAll("([a-z])([A-Z])", "$1 $2");
        return Character.toUpperCase(label.charAt(0)) + label.substring(1) + ". " + lifecycle(key).name().replace('_', ' ').toLowerCase(java.util.Locale.ROOT) + ".";
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> loadBlueprint() {
        final Object document = new Yaml().load(BLUEPRINT_YAML);
        if(!(document instanceof Map<?, ?> raw)) throw new IllegalStateException("Irongoon config blueprint is not a YAML mapping");
        final Map<String, Object> values = new LinkedHashMap<>();
        for(final var entry : raw.entrySet()) values.put((String) entry.getKey(), entry.getValue());
        return Map.copyOf(values);
    }
}
