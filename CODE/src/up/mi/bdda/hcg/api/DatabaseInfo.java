package up.mi.bdda.hcg.api;

import up.mi.bdda.hcg.main.database.DbInfo;
import up.mi.bdda.hcg.main.database.TableInfo;

public interface DatabaseInfo {

    void init();
    void finish();

    void addTableInfo(TableInfo tableInfo);
    TableInfo getTableInfo(String relation);

    public static DatabaseInfo getSingleton(){
        return DbInfo.getSingleton();
    }

    public int getCompteurRelation();

    
}
