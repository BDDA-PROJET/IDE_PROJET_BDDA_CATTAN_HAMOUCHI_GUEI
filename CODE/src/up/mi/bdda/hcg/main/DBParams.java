package up.mi.bdda.hcg.main;

/**
 * Cette classe représente les paramètres de l'application et fournit des
 * valeurs par défaut pour ces paramètres.
 */
public class DBParams {

	/** Le chemin vers le dossier DB. */
	public static String DBPath;

	/** La taille d’une page. */
	public static int SGBDPageSize;

	/** Le nombre maximal de pages dans un fichier. */
	public static int DMFFileCount;

	public static void main(String[] args) {

		DBPath = "DB";
		SGBDPageSize = 4096;
		DMFFileCount = 4;
	}
}
