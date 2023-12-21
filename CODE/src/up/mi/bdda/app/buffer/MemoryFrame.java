package up.mi.bdda.app.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import up.mi.bdda.app.disk.DiskManager;
import up.mi.bdda.app.page.PageId;
import up.mi.bdda.app.settings.DBParams;

/**
 * The MemoryFrame class represents a frame in the buffer pool.
 * It contains a data buffer, a page ID, a usage count, and a dirty flag.
 */
public class MemoryFrame {
  /**
   * The data buffer that holds the data of the page.
   */
  private ByteBuffer dataBuffer;

  /**
   * The ID of the page that is currently loaded into the frame.
   */
  private final PageId dataPageId;

  /**
   * The usage count of the frame. It is incremented every time the frame is
   * pinned and decremented when it is unpinned.
   */
  private AtomicInteger usageCount;

  /**
   * The dirty flag of the frame. It is set to true when the page is modified
   * while in the frame.
   */
  private boolean isModified;

  /**
   * Constructs a new Frame object.
   */
  public MemoryFrame() {
    dataBuffer = null;
    dataPageId = new PageId(-1, -1);
    usageCount = new AtomicInteger(0);
    isModified = false;
  }

  /**
   * Writes the page currently loaded into the frame to disk if the frame is
   * dirty.
   *
   * @throws IOException if an I/O error occurs
   */
  private void writePageToDisk() throws IOException {
    if (isModified) {
      DiskManager.getInstance().savePageData(dataPageId, dataBuffer);
      isModified = false;
    }
  }

  /**
   * Increments the usage count of the frame.
   *
   * @return the new usage count
   */
  public int increaseUsageCount() {
    return usageCount.incrementAndGet();
  }

  /**
   * Decrements the usage count of the frame.
   *
   * @return the new usage count
   * @throws IllegalStateException if the usage count becomes negative
   */
  public int decreaseUsageCount() {
    int currentCount = usageCount.decrementAndGet();
    if (currentCount < 0) {
      throw new IllegalStateException("Usage count cannot be negative");
    }
    return currentCount;
  }

  /**
   * Resets the frame by clearing the data buffer, resetting the page ID, and
   * setting the usage count and dirty flag to their initial values.
   */
  public void resetDataBlock() {
    isModified = false;

    usageCount.set(0);
    dataPageId.resetIndexes();
    dataBuffer.clear();
  }

  /**
   * Releases the frame by writing the page to disk if the frame is not in use and
   * is dirty.
   *
   * @throws IOException           if an I/O error occurs
   * @throws IllegalStateException if the frame is still in use
   */
  public void releaseDataBlock() throws IOException {
    if (usageCount.get() == 0) {
      writePageToDisk();
    } else {
      throw new IllegalStateException("Cannot release a data block that is being used");
    }
  }

  /**
   * Loads a page into the frame.
   *
   * @param pageId the ID of the page to load
   * @throws IOException if an I/O error occurs
   */
  public void loadDataPage(PageId pageId) throws IOException {
    dataBuffer = ByteBuffer.allocate(DBParams.pageSize);
    DiskManager.getInstance().loadPageData(pageId, dataBuffer);
    this.dataPageId.setIndexes(pageId);
    isModified = false;
  }

  /**
   * Marks the frame as dirty.
   */
  public void markAsModified() {
    isModified = true;
  }

  /**
   * Returns the data buffer of the frame.
   *
   * @return the data buffer of the frame
   */
  public ByteBuffer getDataBuffer() {
    return dataBuffer;
  }

  /**
   * Returns a clone of the page ID of the frame.
   *
   * @return a clone of the page ID of the frame
   */
  public PageId getDataPageId() {
    return dataPageId.clone();
  }

  /**
   * Returns the usage count of the frame.
   *
   * @return the usage count of the frame
   */
  public int getUsageCount() {
    return usageCount.get();
  }

  /**
   * Returns whether the frame is dirty.
   *
   * @return true if the frame is dirty, false otherwise
   */
  public boolean isModified() {
    return isModified;
  }
}