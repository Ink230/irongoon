package lod.irongoon.config;

import lod.irongoon.data.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

public class IrongoonConfig {
    private static IrongoonConfig INSTANCE = new IrongoonConfig();
    public static IrongoonConfig getInstance() {
        return INSTANCE;
    }

    public final String externalDataLoadPath = "./mods/irongoon/US/";
    public final String externalDataLoadExtension = ".csv";
    public final String externalConfigLoadPath = "./mods/irongoon/config.yaml";
    public String publicSeed;
    public long seed;
    public String campaignSeed;
    public boolean useRandomSeedOnNewCampaign;
    public int bodyNumberOfStatsAmount = 4;
    public int dragoonNumberOfStatsAmount = 4;
    public TotalStatsPerLevel bodyTotalStatsPerLevel;
    public TotalStatsPerLevel dragoonTotalStatsPerLevel;
    public TotalStatsMonsters monsterTotalStats;
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


    private IrongoonConfig() {
        this.regenerateConfig();
    }

    public final int battleStageSize = 95;
    
    public void regenerateConfig() {
        File configFile = new File(this.externalConfigLoadPath);

        Map<String, Object> yamlConfig;
        try (InputStream inputStream = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            yamlConfig = yaml.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            yamlConfig = Map.of();
        }

        this.publicSeed = (String) yamlConfig.getOrDefault("publicSeed", "AF51FA7B");
        this.seed = Long.parseLong(this.publicSeed, 16);
        this.useRandomSeedOnNewCampaign = (boolean) yamlConfig.getOrDefault("useRandomSeedOnNewCampaign", false);
        this.bodyTotalStatsPerLevel = TotalStatsPerLevel.valueOf((String) yamlConfig.getOrDefault("bodyTotalStatsPerLevel", "RANDOMIZE_BOUNDS_PER_LEVEL"));
        this.dragoonTotalStatsPerLevel = TotalStatsPerLevel.valueOf((String) yamlConfig.getOrDefault("dragoonTotalStatsPerLevel", "RANDOMIZE_BOUNDS_PER_LEVEL"));
        this.monsterTotalStats = TotalStatsMonsters.valueOf((String) yamlConfig.getOrDefault("monsterTotalStats", "RANDOMIZE_BOUNDS"));
        this.monsterDefenseFloor = (int) yamlConfig.getOrDefault("monsterDefenseFloor", 50);
        this.monsterMagicDefenseFloor = (int) yamlConfig.getOrDefault("monsterMagicDefenseFloor", 50);
        this.speedStatUpperPercentBound = (int) yamlConfig.getOrDefault("speedStatUpperPercentBound", 150);
        this.speedStatLowerPercentBound = (int) yamlConfig.getOrDefault("speedStatLowerPercentBound", 50);
        this.totalStatsMonstersUpperPercentBound = (int) yamlConfig.getOrDefault("totalStatsMonstersUpperPercentBound", 150);
        this.totalStatsMonstersLowerPercentBound = (int) yamlConfig.getOrDefault("totalStatsMonstersLowerPercentBound", 50);
        this.bodyTotalStatsBounds = TotalStatsBounds.valueOf((String) yamlConfig.getOrDefault("bodyTotalStatsBounds", "STOCK"));
        this.dragoonStatsBounds = TotalStatsBounds.valueOf((String) yamlConfig.getOrDefault("dragoonStatsBounds", "STOCK"));
        this.bodyTotalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.valueOf((String) yamlConfig.getOrDefault("bodyTotalStatsDistributionPerLevel", "RANDOM"));
        this.dragoonTotalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.valueOf((String) yamlConfig.getOrDefault("dragoonTotalStatsDistributionPerLevel", "RANDOM"));
        this.hpStatPerLevel = HPStatPerLevel.valueOf((String) yamlConfig.getOrDefault("hpStatPerLevel", "RANDOMIZE_BOUNDS_PERCENT_MODIFIED_PER_LEVEL"));
        this.hpStatUpperPercentBound = (int) yamlConfig.getOrDefault("hpStatUpperPercentBound", 150);
        this.hpStatLowerPercentBound = (int) yamlConfig.getOrDefault("hpStatLowerPercentBound", 50);
        this.speedStatPerLevel = SpeedStatPerLevel.valueOf((String) yamlConfig.getOrDefault("speedStatPerLevel", "RANDOMIZE_BOUNDS"));
        this.hpStatMonsters = HPStatMonsters.valueOf((String) yamlConfig.getOrDefault("hpStatMonster", "RANDOMIZE_BOUNDS"));
        this.hpStatMonstersUpperPercentBound = (int) yamlConfig.getOrDefault("hpStatMonstersUpperPercentBound", 150);
        this.hpStatMonstersLowerPercentBound = (int) yamlConfig.getOrDefault("hpStatMonstersLowerPercentBound", 50);
        this.speedStatMonsters = SpeedStatMonsters.valueOf((String) yamlConfig.getOrDefault("speedStatMonster", "RANDOMIZE_BOUNDS"));
        this.speedStatMonstersUpperBound = (int) yamlConfig.getOrDefault("speedStatMonstersUpperBound", 100);
        this.speedStatMonstersLowerBound = (int) yamlConfig.getOrDefault("speedStatMonstersLowerBound", 30);
        this.statsVarianceMonsters = StatsVarianceMonsters.valueOf((String) yamlConfig.getOrDefault("statsVarianceMonsters", "RANDOM_PERCENT_BOUNDS"));
        this.monsterElements = ElementsMonsters.valueOf((String) yamlConfig.getOrDefault("monsterElements", "MAINTAIN_STOCK"));
        this.noElementMonsters = NoElementMonsters.valueOf((String) yamlConfig.getOrDefault("noElementMonsters", "EXCLUDE"));
        this.battleStage = BattleStage.valueOf((String) yamlConfig.getOrDefault("battleStage", "RANDOM"));
        this.battleStageList = (List<Integer>) yamlConfig.getOrDefault("battleStageList", new ArrayList<>());
        this.escapeChance = EscapeChance.valueOf((String) yamlConfig.getOrDefault("escapeChance", "STOCK"));
        this.escapeChanceUpperBound = (int) yamlConfig.getOrDefault("escapeChanceUpperBound", 99);
        this.escapeChanceLowerBound = (int) yamlConfig.getOrDefault("escapeChanceLowerBound", 1);
        this.shopAvailability = ShopAvailability.valueOf((String) yamlConfig.getOrDefault("shopAvailability", "RANDOM"));
        this.shopQuantity = ShopQuantity.valueOf((String) yamlConfig.getOrDefault("shopQuantity", "RANDOMIZE_BOUNDS"));
        this.shopQuantityUpperBound = (int) yamlConfig.getOrDefault("shopQuantityUpperBound", "8");
        this.shopQuantityLowerBound = (int) yamlConfig.getOrDefault("shopQuantityLowerBound", "0");
        this.shopQuantityLogic = ShopQuantityLogic.valueOf((String) yamlConfig.getOrDefault("shopQuantityLogic", "RESPECT_SHOP_CONTENTS"));
        this.shopContents = ShopContents.valueOf((String) yamlConfig.getOrDefault("shopContents", "RANDOMIZE_ALL_MIXED"));
        this.shopContentsRecalled = (List<String>) yamlConfig.getOrDefault("shopContentsRecalled", new ArrayList<>());
        this.shopContentsItemPool = ((List<String>) yamlConfig.getOrDefault("shopContentsItemPool", new ArrayList<>())).stream().filter(entry -> !this.shopContentsRecalled.contains(entry)).collect(Collectors.toList());
        this.shopContentsEquipmentPool = ((List<String>) yamlConfig.getOrDefault("shopContentsEquipmentPool", new ArrayList<>())).stream().filter(entry -> !this.shopContentsRecalled.contains(entry)).collect(Collectors.toList());;
        this.shopDuplicates = ShopDuplicates.valueOf((String) yamlConfig.getOrDefault("shopDuplicates", "NONE"));
        this.battleMusic = BattleMusic.valueOf((String) yamlConfig.getOrDefault("battleMusic", "RANDOM"));
        this.itemCarryLimit = (int) yamlConfig.getOrDefault("itemCarryLimit", 0);
    }
}
