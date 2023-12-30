package lod.irongoon.parse.game;

import lod.irongoon.data.EnemyStatsData;
import lod.irongoon.data.ExternalData;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.services.DataTables;

import java.util.HashMap;

public class MonsterStatsParser {
    private static final MonsterStatsParser INSTANCE = new MonsterStatsParser();
    public static MonsterStatsParser getInstance() { return INSTANCE; }

    private MonsterStatsParser() {}

    private final DataTableParser dataTableAccessor = DataTableParser.getInstance();
    private final int chunkSize = 1;

    private final ExternalData dataTableKey = ExternalData.MONSTER_STATS;

    public HashMap<String, Integer> getMonsterStatsById(int monsterId) {
        var statsArray = dataTableAccessor.getRowFromDataTable(monsterId, dataTableKey);

        HashMap<String, Integer> monsterStatsMap = new HashMap<>();

        for (EnemyStatsData stat : EnemyStatsData.values()) {
            int statValue = Integer.parseInt(String.valueOf(statsArray[stat.getValue()]));
            monsterStatsMap.put(stat.name(), statValue);
        }

        return monsterStatsMap;
    }
}
