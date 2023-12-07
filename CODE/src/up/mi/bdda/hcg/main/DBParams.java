package up.mi.bdda.hcg.main;

import java.nio.ByteBuffer;
import java.util.List;

import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.database.ColInfo;
import up.mi.bdda.hcg.main.database.Record;
import up.mi.bdda.hcg.main.database.TableInfo;
import up.mi.bdda.hcg.main.database.Type;
import up.mi.bdda.hcg.main.file.FileManager;
import up.mi.bdda.hcg.main.page.PageId;

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

		ByteBuffer buff = ByteBuffer.allocate(600);

		TableInfo user = new TableInfo(buff, "Etudiant", List.of(
			new ColInfo("AGE", Type.INT),
			new ColInfo("TAILLE", Type.FLOAT),
			new ColInfo("ADRESSE", Type.VARSTRING.size(50))));
	
		PageId pId0 = DiskManager.getSingleton().allocPage();
	
		Record record = new Record(user);
		record.addRecValues(
        new Object[] {
            20,
            1.80f,
            "1 rue de la paix"
        });

		FileManager.getSingleton().writeRecordToDataPage(record, pId0);
		FileManager.getSingleton().createNewHeaderPage();
		FileManager.getSingleton().addDataPage(user);
		FileManager.getSingleton().getFreeDataPageId(user, record.size());
		//FileManager.getSingleton().getAllRecords(user);
		FileManager.getSingleton().getRecordsInDataPage(user, pId0);
		FileManager.getSingleton().getDataPages(user);
		FileManager.getSingleton().insertRecordIntoTable(record);

	}
}
