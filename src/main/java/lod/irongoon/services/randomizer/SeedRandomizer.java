package lod.irongoon.services.randomizer;

import lod.irongoon.config.IrongoonConfig;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

public class SeedRandomizer {
    private static final SeedRandomizer INSTANCE = new SeedRandomizer();
    public static SeedRandomizer getInstance() { return INSTANCE; }

    private SeedRandomizer() {}

    private final IrongoonConfig config = IrongoonConfig.getInstance();

    public String generateNewSeed() {
        var random = new Random();
        var result = random.nextInt();
        var hexString = String.format("%08X", result);

        return hexString;
    }

    public void saveNewSeed(String seed) {
        Map<String, Object> yamlConfig;
        try (InputStream inputStream = new FileInputStream(config.externalConfigLoadPath)) {
            Yaml yaml = new Yaml();
            yamlConfig = yaml.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            yamlConfig = Map.of();
        }

        yamlConfig.put("publicSeed", seed);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yamlWriter = new Yaml(options);
        try {
            FileWriter writer = new FileWriter(config.externalConfigLoadPath);
            yamlWriter.dump(yamlConfig, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
