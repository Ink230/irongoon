package lod.irongoon.services.data;

import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;

public record LoadedDataTable(
    ExternalData data,
    DataTable table,
    DataSourceKind source,
    String reason,
    boolean liveUpdatesEnabled
) { }
