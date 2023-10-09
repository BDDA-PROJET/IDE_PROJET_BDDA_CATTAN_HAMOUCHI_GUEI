package up.mi.bdda.hcg.main;

import java.nio.ByteBuffer;
import java.util.Arrays;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.api.DatabaseInfo;
import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.database.ColInfo;
import up.mi.bdda.hcg.main.database.Record;
import up.mi.bdda.hcg.main.database.TableInfo;
import up.mi.bdda.hcg.main.database.Type;
import up.mi.bdda.hcg.main.disk.PageId;

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

	/** Le nombre maximal de frames disponible pour le {@code BufferManager} */
	public static int frameCount;

	public static void main(String[] args) {

		DBPath = "DB";
		SGBDPageSize = 4096;
		DMFFileCount = 4;
		frameCount = 2;

		TableInfo user = new TableInfo("User", 4,
				Arrays.asList(
						new ColInfo("id", Type.INT),
						new ColInfo("name", Type.STRING.size(6)),
						new ColInfo("height", Type.FLOAT),
						new ColInfo("address", Type.VARSTRING.size(20))));

		// PageId pId0 = DiskManager.getSingleton().allocPage();
		PageId pId = DiskManager.getSingleton().allocPage();
		// BufferManager.getSingleton().getPage(pId0);
		ByteBuffer buff = BufferManager.getSingleton().getPage(pId);
		// BufferManager.getSingleton().freePage(pId0, true);
		// BufferManager.getSingleton().freePage(pId, true);
		DatabaseInfo.getSingleton().addTableInfo(user);
		Record record = new Record(user);

		// ECRITURE D'UNE RECORD
		// record.addRecValues(new Object[] { 1, "Boby", 1.50f, "1 rue de paris" });
		// int size = record.writeToBuffer(buff, 0);
		// BufferManager.getSingleton().freePage(pId, true);

		// LECTURE D'UNE RECORD
		int size = record.readFromBuffer(buff, 0);

		// print the record
		System.out.println(record);

		// int totalSize = size + record.readFromBuffer(buff, size);
		// print the record
		// System.out.println(record);

		System.out.println("# RECORD SIZE : " + size);
	}
}
