package up.mi.bdda.app.database.resource;

import up.mi.bdda.app.page.PageId;

/**
 * The RecordId class represents a unique identifier for a record in a database.
 * It consists of a PageId and a slot index.
 */
public class RecordId {
  /**
   * The PageId of the page that contains the record.
   */
  private PageId pageId;

  /**
   * The index of the slot within the page where the record is stored.
   */
  private int slotIdx;

  /**
   * Constructs a new RecordId with the given PageId and slot index.
   *
   * @param pageId  the PageId of the page that contains the record
   * @param slotIdx the index of the slot within the page where the record is
   *                stored
   * @throws IllegalArgumentException if the slot index is negative
   */
  public RecordId(PageId pageId, int slotIdx) {
    if (slotIdx < 0) {
      throw new IllegalArgumentException("Slot index must be positive");
    }
    this.pageId = pageId;
    this.slotIdx = slotIdx;
  }

  /**
   * Constructs a new RecordId with a default PageId and slot index of -1.
   */
  public RecordId() {
    this.pageId = new PageId(-1, -1);
    this.slotIdx = -1;
  }

  /**
   * Returns the PageId of the page that contains the record.
   *
   * @return the PageId of the page that contains the record
   */
  public PageId getPageId() {
    return pageId;
  }

  /**
   * Returns the index of the slot within the page where the record is stored.
   *
   * @return the index of the slot within the page where the record is stored
   */
  public int getSlotIdx() {
    return slotIdx;
  }

  /**
   * Sets the PageId of the page that contains the record.
   *
   * @param pageId the new PageId
   */
  public void setPageId(PageId pageId) {
    this.pageId.setIndexes(pageId);
  }

  /**
   * Sets the index of the slot within the page where the record is stored.
   *
   * @param slotIdx the new slot index
   */
  public void setSlotIdx(int slotIdx) {
    this.slotIdx = slotIdx;
  }

  /**
   * Returns a string representation of the RecordId.
   *
   * @return a string representation of the RecordId
   */
  @Override
  public String toString() {
    return "RecordId [pageId=" + pageId + ", slotIdx=" + slotIdx + "]";
  }
}