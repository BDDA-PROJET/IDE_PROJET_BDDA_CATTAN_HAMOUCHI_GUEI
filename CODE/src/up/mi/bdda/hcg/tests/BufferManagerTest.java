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
    DBParams.DBPath = "DB";
    DBParams.SGBDPageSize = 4096;
    DBParams.DMFFileCount = 4;
    DBParams.frameCount = 2;

    DiskManager disk = DiskManager.getSingleton();
    BufferManager manager = BufferManager.getSingleton();

    PageId pId1 = disk.allocPage();
    PageId pId2 = disk.allocPage();

    ByteBuffer writer = manager.getPage(pId1);
    int inputData = 100;
    writer.putInt(inputData);
    disk.writePage(pId1, writer);

    ByteBuffer reader = manager.getPage(pId1);
    int outputData = reader.getInt();

    assertEquals(inputData, outputData);

    manager.freePage(pId1, false);

    manager.getPage(pId1);
    manager.getPage(pId2);

    manager.flushAll();
  }
}
