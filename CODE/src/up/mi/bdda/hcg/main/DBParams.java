package up.mi.bdda.hcg.main;

import up.mi.bdda.hcg.api.DiskManager;

/**
 * Cette classe regroupe les paramètres de notre application.
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

		DiskManager disk = new DManager();
	}

}
