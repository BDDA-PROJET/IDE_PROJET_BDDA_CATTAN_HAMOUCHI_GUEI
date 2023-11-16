package up.mi.bdda.hcg.main.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import up.mi.bdda.hcg.api.DatabaseInfo;

public final class DBInfo implements DatabaseInfo {
    /** Le compteur de relations. */
    public static int compteurRelation; // TODO : useless in the meantime.
    /** La liste de relations. */
    private final Map<String, TableInfo> tablesInfos;
    /** L'unique instance du {@code DatabaseInfo}. */
    private static final DatabaseInfo gSingleton = new DBInfo();

    private DBInfo() {
        tablesInfos = new HashMap<>();
        compteurRelation = 0;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'finish'");
    }

    @Override
    public void addTableInfo(TableInfo tableInfo) {
        compteurRelation = tablesInfos.containsKey(tableInfo.getRelation()) ? compteurRelation : compteurRelation + 1;
        tablesInfos.put(tableInfo.getRelation(), tableInfo);
    }

    @Override
    public TableInfo getTableInfo(String relation) {
        TableInfo tableInfo = tablesInfos.get(relation);

        Objects.requireNonNull(tableInfo);
        return tableInfo;
    }

    /**
     * Retourne l'unique instance de {@code DatabaseInfo} .
     * 
     * @return une instance de DatabaseInfo
     */
    public static DatabaseInfo getSingleton() {
        return gSingleton;
    }

}
