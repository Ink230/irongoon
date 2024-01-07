package lod.irongoon.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import lod.irongoon.config.modifiers.*;

public class Config {
    private final static String EXTERNAL_DATA_LOAD_PATH =  "mods/irongoon/US/";
    private final static String EXTERNAL_CONFIG_LOAD_PATH = "mods/irongoon/Config.yaml";

    private final static String EXTERNAL_DATA_LOAD_EXTENSION = ".csv";

    public static String getFullPath(String filename) {
        return EXTERNAL_DATA_LOAD_PATH + filename + EXTERNAL_DATA_LOAD_EXTENSION;
    }

    public static final String publicSeed;
    public static final long seed;
    public static final int bodyNumberOfStatsAmount = 4;
    public static final int dragoonNumberOfStatsAmount = 4;
    public static final TotalStatsPerLevel bodyTotalStatsPerLevel;
    public static final TotalStatsPerLevel dragoonTotalStatsPerLevel;
    public static final TotalStatsMonsters monsterTotalStats;
    public static final int monsterDefenseFloor;
    public static final int monsterMagicDefenseFloor;
    public static final int nonBaselineStatsUpperPercentBound;
    public static final int nonBaselineStatsLowerPercentBound;
    public static final int totalStatsMonstersUpperPercentBound;
    public static final int totalStatsMonstersLowerPercentBound;
    public static final TotalStatsBounds bodyTotalStatsBounds;
    public static final TotalStatsBounds dragoonStatsBounds;
    public static final TotalStatsDistributionPerLevel bodyTotalStatsDistributionPerLevel;
    public static final TotalStatsDistributionPerLevel dragoonTotalStatsDistributionPerLevel;
    public static final HPStatPerLevel hpStatPerLevel;
    public static final int hpStatUpperPercentBound;
    public static final int hpStatLowerPercentBound;
    public static final SpeedStatPerLevel speedStatPerLevel;
    public static final HPStatMonsters hpStatMonsters;
    public static final int hpStatMonstersUpperPercentBound;
    public static final int hpStatMonstersLowerPercentBound;
    public static final SpeedStatMonsters speedStatMonsters;
    public static final int speedStatMonstersUpperBound;
    public static final int speedStatMonstersLowerBound;
    public static final StatsVarianceMonsters statsVarianceMonsters;

    static {
        File configFile = new File(EXTERNAL_CONFIG_LOAD_PATH);

        Map<String, Object> yamlConfig;
        try (InputStream inputStream = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            yamlConfig = yaml.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            yamlConfig = Map.of();
        }

        publicSeed = (String) yamlConfig.getOrDefault("publicSeed", "7963AC95E13873B6");
        seed = Long.parseUnsignedLong(publicSeed, 16);
        bodyTotalStatsPerLevel = TotalStatsPerLevel.valueOf((String) yamlConfig.getOrDefault("bodyTotalStatsPerLevel", "RANDOMIZE_BOUNDS_PER_LEVEL"));
        dragoonTotalStatsPerLevel = TotalStatsPerLevel.valueOf((String) yamlConfig.getOrDefault("dragoonTotalStatsPerLevel", "RANDOMIZE_BOUNDS_PER_LEVEL"));
        monsterTotalStats = TotalStatsMonsters.valueOf((String) yamlConfig.getOrDefault("monsterTotalStats", "RANDOMIZE_BOUNDS"));
        monsterDefenseFloor = (int) yamlConfig.getOrDefault("monsterDefenseFloor", 50);
        monsterMagicDefenseFloor = (int) yamlConfig.getOrDefault("monsterMagicDefenseFloor", 50);
        nonBaselineStatsUpperPercentBound = (int) yamlConfig.getOrDefault("nonBaselineStatsUpperPercentBound", 150);
        nonBaselineStatsLowerPercentBound = (int) yamlConfig.getOrDefault("nonBaselineStatsLowerPercentBound", 50);
        totalStatsMonstersUpperPercentBound = (int) yamlConfig.getOrDefault("totalStatsMonstersUpperPercentBound", 150);
        totalStatsMonstersLowerPercentBound = (int) yamlConfig.getOrDefault("totalStatsMonstersLowerPercentBound", 50);
        bodyTotalStatsBounds = TotalStatsBounds.valueOf((String) yamlConfig.getOrDefault("bodyTotalStatsBounds", "STOCK"));
        dragoonStatsBounds = TotalStatsBounds.valueOf((String) yamlConfig.getOrDefault("dragoonStatsBounds", "STOCK"));
        bodyTotalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.valueOf((String) yamlConfig.getOrDefault("bodyTotalStatsDistributionPerLevel", "RANDOM"));
        dragoonTotalStatsDistributionPerLevel = TotalStatsDistributionPerLevel.valueOf((String) yamlConfig.getOrDefault("dragoonTotalStatsDistributionPerLevel", "RANDOM"));
        hpStatPerLevel = HPStatPerLevel.valueOf((String) yamlConfig.getOrDefault("hpStatPerLevel", "RANDOMIZE_BOUNDS_PERCENT_MODIFIED_PER_LEVEL"));
        hpStatUpperPercentBound = (int) yamlConfig.getOrDefault("hpStatUpperPercentBound", 150);
        hpStatLowerPercentBound = (int) yamlConfig.getOrDefault("hpStatLowerPercentBound", 50);
        speedStatPerLevel = SpeedStatPerLevel.valueOf((String) yamlConfig.getOrDefault("speedStatPerLevel", "RANDOMIZE_BOUNDS"));
        hpStatMonsters = HPStatMonsters.valueOf((String) yamlConfig.getOrDefault("hpStatMonster", "RANDOMIZE_BOUNDS"));
        hpStatMonstersUpperPercentBound = (int) yamlConfig.getOrDefault("hpStatMonstersUpperPercentBound", 150);
        hpStatMonstersLowerPercentBound = (int) yamlConfig.getOrDefault("hpStatMonstersLowerPercentBound", 50);
        speedStatMonsters = SpeedStatMonsters.valueOf((String) yamlConfig.getOrDefault("speedStatMonster", "RANDOMIZE_BOUNDS"));
        speedStatMonstersUpperBound = (int) yamlConfig.getOrDefault("speedStatMonstersUpperBound", 100);
        speedStatMonstersLowerBound = (int) yamlConfig.getOrDefault("speedStatMonstersLowerBound", 30);
        statsVarianceMonsters = StatsVarianceMonsters.valueOf((String) yamlConfig.getOrDefault("statsVarianceMonsters", "RANDOM_PERCENT_BOUNDS"));
    }
}
