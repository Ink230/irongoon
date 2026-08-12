package lod.irongoon.services.data;

import lod.irongoon.config.IrongoonConfig;
import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;
import lod.irongoon.parse.external.CSVParser;
import lod.irongoon.parse.external.DataParser;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CSVDataTableSource implements DataTableSource {
    private final IrongoonConfig config;
    private final DataParser parser;

    public CSVDataTableSource() {
        this(IrongoonConfig.getInstance(), CSVParser.getInstance());
    }

    CSVDataTableSource(final IrongoonConfig config, final DataParser parser) {
        this.config = config;
        this.parser = parser;
    }

    @Override
    public String name() {
        return "CSV";
    }

    @Override
    public boolean supports(final ExternalData data) {
        return Files.isRegularFile(this.path(data));
    }

    @Override
    public DataTable load(final ExternalData data) {
        final Path path = this.path(data);
        try {
            return new DataTable(this.parser.load(path.toString()));
        } catch (final RuntimeException exception) {
            throw new IllegalStateException("Failed to load " + data + " from CSV " + path, exception);
        }
    }

    public Path path(final ExternalData data) {
        return Path.of(this.config.externalDataLoadPath + data.getValue() + this.config.externalDataLoadExtension);
    }
}
