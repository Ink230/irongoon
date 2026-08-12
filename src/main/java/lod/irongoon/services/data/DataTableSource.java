package lod.irongoon.services.data;

import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DataTable;

public interface DataTableSource {
    String name();

    boolean supports(ExternalData data);

    DataTable load(ExternalData data);
}
