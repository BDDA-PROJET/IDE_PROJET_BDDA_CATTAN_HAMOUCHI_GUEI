package up.mi.bdda.app.disk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import up.mi.bdda.app.page.PageId;

/**
 * DiskManager is a singleton class that manages the allocation and deallocation
 * of pages on disk.
 * It keeps track of active and reusable pages, and provides methods to read and
 * write page data.
 */
public final class DiskManager {
  /**
   * A set of PageIds that are currently active.
   */
  private final Set<PageId> activePageIds;

  /**
   * A queue of PageIds that have been deallocated and can be reused.
   */
  private final Deque<PageId> reusablePageIds;

  /**
   * The PageId that is currently being worked on.
   */
  private final PageId workingPageId;

  /**
   * Private constructor for the singleton DiskManager class.
   */
  private DiskManager() {
    activePageIds = new HashSet<>();
    reusablePageIds = new ArrayDeque<>();
    workingPageId = new PageId(0, 0);
  }

  /**
   * Initializes the DiskManager by clearing all active and reusable pages.
   */
  public void initialize() throws IOException {
    clear();
  }

  /**
   * Terminates the DiskManager by clearing all active and reusable pages.
   */
  public void terminate() throws IOException {
    clear();
  }

  /**
   * Clears all active and reusable pages and resets the working page index.
   */
  public void clear() throws IOException {
    workingPageId.setIndexes(0, 0);
    activePageIds.clear();
    reusablePageIds.clear();
  }

  /**
   * Allocates a new page on disk. If there are reusable pages, one of them is
   * reused.
   * Otherwise, a new page is created.
   *
   * @return The PageId of the allocated page.
   */
  public PageId allocatePage() throws IOException {
    PageId pageId;

    if (reusablePageIds.isEmpty()) {
      pageId = workingPageId.clone();
      try {
        pageId.createFile();
      } catch (IOException e) {
        throw new IOException("Failed to allocate page", e);
      }
      workingPageId.nextIndex();
    } else {
      pageId = reusablePageIds.poll();
    }

    if (!activePageIds.contains(pageId)) {
      activePageIds.add(pageId);
    }
    return pageId;
  }

  /**
   * Deallocates a page, making it reusable.
   *
   * @param pageId The PageId of the page to deallocate.
   */
  public void deallocatePage(PageId pageId) {
    if (pageId == null) {
      throw new IllegalArgumentException("PageId cannot be null");
    }

    if (activePageIds.removeIf(p -> p.equals(pageId))) {
      reusablePageIds.add(pageId);
    }

    if (activePageIds.isEmpty()) {
      workingPageId.setIndexes(0, 0);
    }
  }

  /**
   * Returns the number of currently active pages.
   *
   * @return The number of active pages.
   */
  public int countActivePages() {
    return activePageIds.size();
  }

  /**
   * Loads the data of a page into a ByteBuffer.
   *
   * @param pageId The PageId of the page to load.
   * @param buffer The ByteBuffer to load the data into.
   */
  public void loadPageData(PageId pageId, ByteBuffer buffer) throws IOException {
    if (pageId == null) {
      throw new IllegalArgumentException("PageId cannot be null");
    }

    if (buffer == null) {
      throw new IllegalArgumentException("ByteBuffer cannot be null");
    }

    try (RandomAccessFile file = pageId.getAccessFile()) {
      file.read(buffer.array());
    } catch (IOException e) {
      throw new RuntimeException("Error reading page", e);
    }
  }

  /**
   * Saves the data of a ByteBuffer to a page.
   *
   * @param pageId The PageId of the page to save to.
   * @param buffer The ByteBuffer containing the data to save.
   */
  public void savePageData(PageId pageId, ByteBuffer buffer) throws IOException {
    if (pageId == null) {
      throw new IllegalArgumentException("PageId cannot be null");
    }

    if (buffer == null) {
      throw new IllegalArgumentException("ByteBuffer cannot be null");
    }

    try (RandomAccessFile file = pageId.getAccessFile()) {
      file.write(buffer.array());
    } catch (IOException e) {
      throw new IOException("Error writing page", e);
    }
  }

  /**
   * Returns the singleton instance of the DiskManager.
   *
   * @return The singleton DiskManager instance.
   */
  public static DiskManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  /**
   * Private static class that holds the singleton DiskManager instance.
   */
  private static class SingletonHolder {
    private static final DiskManager INSTANCE = new DiskManager();
  }
}
