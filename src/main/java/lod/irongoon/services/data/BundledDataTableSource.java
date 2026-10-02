package lod.irongoon.services.data;

import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;
import lod.irongoon.parse.external.CSVParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class BundledDataTableSource implements DataTableSource {
    private static final BundledDataTableSource INSTANCE = new BundledDataTableSource();
    private static final String RESOURCE_DIRECTORY = "/irongoon-data/";

    public static BundledDataTableSource getInstance() {
        return INSTANCE;
    }

    private final CSVParser parser;

    private BundledDataTableSource() {
        this.parser = CSVParser.getInstance();
    }

    @Override
    public String name() {
        return "bundled compatibility CSV";
    }

    @Override
    public boolean supports(final ExternalData data) {
        return BundledDataTableSource.class.getResource(this.resourcePath(data)) != null;
    }

    @Override
    public DataTable load(final ExternalData data) {
        final String resourcePath = this.resourcePath(data);
        final InputStream inputStream = BundledDataTableSource.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalStateException("Bundled compatibility data " + data + " is unavailable at " + resourcePath);
        }

        try (inputStream; var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return new DataTable(this.parser.load(reader));
        } catch (final IOException | RuntimeException exception) {
            throw new IllegalStateException("Failed to load bundled compatibility data " + data + " from " + resourcePath, exception);
        }
    }

    private String resourcePath(final ExternalData data) {
        return RESOURCE_DIRECTORY + data.getValue() + ".csv";
    }
}
