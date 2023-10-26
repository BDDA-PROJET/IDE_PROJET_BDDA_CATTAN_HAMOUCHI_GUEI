package up.mi.bdda.hcg.main.database;

import java.util.HashMap;
import java.util.Map;

import up.mi.bdda.hcg.api.DatabaseInfo;

public  final class DbInfo implements DatabaseInfo{
    private int compteurRelation;
    private final Map<String, TableInfo> tablesInfos;

    private static final DatabaseInfo gSingleton = new DbInfo();

    private DbInfo(){
        tablesInfos = new HashMap<>();
        compteurRelation =0;
    }
    
    @Override
    public int getCompteurRelation() {
        return compteurRelation;
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
       tablesInfos.put(tableInfo.getRelation(), tableInfo);
       compteurRelation++;
    }


    @Override
    public TableInfo getTableInfo(String relation) {
      return tablesInfos.get(relation);    
    }

    public static DatabaseInfo getSingleton(){
        return gSingleton;
    }
    
}
