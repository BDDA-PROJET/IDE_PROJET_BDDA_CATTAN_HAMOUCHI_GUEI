package up.mi.bdda.hcg.main.database;

import up.mi.bdda.hcg.main.page.PageId;

public class RecordId {
  private PageId pageId;
  private int slotIdx;

  /**
   * 
   * @param pageId
   * @param slotIdx
   */
  public RecordId(PageId pageId, int slotIdx) {
    this.pageId = pageId;
    this.slotIdx = slotIdx;
  }
}
