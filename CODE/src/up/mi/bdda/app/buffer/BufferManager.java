package up.mi.bdda.app.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;

import up.mi.bdda.app.DBParams;
import up.mi.bdda.app.page.PageId;

public final class BufferManager {
  private final LinkedHashMap<PageId, Frame> cache;

  private BufferManager() {
    cache = new LinkedHashMap<>(DBParams.frameCount, 0.75f, true);
  }

  public void init() {
    reset();
  }

  public void finish() throws IOException {
    flushAll();
  }

  public void reset() {
    cache.clear();
  }

  private Frame peekFrame(PageId pageId) {
    return cache.values().stream().parallel().filter(f -> f.getPageId().equals(pageId)).findAny().orElse(null);
  }

  private Frame requestPage(PageId pageId) throws IOException {
    Frame frame = cache.get(pageId);
    if (frame == null) {
      // Page not in memory, need to load it into a frame
      if (cache.size() == DBParams.frameCount) {
        // No available frames, need to evict the least recently used one
        Iterator<PageId> iterator = cache.keySet().iterator();
        while (iterator.hasNext()) {
          PageId lruPageId = iterator.next();
          frame = peekFrame(lruPageId);
          if (frame.getPinCount() == 0) {
            break;
          }
        }
        // Write the frame to disk if it's dirty
        frame.free();

        // Remove the page from the cache
        cache.remove(frame.getPageId());
      } else {
        // There's an available frame, use it
        frame = new Frame();
      }
      // Load the page into the frame
      frame.loadPage(pageId);
      // Add the page to the cache
      cache.put(pageId, frame);
    }
    // Increment the pin count
    frame.incrementPinCount();
    return frame;
  }

  public ByteBuffer getPage(PageId pageId) throws IOException {
    Frame frame = requestPage(pageId);

    return frame.getBuff();
  }

  public synchronized void freePage(PageId pageId, boolean dirty) throws IOException {
    // Get the frame containing the page
    // Frame frame = peekFrame(pageId);
    Frame frame = cache.get(pageId);

    if (frame != null) {
      // The page is in the cache
      // Decrement the pin count
      frame.decrementPinCount();
      if (dirty) {
        // Mark the frame as dirty
        frame.setDirty();
        // Write the frame to disk if it's dirty
        frame.free();
      }
    } else {
      throw new IllegalStateException("Cannot unpin a page that does not exist in buffer");
    }
  }

  public void flushAll() throws IOException {
    if (cache.size() > 0) {
      for (Frame frame : cache.values()) {
        // Flush the frame to disk
        frame.free();
        frame.reset();
      }
      // Clear the cache
      cache.clear();
    }
  }

  public static BufferManager getSingleton() {
    return Holder.INSTANCE;
  }

  private final class Holder {
    private static final BufferManager INSTANCE = new BufferManager();
  }
}
