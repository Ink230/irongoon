package lod.irongoon.parse.game;

import lod.irongoon.data.ExternalData;
import lod.irongoon.services.DataTables;

public class MonsterStatsParser {
    private static final MonsterStatsParser INSTANCE = new MonsterStatsParser();
    public static MonsterStatsParser getInstance() { return INSTANCE; }

    private MonsterStatsParser() {}

    private final DataTableParser dataTableAccessor = DataTableParser.getInstance();
    private final int chunkSize = 1;

    private final ExternalData dataTableKey = ExternalData.MONSTER_STATS;
}
