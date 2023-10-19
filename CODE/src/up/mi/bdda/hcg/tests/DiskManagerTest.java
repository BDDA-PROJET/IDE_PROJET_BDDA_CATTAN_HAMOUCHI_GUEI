package up.mi.bdda.hcg.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.disk.PageId;

public class DiskManagerTest {
  @Test
  void testAllocAndDeallocPage() {
    DBParams.DBPath = "DB";
    DBParams.SGBDPageSize = 4096;
    DBParams.DMFFileCount = 4;

    DiskManager disk = DiskManager.getSingleton();

    PageId pId1 = disk.allocPage();
    assertEquals("0.0", pId1.toString());

    PageId pId2 = disk.allocPage();
    assertEquals("1.0", pId2.toString());

    disk.deallocPage(pId1);
    assertEquals(1, disk.getCurrentCountAllocPages());
    disk.deallocPage(pId2);
    assertEquals(0, disk.getCurrentCountAllocPages());
  }

  @Test
  void testReadAndWritePage() {
    DBParams.DBPath = "DB";
    DBParams.SGBDPageSize = 4096;
    DBParams.DMFFileCount = 4;

    DiskManager disk = DiskManager.getSingleton();
    PageId pId = disk.allocPage();
    ByteBuffer buffReader = ByteBuffer.allocate(DBParams.SGBDPageSize);
    ByteBuffer buffWriter = ByteBuffer.allocate(DBParams.SGBDPageSize);

    // write data into buffWriter
    int inputData1 = 123456;
    int inputData2 = 2024;
    buffWriter.putInt(inputData1);
    buffWriter.putInt(inputData2);
    disk.writePage(pId, buffWriter);

    // read data from buffReader
    disk.readPage(pId, buffReader);
    int outputData1 = buffReader.getInt();
    int outputDat2 = buffReader.getInt();

    // testing input and output data
    assertEquals(inputData1, outputData1);
    assertEquals(inputData2, outputDat2);
  }
}