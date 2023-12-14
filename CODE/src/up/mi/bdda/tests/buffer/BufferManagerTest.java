package up.mi.bdda.tests.buffer;

import up.mi.bdda.app.DBParams;

public class BufferManagerTest {

  public BufferManagerTest() {
    DBParams.DBPath = "DB";
    DBParams.SGBDPageSize = 4096;
    DBParams.DMFFileCount = 4;
    DBParams.frameCount = 2;
  }

}