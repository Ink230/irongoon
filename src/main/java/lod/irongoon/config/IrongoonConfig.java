package lod.irongoon.config;

import lod.irongoon.data.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class IrongoonConfig {
    private static final IrongoonConfig INSTANCE = new IrongoonConfig();
    public static IrongoonConfig getInstance() {
        return INSTANCE;
    }

    public final String externalDataLoadPath = "./mods/irongoon/US/";
    public final String externalDataLoadExtension = ".csv";
    public final String externalConfigLoadPath = "./mods/irongoon/config.yaml";
    public String publicSeed;
    public long seed;
    public String campaignSeed;
    public final boolean useRandomSeedOnNewCampaign;
    public final int bodyNumberOfStatsAmount = 4;
    public final int dragoonNumberOfStatsAmount = 4;
    public final TotalStatsPerLevel bodyTotalStatsPerLevel;
    public final TotalStatsPerLevel dragoonTotalStatsPerLevel;
    public final TotalStatsMonsters monsterTotalStats;
    public final int monsterDefenseFloor;
    public final int monsterMagicDefenseFloor;
    public final int speedStatUpperPercentBound;
    public final int speedStatLowerPercentBound;
    public final int totalStatsMonstersUpperPercentBound;
    public final int totalStatsMonstersLowerPercentBound;
    public final TotalStatsBounds bodyTotalStatsBounds;
    public final TotalStatsBounds dragoonStatsBounds;
    public final TotalStatsDistributionPerLevel bodyTotalStatsDistributionPerLevel;
    public final TotalStatsDistributionPerLevel dragoonTotalStatsDistributionPerLevel;
    public final HPStatPerLevel hpStatPerLevel;
    public final int hpStatUpperPercentBound;
    public final int hpStatLowerPercentBound;
    public final SpeedStatPerLevel speedStatPerLevel;
    public final HPStatMonsters hpStatMonsters;
    public final int hpStatMonstersUpperPercentBound;
    public final int hpStatMonstersLowerPercentBound;
    public final SpeedStatMonsters speedStatMonsters;
    public final int speedStatMonstersUpperBound;
    public final int speedStatMonstersLowerBound;
    public final StatsVarianceMonsters statsVarianceMonsters;
    public final ElementsMonsters monsterElements;
    public final NoElementMonsters noElementMonsters;
    public final BattleStage battleStage;
    public final List<Integer> battleStageList;
    public final EscapeChance escapeChance;
    public final int escapeChanceUpperBound;
    public final int escapeChanceLowerBound;
    public final ShopAvailability shopAvailability;

    private IrongoonConfig() {
        File configFile = new File(externalConfigLoadPath);

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
    }

    public final int battleStageSize = 95;
}
