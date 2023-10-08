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
    DBParams.DBPath = "DB";
		DBParams.SGBDPageSize = 4096;
		DBParams.DMFFileCount = 4;
    PageId p = new PageId(0,0);
    ByteBuffer b = new ByteBuffer.allocate(4);
    b.putInt(1);
    b.putInt(2);
    b.putInt(3);
    b.putChar('a');
    DiskManager disk = DManager.writePage(a,b);
    
    
    
  }
}
