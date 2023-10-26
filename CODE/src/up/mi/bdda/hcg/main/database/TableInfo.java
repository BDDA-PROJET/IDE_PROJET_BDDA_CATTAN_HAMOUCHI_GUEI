package up.mi.bdda.hcg.main.database;

import java.util.List;

public class TableInfo {
    private String relation;
    private List<ColInfo> colInfos;

    public TableInfo(String relation,int nombreColonne,List<ColInfo> colInfos){
        this.colInfos = colInfos;
        this.relation = relation;
    }
    
    public String getRelation() {
        return relation;
    }

    public int getNombreColonne(){
        return colInfos.size();
    }
    
}
