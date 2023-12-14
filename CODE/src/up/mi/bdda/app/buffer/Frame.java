package up.mi.bdda.app.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import up.mi.bdda.app.DBParams;
import up.mi.bdda.app.disk.DiskManager;
import up.mi.bdda.app.page.PageId;

public class Frame {
  private ByteBuffer buff;
  private final PageId pageId;
  private AtomicInteger pinCount;
  private boolean flagDirty;

  public Frame() {
    buff = null;
    pageId = new PageId(-1, -1);
    pinCount = new AtomicInteger(0);
    flagDirty = false;
  }

  private void flushPage() throws IOException {
    if (flagDirty) {
      DiskManager.getSingleton().writePage(pageId, buff);
      flagDirty = false;
    }
  }

  public int incrementPinCount() {
    return pinCount.incrementAndGet();
  }

  public int decrementPinCount() {
    int currentCount = pinCount.decrementAndGet();
    if (currentCount < 0) {
      throw new IllegalStateException("Pin count cannot be negative");
    }
    return currentCount;
  }

  public void reset() {
    flagDirty = false;

    pinCount.set(0);
    pageId.reset();
    buff.clear();
  }

  public void free() throws IOException {
    if (pinCount.get() == 0) {
      flushPage();
    } else {
      throw new IllegalStateException("Cannot free a frame that is being used");
    }
  }

  public void loadPage(PageId pageId) throws IOException {
    buff = ByteBuffer.allocate(DBParams.SGBDPageSize);
    DiskManager.getSingleton().readPage(pageId, buff);
    this.pageId.set(pageId);
    flagDirty = false;
  }

  public void setDirty() {
    flagDirty = true;
  }

  public ByteBuffer getBuff() {
    return buff;
  }

  public PageId getPageId() {
    return pageId.clone();
  }

  public int getPinCount() {
    return pinCount.get();
  }

  public boolean isDirty() {
    return flagDirty;
  }
}
