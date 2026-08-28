package lod.irongoon.config;

import lod.irongoon.data.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class IrongoonConfig {
    private static final int MAX_RANDOM_PERCENT_BOUND = Integer.MAX_VALUE - 1;
    private static IrongoonConfig INSTANCE = new IrongoonConfig();
    public static IrongoonConfig getInstance() {
        return INSTANCE;
    }

    public final String externalDataLoadPath = "./mods/irongoon/irongoon-data/";
    public final String externalDataLoadExtension = ".csv";
    public final String externalConfigLoadPath = "./mods/irongoon/config.yaml";
    public String publicSeed;
    public long seed;
    public String campaignSeed;
    public boolean useRandomSeedOnNewCampaign;
    public boolean csvDataOverrides;
    public AdditionUnlocks additionUnlocks;
    public int additionUnlockLevelLowerBound;
    public int additionUnlockLevelUpperBound;
    public AdditionValueMode additionBaseStats;
    public boolean additionRandomizeDamage;
    public int additionDamageLowerPercentBound;
    public int additionDamageUpperPercentBound;
    public boolean additionRandomizeSp;
    public int additionSpLowerPercentBound;
    public int additionSpUpperPercentBound;
    public AdditionValueMode additionLevelScaling;
    public boolean additionRandomizeDamageScaling;
    public int additionDamageScalingLowerPercentBound;
    public int additionDamageScalingUpperPercentBound;
    public boolean additionRandomizeSpScaling;
    public int additionSpScalingLowerPercentBound;
    public int additionSpScalingUpperPercentBound;
    public AdditionHitTiming additionHitTiming;
    public int additionHitTimingLowerPercentBound;
    public int additionHitTimingUpperPercentBound;
    public AdditionElements additionElements;
    public boolean additionNoElement;
    public AdditionStatuses additionStatuses;
    public int additionStatusChanceLowerBound;
    public int additionStatusChanceUpperBound;
    public boolean additionStatusAllowPetrify;
    public boolean additionStatusAllowBewitch;
    public boolean additionStatusAllowConfuse;
    public boolean additionStatusAllowFear;
    public boolean additionStatusAllowStun;
    public boolean additionStatusAllowWeaponBlock;
    public boolean additionStatusAllowDispirit;
    public boolean additionStatusAllowPoison;
    public int bodyNumberOfStatsAmount = 4;
    public int dragoonNumberOfStatsAmount = 4;
    public TotalStatsPerLevel bodyTotalStatsPerLevel;
    public TotalStatsPerLevel dragoonTotalStatsPerLevel;
    public TotalStatsMonsters monsterTotalStatsPerLevel;
    public int monsterDefenseFloor;
    public int monsterMagicDefenseFloor;
    public int speedStatUpperPercentBound;
    public int speedStatLowerPercentBound;
    public int totalStatsMonstersUpperPercentBound;
    public int totalStatsMonstersLowerPercentBound;
    public TotalStatsBounds bodyTotalStatsBounds;
    public TotalStatsBounds dragoonStatsBounds;
    public TotalStatsDistributionPerLevel bodyTotalStatsDistributionPerLevel;
    public TotalStatsDistributionPerLevel dragoonTotalStatsDistributionPerLevel;
    public HPStatPerLevel hpStatPerLevel;
    public int hpStatUpperPercentBound;
    public int hpStatLowerPercentBound;
    public SpeedStatPerLevel speedStatPerLevel;
    public HPStatMonsters hpStatMonsters;
    public int hpStatMonstersUpperPercentBound;
    public int hpStatMonstersLowerPercentBound;
    public SpeedStatMonsters speedStatMonsters;
    public int speedStatMonstersUpperBound;
    public int speedStatMonstersLowerBound;
    public StatsVarianceMonsters statsVarianceMonsters;
    public ElementsMonsters monsterElements;
    public NoElementMonsters noElementMonsters;
    public BattleStage battleStage;
    public List<Integer> battleStageList;
    public EscapeChance escapeChance;
    public int escapeChanceUpperBound;
    public int escapeChanceLowerBound;
    public ShopAvailability shopAvailability;
    public ShopQuantity shopQuantity;
    public int shopQuantityUpperBound;
    public int shopQuantityLowerBound;
    public ShopQuantityLogic shopQuantityLogic;
    public ShopContents shopContents;
    public List<String> shopContentsItemPool;
    public List<String> shopContentsEquipmentPool;
    public List<String> shopContentsRecalled;
    public ShopDuplicates shopDuplicates;
    public BattleMusic battleMusic;
    public int itemCarryLimit;
    public CharacterElements characterElements;
    public boolean characterNoElement;
    public List<String> characterElementOverride;
    public EnableAllDragoons enableAllDragoons;
    public DragoonElements dragoonElements;
    public boolean dragoonNoElement;
    public List<String> dragoonElementOverride;
    public DragoonSpellUnlocks dragoonSpellUnlocks;
    public DragoonSpellRandomizationPool dragoonSpellRandomizationPool;
    public DragoonSpellStats dragoonSpellStats;
    public boolean dragoonSpellRandomizePower;
    public int dragoonSpellPowerLowerPercentBound;
    public int dragoonSpellPowerUpperPercentBound;
    public DragoonSpellMpCosts dragoonSpellMpCosts;
    public int dragoonSpellMpCostLowerBound;
    public int dragoonSpellMpCostUpperBound;
    public boolean dragoonSpellRandomizeAccuracy;
    public int dragoonSpellAccuracyLowerBound;
    public int dragoonSpellAccuracyUpperBound;
    public boolean dragoonSpellRandomizeStatusChance;
    public int dragoonSpellStatusChanceLowerBound;
    public int dragoonSpellStatusChanceUpperBound;
    public DragoonSpellElements dragoonSpellElements;
    public boolean dragoonSpellNoElement;
    public DragoonSpellEffects dragoonSpellEffects;
    public boolean dragoonSpellAllowDamage;
    public boolean dragoonSpellAllowHealHp;
    public boolean dragoonSpellAllowRestoreMp;
    public boolean dragoonSpellAllowRestoreSp;
    public boolean dragoonSpellAllowRevive;
    public boolean dragoonSpellAllowCleanse;
    public boolean dragoonSpellAllowDrainHp;
    public boolean dragoonSpellAllowDrainMp;
    public boolean dragoonSpellAllowDrainSp;
    public boolean dragoonSpellAllowStatus;
    public boolean dragoonSpellAllowBuff;
    public boolean dragoonSpellAllowDebuff;
    public boolean dragoonSpellAllowRegenHp;
    public boolean dragoonSpellAllowRegenMp;
    public boolean dragoonSpellAllowRegenSp;
    public EnableAllCharacters enableAllCharacters;
    public BattleParty battleParty;
    public List<Integer> battlePartyOverride;
    public int battlePartySize;
    public List<Integer> battlePartyPool;
    public boolean battlePartyDuplicates;

    private IrongoonConfig() {
        this.apply(IrongoonConfigCodec.fromValues("Blueprint", IrongoonConfigSchema.blueprintValues()));
    }

    public final int battleStageSize = 95;
    
    public void regenerateConfig() {
        this.apply(IrongoonConfigCodec.fromValues("Blueprint", IrongoonConfigSchema.blueprintValues()));
    }

    /**
     * Applies a snapshot only after the codec has parsed and validated every supplied value.
     * This remains the temporary compatibility seam while campaign-backed snapshots are added.
     */
    public void apply(final IrongoonConfigSnapshot snapshot) {
        final Map<String, Object> yamlConfig = snapshot.values();

        this.publicSeed = (String) required(yamlConfig, "publicSeed");
        this.seed = Long.parseLong(this.publicSeed, 16);
        this.useRandomSeedOnNewCampaign = (boolean) required(yamlConfig, "useRandomSeedOnNewCampaign");
        this.csvDataOverrides = (boolean) required(yamlConfig, "csvDataOverrides");
        this.additionUnlocks = AdditionUnlocks.valueOf((String) required(yamlConfig, "additionUnlocks"));
        this.additionUnlockLevelLowerBound = (int) required(yamlConfig, "additionUnlockLevelLowerBound");
        this.additionUnlockLevelUpperBound = (int) required(yamlConfig, "additionUnlockLevelUpperBound");
        this.additionBaseStats = AdditionValueMode.valueOf((String) required(yamlConfig, "additionBaseStats"));
        this.additionRandomizeDamage = (boolean) required(yamlConfig, "additionRandomizeDamage");
        this.additionDamageLowerPercentBound = (int) required(yamlConfig, "additionDamageLowerPercentBound");
        this.additionDamageUpperPercentBound = (int) required(yamlConfig, "additionDamageUpperPercentBound");
        this.additionRandomizeSp = (boolean) required(yamlConfig, "additionRandomizeSp");
        this.additionSpLowerPercentBound = (int) required(yamlConfig, "additionSpLowerPercentBound");
        this.additionSpUpperPercentBound = (int) required(yamlConfig, "additionSpUpperPercentBound");
        this.additionLevelScaling = AdditionValueMode.valueOf((String) required(yamlConfig, "additionLevelScaling"));
        this.additionRandomizeDamageScaling = (boolean) required(yamlConfig, "additionRandomizeDamageScaling");
        this.additionDamageScalingLowerPercentBound = (int) required(yamlConfig, "additionDamageScalingLowerPercentBound");
        this.additionDamageScalingUpperPercentBound = (int) required(yamlConfig, "additionDamageScalingUpperPercentBound");
        this.additionRandomizeSpScaling = (boolean) required(yamlConfig, "additionRandomizeSpScaling");
        this.additionSpScalingLowerPercentBound = (int) required(yamlConfig, "additionSpScalingLowerPercentBound");
        this.additionSpScalingUpperPercentBound = (int) required(yamlConfig, "additionSpScalingUpperPercentBound");
        this.additionHitTiming = AdditionHitTiming.valueOf((String) required(yamlConfig, "additionHitTiming"));
        this.additionHitTimingLowerPercentBound = (int) required(yamlConfig, "additionHitTimingLowerPercentBound");
        this.additionHitTimingUpperPercentBound = (int) required(yamlConfig, "additionHitTimingUpperPercentBound");
        this.additionElements = AdditionElements.valueOf((String) required(yamlConfig, "additionElements"));
        this.additionNoElement = (boolean) required(yamlConfig, "additionNoElement");
        this.additionStatuses = AdditionStatuses.valueOf((String) required(yamlConfig, "additionStatuses"));
        this.additionStatusChanceLowerBound = (int) required(yamlConfig, "additionStatusChanceLowerBound");
        this.additionStatusChanceUpperBound = (int) required(yamlConfig, "additionStatusChanceUpperBound");
        this.additionStatusAllowPetrify = (boolean) required(yamlConfig, "additionStatusAllowPetrify");
        this.additionStatusAllowBewitch = (boolean) required(yamlConfig, "additionStatusAllowBewitch");
        this.additionStatusAllowConfuse = (boolean) required(yamlConfig, "additionStatusAllowConfuse");
        this.additionStatusAllowFear = (boolean) required(yamlConfig, "additionStatusAllowFear");
        this.additionStatusAllowStun = (boolean) required(yamlConfig, "additionStatusAllowStun");
        this.additionStatusAllowWeaponBlock = (boolean) required(yamlConfig, "additionStatusAllowWeaponBlock");
        this.additionStatusAllowDispirit = (boolean) required(yamlConfig, "additionStatusAllowDispirit");
        this.additionStatusAllowPoison = (boolean) required(yamlConfig, "additionStatusAllowPoison");
        this.bodyTotalStatsPerLevel = TotalStatsPerLevel.valueOf((String) required(yamlConfig, "bodyTotalStatsPerLevel"));
        this.dragoonTotalStatsPerLevel = TotalStatsPerLevel.valueOf((String) required(yamlConfig, "dragoonTotalStatsPerLevel"));
        this.monsterTotalStatsPerLevel = TotalStatsMonsters.valueOf((String) required(yamlConfig, "monsterTotalStatsPerLevel"));
        this.monsterDefenseFloor = (int) required(yamlConfig, "monsterDefenseFloor");
        this.monsterMagicDefenseFloor = (int) required(yamlConfig, "monsterMagicDefenseFloor");
        this.speedStatUpperPercentBound = (int) required(yamlConfig, "speedStatUpperPercentBound");
        this.speedStatLowerPercentBound = (int) required(yamlConfig, "speedStatLowerPercentBound");
        this.totalStatsMonstersUpperPercentBound = (int) required(yamlConfig, "totalStatsMonstersUpperPercentBound");
        this.totalStatsMonstersLowerPercentBound = (int) required(yamlConfig, "totalStatsMonstersLowerPercentBound");
        this.bodyTotalStatsBounds = TotalStatsBounds.valueOf((String) required(yamlConfig, "bodyTotalStatsBounds"));
        this.dragoonStatsBounds = TotalStatsBounds.valueOf((String) required(yamlConfig, "dragoonStatsBounds"));
        this.bodyTotalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.valueOf((String) required(yamlConfig, "bodyTotalStatsDistributionPerLevel"));
        this.dragoonTotalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.valueOf((String) required(yamlConfig, "dragoonTotalStatsDistributionPerLevel"));
        this.hpStatPerLevel = HPStatPerLevel.valueOf((String) required(yamlConfig, "hpStatPerLevel"));
        this.hpStatUpperPercentBound = (int) required(yamlConfig, "hpStatUpperPercentBound");
        this.hpStatLowerPercentBound = (int) required(yamlConfig, "hpStatLowerPercentBound");
        this.speedStatPerLevel = SpeedStatPerLevel.valueOf((String) required(yamlConfig, "speedStatPerLevel"));
        this.hpStatMonsters = HPStatMonsters.valueOf((String) required(yamlConfig, "hpStatMonsters"));
        this.hpStatMonstersUpperPercentBound = (int) required(yamlConfig, "hpStatMonstersUpperPercentBound");
        this.hpStatMonstersLowerPercentBound = (int) required(yamlConfig, "hpStatMonstersLowerPercentBound");
        this.speedStatMonsters = SpeedStatMonsters.valueOf((String) required(yamlConfig, "speedStatMonsters"));
        this.speedStatMonstersUpperBound = (int) required(yamlConfig, "speedStatMonstersUpperBound");
        this.speedStatMonstersLowerBound = (int) required(yamlConfig, "speedStatMonstersLowerBound");
        this.statsVarianceMonsters = StatsVarianceMonsters.valueOf((String) required(yamlConfig, "statsVarianceMonsters"));
        this.monsterElements = ElementsMonsters.valueOf((String) required(yamlConfig, "monsterElements"));
        this.noElementMonsters = NoElementMonsters.valueOf((String) required(yamlConfig, "noElementMonsters"));
        this.battleStage = BattleStage.valueOf((String) required(yamlConfig, "battleStage"));
        this.battleStageList = (List<Integer>) required(yamlConfig, "battleStageList");
        this.escapeChance = EscapeChance.valueOf((String) required(yamlConfig, "escapeChance"));
        this.escapeChanceUpperBound = (int) required(yamlConfig, "escapeChanceUpperBound");
        this.escapeChanceLowerBound = (int) required(yamlConfig, "escapeChanceLowerBound");
        this.shopAvailability = ShopAvailability.valueOf((String) required(yamlConfig, "shopAvailability"));
        this.shopQuantity = ShopQuantity.valueOf((String) required(yamlConfig, "shopQuantity"));
        this.shopQuantityUpperBound = (int) required(yamlConfig, "shopQuantityUpperBound");
        this.shopQuantityLowerBound = (int) required(yamlConfig, "shopQuantityLowerBound");
        this.shopQuantityLogic = ShopQuantityLogic.valueOf((String) required(yamlConfig, "shopQuantityLogic"));
        this.shopContents = ShopContents.valueOf((String) required(yamlConfig, "shopContents"));
        this.shopContentsRecalled = (List<String>) required(yamlConfig, "shopContentsRecalled");
        this.shopContentsItemPool = ((List<String>) required(yamlConfig, "shopContentsItemPool")).stream().filter(entry -> !this.shopContentsRecalled.contains(entry)).collect(Collectors.toList());
        this.shopContentsEquipmentPool = ((List<String>) required(yamlConfig, "shopContentsEquipmentPool")).stream().filter(entry -> !this.shopContentsRecalled.contains(entry)).collect(Collectors.toList());;
        this.shopDuplicates = ShopDuplicates.valueOf((String) required(yamlConfig, "shopDuplicates"));
        this.battleMusic = BattleMusic.valueOf((String) required(yamlConfig, "battleMusic"));
        this.itemCarryLimit = (int) required(yamlConfig, "itemCarryLimit");
        this.characterElements = CharacterElements.valueOf((String) required(yamlConfig, "characterElements"));
        this.characterNoElement = (boolean) required(yamlConfig, "characterNoElement");
        this.characterElementOverride = (List<String>) required(yamlConfig, "characterElementOverride");
        this.enableAllDragoons = EnableAllDragoons.valueOf((String) required(yamlConfig, "enableAllDragoons"));
        this.dragoonElements = DragoonElements.valueOf((String) required(yamlConfig, "dragoonElements"));
        this.dragoonNoElement = (boolean) required(yamlConfig, "dragoonNoElement");
        this.dragoonElementOverride = (List<String>) required(yamlConfig, "dragoonElementOverride");
        this.dragoonSpellUnlocks = DragoonSpellUnlocks.valueOf((String) required(yamlConfig, "dragoonSpellUnlocks"));
        this.dragoonSpellRandomizationPool = DragoonSpellRandomizationPool.valueOf((String) required(yamlConfig, "dragoonSpellRandomizationPool"));
        this.dragoonSpellStats = DragoonSpellStats.valueOf((String) required(yamlConfig, "dragoonSpellStats"));
        this.dragoonSpellRandomizePower = (boolean) required(yamlConfig, "dragoonSpellRandomizePower");
        this.dragoonSpellPowerLowerPercentBound = (int) required(yamlConfig, "dragoonSpellPowerLowerPercentBound");
        this.dragoonSpellPowerUpperPercentBound = (int) required(yamlConfig, "dragoonSpellPowerUpperPercentBound");
        this.dragoonSpellMpCosts = DragoonSpellMpCosts.valueOf((String) required(yamlConfig, "dragoonSpellMpCosts"));
        this.dragoonSpellMpCostLowerBound = (int) required(yamlConfig, "dragoonSpellMpCostLowerBound");
        this.dragoonSpellMpCostUpperBound = (int) required(yamlConfig, "dragoonSpellMpCostUpperBound");
        this.dragoonSpellRandomizeAccuracy = (boolean) required(yamlConfig, "dragoonSpellRandomizeAccuracy");
        this.dragoonSpellAccuracyLowerBound = (int) required(yamlConfig, "dragoonSpellAccuracyLowerBound");
        this.dragoonSpellAccuracyUpperBound = (int) required(yamlConfig, "dragoonSpellAccuracyUpperBound");
        this.dragoonSpellRandomizeStatusChance = (boolean) required(yamlConfig, "dragoonSpellRandomizeStatusChance");
        this.dragoonSpellStatusChanceLowerBound = (int) required(yamlConfig, "dragoonSpellStatusChanceLowerBound");
        this.dragoonSpellStatusChanceUpperBound = (int) required(yamlConfig, "dragoonSpellStatusChanceUpperBound");
        this.dragoonSpellElements = DragoonSpellElements.valueOf((String) required(yamlConfig, "dragoonSpellElements"));
        this.dragoonSpellNoElement = (boolean) required(yamlConfig, "dragoonSpellNoElement");
        this.dragoonSpellEffects = DragoonSpellEffects.valueOf((String) required(yamlConfig, "dragoonSpellEffects"));
        this.dragoonSpellAllowDamage = (boolean) required(yamlConfig, "dragoonSpellAllowDamage");
        this.dragoonSpellAllowHealHp = (boolean) required(yamlConfig, "dragoonSpellAllowHealHp");
        this.dragoonSpellAllowRestoreMp = (boolean) required(yamlConfig, "dragoonSpellAllowRestoreMp");
        this.dragoonSpellAllowRestoreSp = (boolean) required(yamlConfig, "dragoonSpellAllowRestoreSp");
        this.dragoonSpellAllowRevive = (boolean) required(yamlConfig, "dragoonSpellAllowRevive");
        this.dragoonSpellAllowCleanse = (boolean) required(yamlConfig, "dragoonSpellAllowCleanse");
        this.dragoonSpellAllowDrainHp = (boolean) required(yamlConfig, "dragoonSpellAllowDrainHp");
        this.dragoonSpellAllowDrainMp = (boolean) required(yamlConfig, "dragoonSpellAllowDrainMp");
        this.dragoonSpellAllowDrainSp = (boolean) required(yamlConfig, "dragoonSpellAllowDrainSp");
        this.dragoonSpellAllowStatus = (boolean) required(yamlConfig, "dragoonSpellAllowStatus");
        this.dragoonSpellAllowBuff = (boolean) required(yamlConfig, "dragoonSpellAllowBuff");
        this.dragoonSpellAllowDebuff = (boolean) required(yamlConfig, "dragoonSpellAllowDebuff");
        this.dragoonSpellAllowRegenHp = (boolean) required(yamlConfig, "dragoonSpellAllowRegenHp");
        this.dragoonSpellAllowRegenMp = (boolean) required(yamlConfig, "dragoonSpellAllowRegenMp");
        this.dragoonSpellAllowRegenSp = (boolean) required(yamlConfig, "dragoonSpellAllowRegenSp");
        this.validateDragoonSpellConfig();
        this.enableAllCharacters = EnableAllCharacters.valueOf((String) required(yamlConfig, "enableAllCharacters"));
        this.battleParty = BattleParty.valueOf((String) required(yamlConfig, "battleParty"));
        this.battlePartyOverride = (List<Integer>) required(yamlConfig, "battlePartyOverride");
        this.battlePartySize = (int) required(yamlConfig, "battlePartySize");
        this.battlePartyPool = (List<Integer>) required(yamlConfig, "battlePartyPool");
        this.battlePartyDuplicates = (boolean) required(yamlConfig, "battlePartyDuplicates");
        this.normalizeAdditionUnlockLevels();
        this.validateAdditionConfig();
    }

    private static Object required(final Map<String, Object> yamlConfig, final String key) {
        final Object value = yamlConfig.get(key);
        if(value == null) throw new IllegalStateException("Validated Irongoon configuration is missing " + key);
        return value;
    }

    private void normalizeAdditionUnlockLevels() {
        if(this.additionUnlockLevelLowerBound == 1) this.additionUnlockLevelLowerBound = 2;
        if(this.additionUnlockLevelUpperBound == 1) this.additionUnlockLevelUpperBound = 2;
    }

    private void validateAdditionConfig() {
        validateRange("addition unlock levels", this.additionUnlockLevelLowerBound, this.additionUnlockLevelUpperBound, 2, 60);
        validateRange("addition damage percentages", this.additionDamageLowerPercentBound, this.additionDamageUpperPercentBound, 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange("addition SP percentages", this.additionSpLowerPercentBound, this.additionSpUpperPercentBound, 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange("addition damage scaling percentages", this.additionDamageScalingLowerPercentBound, this.additionDamageScalingUpperPercentBound, 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange("addition SP scaling percentages", this.additionSpScalingLowerPercentBound, this.additionSpScalingUpperPercentBound, 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange("addition hit timing percentages", this.additionHitTimingLowerPercentBound, this.additionHitTimingUpperPercentBound, 0, MAX_RANDOM_PERCENT_BOUND);
        validateRange("addition status chances", this.additionStatusChanceLowerBound, this.additionStatusChanceUpperBound, 0, 100);

        if(this.additionStatuses == AdditionStatuses.RANDOMIZE && !this.hasAllowedAdditionStatus()) {
            throw new IllegalStateException("Addition status randomization requires at least one additionStatusAllow* entry");
        }
    }

    private boolean hasAllowedAdditionStatus() {
        return this.additionStatusAllowPetrify
            || this.additionStatusAllowBewitch
            || this.additionStatusAllowConfuse
            || this.additionStatusAllowFear
            || this.additionStatusAllowStun
            || this.additionStatusAllowWeaponBlock
            || this.additionStatusAllowDispirit
            || this.additionStatusAllowPoison;
    }

    private static void validateRange(final String name, final int lowerBound, final int upperBound, final int minimum, final int maximum) {
        if(lowerBound < minimum || upperBound > maximum || lowerBound > upperBound) {
            throw new IllegalStateException(
                "Invalid " + name + ": expected " + minimum + " <= lower <= upper <= " + maximum
                    + ", got " + lowerBound + ".." + upperBound
            );
        }
    }

    private void validateDragoonSpellConfig() {
        this.validateBounds("dragoonSpellPowerPercent", this.dragoonSpellPowerLowerPercentBound, this.dragoonSpellPowerUpperPercentBound, 0, Integer.MAX_VALUE);
        this.validateBounds("dragoonSpellMpCost", this.dragoonSpellMpCostLowerBound, this.dragoonSpellMpCostUpperBound, 0, Integer.MAX_VALUE);
        this.validateBounds("dragoonSpellAccuracy", this.dragoonSpellAccuracyLowerBound, this.dragoonSpellAccuracyUpperBound, 0, 100);
        this.validateBounds("dragoonSpellStatusChance", this.dragoonSpellStatusChanceLowerBound, this.dragoonSpellStatusChanceUpperBound, 0, 100);

        if(this.dragoonSpellEffects != DragoonSpellEffects.STOCK && this.dragoonSpellEffects != DragoonSpellEffects.RANDOMIZE_RAW && !this.hasSafeDragoonSpellEffect()) {
            throw new IllegalStateException("Dragoon spell effect configuration cannot produce a living-target spell; enable damage, healing, restore, cleanse, drain, status, buff, debuff, or regeneration");
        }
    }

    private void validateBounds(final String name, final int lower, final int upper, final int minimum, final int maximum) {
        if(lower < minimum || upper > maximum || lower > upper) {
            throw new IllegalStateException(name + " bounds must satisfy " + minimum + " <= lower <= upper <= " + maximum + "; received " + lower + " to " + upper);
        }
    }

    private boolean hasSafeDragoonSpellEffect() {
        return this.dragoonSpellAllowDamage
            || this.dragoonSpellAllowHealHp
            || this.dragoonSpellAllowRestoreMp
            || this.dragoonSpellAllowRestoreSp
            || this.dragoonSpellAllowCleanse
            || this.dragoonSpellAllowDrainHp
            || this.dragoonSpellAllowDrainMp
            || this.dragoonSpellAllowDrainSp
            || this.dragoonSpellAllowStatus
            || this.dragoonSpellAllowBuff
            || this.dragoonSpellAllowDebuff
            || this.dragoonSpellAllowRegenHp
            || this.dragoonSpellAllowRegenMp
            || this.dragoonSpellAllowRegenSp;
    }
}
