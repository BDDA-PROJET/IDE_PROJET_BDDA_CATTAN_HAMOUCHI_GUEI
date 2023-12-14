package up.mi.bdda.app.page;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import up.mi.bdda.app.buffer.BufferManager;

public class HeaderPage implements Iterable<PageId> {
  private ByteBuffer buff;

  public HeaderPage(ByteBuffer buff) {
    this.buff = buff;
  }

  public void setFreePageId(PageId pageId) {
    buff.putInt(0, pageId.getFileIdx());
    buff.putInt(4, pageId.getPageIdx());
  }

  public void setFullPageId(PageId pageId) {
    buff.putInt(8, pageId.getFileIdx());
    buff.putInt(12, pageId.getPageIdx());
  }

  public PageId getFreePageId() {
    return new PageId(buff.getInt(0), buff.getInt(4));
  }

  public PageId getFullPageId() {
    return new PageId(buff.getInt(8), buff.getInt(12));
  }

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
            ByteBuffer nextFreePageBuf = BufferManager.getSingleton().getPage(pageId);
            freePageId = new PageId(nextFreePageBuf.getInt(0), nextFreePageBuf.getInt(4));
            BufferManager.getSingleton().freePage(pageId, false);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return pageId;
      }
    };
  }

}
