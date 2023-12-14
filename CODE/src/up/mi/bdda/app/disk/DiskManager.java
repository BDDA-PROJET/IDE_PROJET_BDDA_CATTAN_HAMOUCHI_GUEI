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
 * The {@code DiskManager} class is a singleton class that manages the
 * allocation and deallocation of pages on disk. It also provides methods to
 * read and write data to these pages.
 * <p>
 * Here's a brief description of the methods in this class:
 * <ul>
 * <li>{@link #DiskManager()} - This is the default constructor that initializes
 * the fields.
 * <li>{@link #allocPage()} - This method allocates a new page on disk. If there
 * are deallocated pages, it reuses one of them. Otherwise, it creates a new
 * page.
 * <li>{@link #deallocPage(PageId)} - This method deallocates a page on disk. If
 * the page is in the set of allocated pages, it is removed from there and added
 * to the deque of deallocated pages.
 * <li>{@link #getAllocatedPageCount()} - This method returns the number of
 * allocated pages.
 * <li>{@link #readPage(PageId, ByteBuffer)} - This method reads data from a
 * page into a {@link ByteBuffer}.
 * <li>{@link #writePage(PageId, ByteBuffer)} - This method writes data from a
 * {@link ByteBuffer} to a page.
 * <li>{@link #getSingleton()} - This method returns the single instance of
 * {@code DiskManager}. If it doesn't exist, it creates it.
 */
public final class DiskManager {
  /**
   * A set of {@link PageId} objects that have been allocated.
   */
  private final Set<PageId> allocatedPageIds;

  /**
   * A deque of {@link PageId} objects that have been deallocated and can be
   * reused.
   */
  private final Deque<PageId> deallocatedPageIds;

  /**
   * The {@link PageId} of the current page being worked on.
   */
  private final PageId currentPageId;

  /**
   * This is the default constructor that initializes the fields.
   */
  private DiskManager() {
    allocatedPageIds = new HashSet<>();
    deallocatedPageIds = new ArrayDeque<>();
    currentPageId = new PageId(0, 0);
  }

  public void init() throws IOException {
    reset();
  }

  public void finish() throws IOException {
    reset();
  }

  public void reset() throws IOException {
    currentPageId.set(0, 0);
    allocatedPageIds.clear();
    deallocatedPageIds.clear();
  }

  /**
   * This method allocates a new page on disk. If there are deallocated pages, it
   * reuses one of them. Otherwise, it creates a new page.
   * 
   * @return the {@link PageId} of the allocated page
   */
  public PageId allocPage() throws IOException {
    PageId pageId;

    if (deallocatedPageIds.isEmpty()) {
      pageId = currentPageId.clone();
      try {
        pageId.createFile();
      } catch (IOException e) {
        throw new IOException("Failed to allocate page", e);
      }
      currentPageId.next();
    } else {
      pageId = deallocatedPageIds.poll();
    }

    if (!allocatedPageIds.contains(pageId)) {
      allocatedPageIds.add(pageId);
    }
    return pageId;
  }

  /**
   * This method deallocates a page on disk. If the page is in the set of
   * allocated pages, it is removed from there and added to the deque of
   * deallocated pages.
   * 
   * @param pageId the {@link PageId} of the page to deallocate
   */
  public void deallocPage(PageId pageId) {
    if (pageId == null) {
      throw new IllegalArgumentException("PageId cannot be null");
    }

    if (allocatedPageIds.removeIf(p -> p.equals(pageId))) {
      deallocatedPageIds.add(pageId);
    }

    if (allocatedPageIds.isEmpty()) {
      currentPageId.set(0, 0);
    }
  }

  /**
   * This method returns the number of allocated pages.
   * 
   * @return the number of allocated pages
   */
  public int getAllocatedPageCount() {
    return allocatedPageIds.size();
  }

  /**
   * This method reads data from a page into a {@link ByteBuffer}.
   * 
   * @param pageId the {@link PageId} of the page to read
   * @param buff   the {@link ByteBuffer} to read into
   * @throws IOException if an I/O error occurs
   */
  public void readPage(PageId pageId, ByteBuffer buff) throws IOException {
    if (pageId == null) {
      throw new IllegalArgumentException("PageId cannot be null");
    }

    if (buff == null) {
      throw new IllegalArgumentException("ByteBuffer cannot be null");
    }

    try (RandomAccessFile file = pageId.getAccessFile()) {
      file.read(buff.array());
    } catch (IOException e) {
      throw new RuntimeException("Error reading page", e);
    }
  }

  /**
   * This method writes data from a {@link ByteBuffer} to a page.
   * 
   * @param pageId the {@link PageId} of the page to write
   * @param buff   the {@link ByteBuffer} to write from
   * @throws IOException if an I/O error occurs
   */
  public void writePage(PageId pageId, ByteBuffer buff) throws IOException {
    if (pageId == null) {
      throw new IllegalArgumentException("PageId cannot be null");
    }

    if (buff == null) {
      throw new IllegalArgumentException("ByteBuffer cannot be null");
    }

    try (RandomAccessFile file = pageId.getAccessFile()) {
      file.write(buff.array());
    } catch (IOException e) {
      throw new IOException("Error writing page", e);
    }
  }

  /**
   * This method returns the unique instance of {@code DiskManager}. If it doesn't
   * exist, it creates it.
   * 
   * @return the unique instance of {@code DiskManager}
   */
  public static DiskManager getSingleton() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    /**
     * The unique instance of {@code DiskManager}.
     */
    private static final DiskManager INSTANCE = new DiskManager();
  }

}
