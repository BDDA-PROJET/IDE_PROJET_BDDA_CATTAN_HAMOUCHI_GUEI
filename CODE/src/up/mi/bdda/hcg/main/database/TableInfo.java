package up.mi.bdda.hcg.main.database;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import up.mi.bdda.hcg.main.page.HeaderPage;
import up.mi.bdda.hcg.main.page.PageId;

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
    /** L'identifiant de la page */
    private HeaderPage headerPageId;
    /** Le nom de la relation */
    private String relation;
    /** La liste des colonnes de la relation */
    private List<ColInfo> colInfos;

    public TableInfo(ByteBuffer buff, String relation, List<ColInfo> colInfos) {
        headerPageId = new HeaderPage(buff);
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
