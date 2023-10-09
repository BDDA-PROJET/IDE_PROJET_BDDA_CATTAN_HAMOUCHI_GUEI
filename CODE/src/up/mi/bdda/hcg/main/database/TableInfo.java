package up.mi.bdda.hcg.main.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe doit stocker les informations de schéma de la relation, c’est à
 * dire :
 * <ul>
 * <li>le nom de la relation (chaîne de caractères)
 * <li>le nombre de colonnes (entier)
 * <li>les noms des colonnes
 * <li>les types des colonnes
 */
public class TableInfo {
    /** Le nom de la relation */
    private String relation;
    /** La liste des colonnes de la relation */
    private List<ColInfo> colInfos;

    public TableInfo(String relation, List<ColInfo> colInfos) {
        this.colInfos = new ArrayList<>(colInfos);
        this.relation = relation;
    }

    public String getRelation() {
        return relation;
    }

    public int getNombreColonne() {
        return colInfos.size();
    }

    public ColInfo[] getColInfos() {
        return colInfos.stream().toArray(ColInfo[]::new);
    }

}
