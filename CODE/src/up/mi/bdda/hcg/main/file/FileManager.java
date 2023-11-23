package up.mi.bdda.hcg.main.file;

import java.nio.ByteBuffer;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.database.RecordId;
import up.mi.bdda.hcg.main.database.TableInfo;
import up.mi.bdda.hcg.main.page.DataPage;
import up.mi.bdda.hcg.main.page.HeaderPage;
import up.mi.bdda.hcg.main.page.PageId;
import up.mi.bdda.hcg.main.buffer.BManager;
import up.mi.bdda.hcg.main.database.Record;

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

  public PageId getFreeDataPageId(TableInfo ti, int sizeRecord){
     DataPage dp = new DataPage(ti.getHeaderPageId().getFreePageId());
      if(dp.getSlot().getOffsetFreeSpace() >= sizeRecord){
        PageId p = dp.getPageId();
        if(p.isValid()){return p;}
        }
    return null;
  
  }


  public RecordId writeRecordToDataPage(Record r, PageId p){
    DataPage dp = new DataPage(p);
    dp.getRecords().add(r);
    int size = r.writeToBuffer(BManager.getSingleton().getPage(p),dp.getSlot().getOffsetFreeSpace());
    dp.getSlot().getCellsDirectory().add(dp.getSlot().getOffsetFreeSpace());
    dp.getSlot().getCellsDirectory().add(size);
    r.writeToBuffer(BManager.getSingleton().getPage(p),dp.getSlot().getOffsetFreeSpace());
    dp.setFreeSpace(size);
    RecordId rid = new RecordId(p,dp.getSlot().getId());
    if(dp.getSlot().getOffsetFreeSpace() == 0){
      HeaderPage hp = new HeaderPage(BManager.getSingleton().getPage(p));
      hp.setFullPageId(p.clone());
    }
    BManager.getSingleton().freePage(p,true);
    return rid;
  }


}
