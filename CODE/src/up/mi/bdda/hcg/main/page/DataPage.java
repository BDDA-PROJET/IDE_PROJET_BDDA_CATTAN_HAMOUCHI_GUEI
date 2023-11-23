package up.mi.bdda.hcg.main.page;

import java.util.ArrayList;
import java.util.List;

import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.database.Record;

public class DataPage {
  private PageId pageId;
  private PageId nextPageId;
  private List<Record> records;
  private Slot slot;
  private int freeSpace;

  public DataPage(PageId pageId) {
    this.pageId = pageId;
    nextPageId = new PageId(-1, -1);
    records = new ArrayList<>();
    slot = new Slot(2*4, 5); //nbCells choisi de manière arbitraire
    freeSpace = 8;
  }

  public PageId getPageId() {
      return pageId;
  }

  public Slot getSlot() {
      return slot;
  }

  public List<Record> getRecords() {
      return records;
  }

  public void setFreeSpace(int size) {
      this.freeSpace = getSlot().setOffsetFreeSpace(size);
  }
}
