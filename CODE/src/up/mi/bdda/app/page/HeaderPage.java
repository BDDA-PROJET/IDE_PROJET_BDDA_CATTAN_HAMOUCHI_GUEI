package up.mi.bdda.app.page;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import up.mi.bdda.app.buffer.BufferManager;

/**
 * The HeaderPage class represents the header of a page in a file.
 * It provides methods to set and get the identifiers of free and full pages.
 * It also implements Iterable to allow iteration over the free pages.
 */
public class HeaderPage implements Iterable<PageId> {

  /**
   * The buffer that holds the data of the header page.
   */
  private ByteBuffer buffer;

  /**
   * Constructs a HeaderPage object with the given buffer.
   * 
   * @param buffer the buffer that holds the data of the header page.
   */
  public HeaderPage(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  /**
   * Sets the identifier of the free page.
   * 
   * @param pageId the identifier of the free page.
   */
  public void setFreePageId(PageId pageId) {
    buffer.putInt(0, pageId.getFileIdx());
    buffer.putInt(4, pageId.getPageIdx());
  }

  /**
   * Sets the identifier of the full page.
   * 
   * @param pageId the identifier of the full page.
   */
  public void setFullPageId(PageId pageId) {
    buffer.putInt(8, pageId.getFileIdx());
    buffer.putInt(12, pageId.getPageIdx());
  }

  /**
   * Returns the identifier of the free page.
   * 
   * @return the identifier of the free page.
   */
  public PageId getFreePageId() {
    return new PageId(buffer.getInt(0), buffer.getInt(4));
  }

  /**
   * Returns the identifier of the full page.
   * 
   * @return the identifier of the full page.
   */
  public PageId getFullPageId() {
    return new PageId(buffer.getInt(8), buffer.getInt(12));
  }

  /**
   * Returns an iterator over the free pages.
   * 
   * @return an iterator over the free pages.
   * @throws NoSuchElementException if there are no more free pages.
   */
  @Override
  public Iterator<PageId> iterator() throws NoSuchElementException {
    // iterate over the free pages only
    return new Iterator<PageId>() {
      PageId freePageId = getFreePageId();

      @Override
      public boolean hasNext() {
        return freePageId.isValid();
      }

      @Override
      public PageId next() {
        if (!hasNext()) {
          throw new NoSuchElementException("No more elements to iterate over.");
        }
        PageId pageId = freePageId;
        // get buffer for next iteration
        if (freePageId.isValid()) {
          try {
            ByteBuffer nextFreePageBuffer = BufferManager.getInstance().getPageBuffer(pageId);
            freePageId = new PageId(nextFreePageBuffer.getInt(0), nextFreePageBuffer.getInt(4));
            BufferManager.getInstance().releasePage(pageId, false);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return pageId;
      }
    };
  }

}
