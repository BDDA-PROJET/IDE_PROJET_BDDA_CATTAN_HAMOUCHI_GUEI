package up.mi.bdda.app.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;

import up.mi.bdda.app.page.PageId;
import up.mi.bdda.app.settings.DBParams;

/**
 * BufferManager is a class that manages the buffer memory of the application.
 * It uses a LinkedHashMap to store MemoryFrame objects, which represent pages
 * in memory.
 * The class uses the Singleton design pattern to ensure only one instance of
 * BufferManager exists.
 */
public final class BufferManager {
  /**
   * A LinkedHashMap that maps PageId objects to MemoryFrame objects.
   * The LinkedHashMap is used because it maintains the insertion order,
   * which is useful for implementing the LRU (Least Recently Used) page
   * replacement policy.
   */
  private final LinkedHashMap<PageId, MemoryFrame> memoryCache;

  /**
   * Private constructor to prevent instantiation of the class.
   * Initializes the memoryCache with a maximum size specified by
   * DBParams.maxFrameCount.
   */
  private BufferManager() {
    memoryCache = new LinkedHashMap<>(DBParams.maxFrameCount, 0.75f, true);
  }

  /**
   * Initializes the BufferManager by clearing the memoryCache.
   */
  public void initialize() {
    clearMemory();
  }

  /**
   * Completes the operations of the BufferManager by flushing all pages.
   * 
   * @throws IOException if an I/O error occurs.
   */
  public void complete() throws IOException {
    flushAllPages();
  }

  /**
   * Clears the memoryCache.
   */
  public void clearMemory() {
    memoryCache.clear();
  }

  /**
   * Returns the MemoryFrame associated with the given PageId.
   * 
   * @param pageId the PageId of the MemoryFrame to return.
   * @return the MemoryFrame associated with the given PageId, or null if no such
   *         MemoryFrame exists.
   */
  private MemoryFrame getMemoryFrame(PageId pageId) {
    return memoryCache.values().stream().parallel().filter(f -> f.getDataPageId().equals(pageId)).findAny()
        .orElse(null);
  }

  /**
   * Loads the page with the given PageId into memory.
   * If the memoryCache is full, it uses the LRU policy to replace a page.
   * 
   * @param pageId the PageId of the page to load.
   * @return the MemoryFrame of the loaded page.
   * @throws IOException if an I/O error occurs.
   */
  private MemoryFrame loadPage(PageId pageId) throws IOException {
    MemoryFrame memoryFrame = memoryCache.get(pageId);
    if (memoryFrame == null) {
      if (memoryCache.size() == DBParams.maxFrameCount) {
        Iterator<PageId> iterator = memoryCache.keySet().iterator();
        while (iterator.hasNext()) {
          PageId lruPageId = iterator.next();
          memoryFrame = getMemoryFrame(lruPageId);
          if (memoryFrame.getUsageCount() == 0) {
            break;
          }
        }
        memoryFrame.releaseDataBlock();
        memoryCache.remove(memoryFrame.getDataPageId());
      } else {
        memoryFrame = new MemoryFrame();
      }
      memoryFrame.loadDataPage(pageId);
      memoryCache.put(pageId, memoryFrame);
    }
    memoryFrame.increaseUsageCount();
    return memoryFrame;
  }

  /**
   * Returns the ByteBuffer of the page with the given PageId.
   * 
   * @param pageId the PageId of the page.
   * @return the ByteBuffer of the page.
   * @throws IOException if an I/O error occurs.
   */
  public ByteBuffer getPageBuffer(PageId pageId) throws IOException {
    MemoryFrame memoryFrame = loadPage(pageId);

    return memoryFrame.getDataBuffer();
  }

  /**
   * Releases the page with the given PageId.
   * If the page was modified, it is marked as such and its data block is
   * released.
   * 
   * @param pageId     the PageId of the page to release.
   * @param isModified whether the page was modified.
   * @throws IOException if an I/O error occurs.
   */
  public void releasePage(PageId pageId, boolean isModified) throws IOException {
    MemoryFrame memoryFrame = memoryCache.get(pageId);

    if (memoryFrame != null) {
      memoryFrame.decreaseUsageCount();
      if (isModified) {
        memoryFrame.markAsModified();
        memoryFrame.releaseDataBlock();
      }
    } else {
      throw new IllegalStateException("Cannot unpin a page that does not exist in buffer");
    }
  }

  /**
   * Flushes all pages in the memoryCache.
   * 
   * @throws IOException if an I/O error occurs.
   */
  public void flushAllPages() throws IOException {
    if (memoryCache.size() > 0) {
      for (MemoryFrame memoryFrame : memoryCache.values()) {
        memoryFrame.releaseDataBlock();
        memoryFrame.resetDataBlock();
      }
      memoryCache.clear();
    }
  }

  /**
   * Returns the single instance of BufferManager.
   * 
   * @return the single instance of BufferManager.
   */
  public static BufferManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  /**
   * SingletonHolder is a private static class that holds the single instance of
   * BufferManager.
   */
  private final class SingletonHolder {
    private static final BufferManager INSTANCE = new BufferManager();
  }
}
