package up.mi.bdda.hcg.tests;
import up.mi.bdda.hcg.main.DBParams;
import org.junit.jupiter.api.Test;

public class DiskManagerTest {
  @Test
  void testAllocPage() {
  }

  @Test
  void testDeallocPage() {

    DBParams.DBPath = "DB";
		DBParams.SGBDPageSize = 4096;
		DBParams.DMFFileCount = 4;

    DiskManager disk = DManager.getSingleton();  
  
  
  }

  @Test
  void testGetCurrentCountAllocPages() {
  }

  @Test
  void testReadPage() {
  }

  @Test
  void testWritePage() {

    PageId p = p.allocPage()
    ByteBuffer b = new ByteBuffer.allocate(SGBDPageSize);
    b.putInt(1);
    DiskManager disk = DManager.writePage(p, b);
  }
}
