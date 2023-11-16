package up.mi.bdda.hcg.main.file;

import java.nio.ByteBuffer;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.page.PageId;

public final class FileManager {
  private static FileManager singleton = new FileManager();

  private FileManager() {
  }

  public PageId createNewHeaderPage() {
    PageId allocedPageId = DiskManager.getSingleton().allocPage();
    ByteBuffer buff = BufferManager.getSingleton().getPage(allocedPageId);
    buff.putInt(0, -1);
    buff.putInt(4, -1);
    buff.putInt(8, -1);
    buff.putInt(12, -1);

    BufferManager.getSingleton().freePage(allocedPageId, true);

    return allocedPageId;
  }

  public static FileManager getSingleton() {
    return singleton;
  }
}
