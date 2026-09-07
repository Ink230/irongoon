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
     * Revalidates and canonicalizes the complete snapshot before mutating runtime configuration.
     */
    public void apply(final IrongoonConfigSnapshot snapshot) {
        final IrongoonConfigSnapshot validated = IrongoonConfigCodec.fromValues(snapshot.source(), snapshot.values());
        for(final IrongoonConfigSchema.Setting setting : IrongoonConfigSchema.settings()) {
            setting.runtimeSetter().set(this, validated.values().get(setting.key()));
        }

        this.seed = Long.parseLong(this.publicSeed, 16);
        this.shopContentsItemPool = this.shopContentsItemPool.stream()
            .filter(entry -> !this.shopContentsRecalled.contains(entry))
            .collect(Collectors.toList());
        this.shopContentsEquipmentPool = this.shopContentsEquipmentPool.stream()
            .filter(entry -> !this.shopContentsRecalled.contains(entry))
            .collect(Collectors.toList());
        this.normalizeAdditionUnlockLevels();
        this.validateAdditionConfig();
        this.validateDragoonSpellConfig();
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
