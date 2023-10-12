package up.mi.bdda.hcg.main;

import java.nio.Buffer;
import java.nio.ByteBuffer;

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

		ByteBuffer buffEcriture = ByteBuffer.allocate(SGBDPageSize);		
		ByteBuffer buffLecture = ByteBuffer.allocate(SGBDPageSize);


		DiskManager disk = DManager.getSingleton();

		PageId p1 = disk.allocPage();
		  disk.allocPage();
		  disk.allocPage();
		  disk.allocPage();
		 PageId p2=  disk.allocPage();

		 	

		  



		// buffEcriture.putInt(12);
		// buffEcriture.putInt(6);
		// disk.writePage(p1, buffEcriture);

		disk.readPage(p2, buffLecture);
		int a = buffLecture.getInt();
		int b =buffLecture.getInt();

		System.out.println("val1 : "  +a  +"\nval2 : "+ b);






	// disk.readPage(p1, buff);
		// var p2 = disk.allocPage();

		// disk.deallocPage(p1);

		// var p3 = disk.allocPage();

		// var p4 = disk.allocPage();

		// var p5 = disk.allocPage();

		// disk.deallocPage(p4);

		// var p6 = disk.allocPage();

		// var p7 = disk.allocPage();

		// var p8 = disk.allocPage();
	}

}
