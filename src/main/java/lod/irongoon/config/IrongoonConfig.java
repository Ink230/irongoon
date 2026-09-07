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
    }

}
