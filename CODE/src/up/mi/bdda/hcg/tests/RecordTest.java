package up.mi.bdda.hcg.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.util.List;

import org.junit.jupiter.api.Test;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.database.ColInfo;
import up.mi.bdda.hcg.main.database.Record;
import up.mi.bdda.hcg.main.database.TableInfo;
import up.mi.bdda.hcg.main.database.Type;
import up.mi.bdda.hcg.main.page.PageId;

public class RecordTest {
  @Test
  public void testWriteToBuffer() {
    DBParams.DBPath = "DB";
    DBParams.SGBDPageSize = 4096;
    DBParams.DMFFileCount = 4;
    DBParams.frameCount = 2;

    TableInfo user = new TableInfo("Etudiant", List.of(
        new ColInfo("AGE", Type.INT),
        new ColInfo("TAILLE", Type.FLOAT),
        new ColInfo("ADRESSE", Type.VARSTRING.size(50))));

    PageId pId0 = DiskManager.getSingleton().allocPage();
    ByteBuffer buffW = BufferManager.getSingleton().getPage(pId0);

    Record record = new Record(user);

    // ECRITURE D'UNE RECORD
    record.addRecValues(
        new Object[] {
            20,
            1.80f,
            "1 rue de la paix"
        });

    int size = record.writeToBuffer(buffW, 0);
    System.out.println("# RECORD WRITE SIZE : " + size);

    // Sauvegarde de la page
    BufferManager.getSingleton().freePage(pId0, true);

    // LECTURE D'UNE RECORD
    int size2 = record.readFromBuffer(buffW, 0);

    // print the record
    System.out.println(record);
    System.out.println("# RECORD READ SIZE : " + size2);

    BufferManager.getSingleton().freePage(pId0, false);

    BufferManager.getSingleton().flushAll();
  }
}
