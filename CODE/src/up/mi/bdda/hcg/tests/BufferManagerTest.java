package up.mi.bdda.hcg.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.disk.PageId;

public class BufferManagerTest {
  @Test
  void testGetPage() {
    // initialisation des paramètre de la base de donnée
    DBParams.DBPath = "DB";
    DBParams.SGBDPageSize = 4096;
    DBParams.DMFFileCount = 4;
    DBParams.frameCount = 2;

    // récupération des gestionnaire de disque et de buffer
    DiskManager disk = DiskManager.getSingleton();
    BufferManager manager = BufferManager.getSingleton();

    // allocation de page auprès du gestionnaire de disque
    PageId pId1 = disk.allocPage();
    PageId pId2 = disk.allocPage();

    // récupération d'un buffer pour écrire dans une page à l'aide du gestionnaire
    // de buffer
    ByteBuffer writer = manager.getPage(pId1);
    int inputData = 100;
    writer.putInt(inputData);
    disk.writePage(pId1, writer);

    // récupération d'un buffer pour lire une page à l'aide du gestionnaire de
    // buffer
    ByteBuffer reader = manager.getPage(pId1);
    int outputData = reader.getInt();

    // TEST
    assertEquals(inputData, outputData);

    // libération d'une page, avec apport de modification
    manager.freePage(pId1, true);

    manager.getPage(pId1);
    manager.getPage(pId2);

    // libération de toutes les pages
    manager.flushAll();
  }

}
