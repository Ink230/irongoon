package lod.irongoon.config;

public class IrongoonConfig {
    private static final IrongoonConfig INSTANCE = new IrongoonConfig();

    public static IrongoonConfig getInstance() {
        return INSTANCE;
    }

    private IrongoonConfig() {}

    public final String ExternalDataLoadPath = "./mods/data/US/";
    public final String ExternalDataLoadExtension = ".csv";
}
