package up.mi.bdda.hcg.api;

import up.mi.bdda.hcg.main.database.DBInfo;
import up.mi.bdda.hcg.main.database.TableInfo;

/**
 * Cette classe doit contenir des informations concernant l’ensemble des
 * relations de la base de données.
 */
public interface DatabaseInfo {
    /**
     * Cette méthode doit lire les informations du fichier {@code DBInfo.save} et
     * remplit le {@code DatabaseInfo} avec ces informations.
     */
    void init();

    /**
     * Cette méthode doit sauvegarder les informations du {@code DatabaseInfo} dans
     * un fichier nommé {@code DBInfo.save}.
     */
    void finish();

    /**
     * Cette méthode prend en argument une {@code TableInfo} puis la rajoute à la
     * liste des relations et actualise (si besoin) le compteur de relation.
     * 
     * @param tableInfo
     */
    void addTableInfo(TableInfo tableInfo);

    /**
     * Cette méthode prend en argument le nom d’une relation et retourne la
     * {@code TableInfo} associée.
     * 
     * @param relation
     * @return
     */
    TableInfo getTableInfo(String relation);

    /**
     * Retourne l'unique instance de {@code DatabaseInfo} .
     * 
     * @return une instance de DatabaseInfo
     */
    public static DatabaseInfo getSingleton() {
        return DBInfo.getSingleton();
    }

}
