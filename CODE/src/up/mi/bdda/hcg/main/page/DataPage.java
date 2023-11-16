package up.mi.bdda.hcg.main.page;

import java.util.ArrayList;
import java.util.List;

import up.mi.bdda.hcg.main.database.Record;

public class DataPage {
  private PageId pageId;
  private PageId nextPageId;
  private List<Record> records;
  private Slot slot;

  public DataPage(PageId pageId) {
    this.pageId = pageId;
    nextPageId = new PageId(-1, -1);
    records = new ArrayList<>();
    slot = new Slot(0, 0);
  }
}
