package up.mi.bdda.hcg.main.buffer;

import java.nio.ByteBuffer;

import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.disk.PageId;

public class Frame {
  private ByteBuffer buff;
  private PageId pageId;
  private int pinCount;
  private boolean flagDirty;

  public Frame() {
    buff = ByteBuffer.allocate(DBParams.SGBDPageSize);
    pageId = new PageId();
    pinCount = 0;
    flagDirty = false;
  }

  public void incrementPinCount() {
    pinCount += 1;
  }

  public void decrementPinCount() {
    pinCount -= 1;
  }

  public void setFlagDirty(boolean flagDirty) {
    this.flagDirty = flagDirty;
  }

  public void reset() {
    buff.clear();
    pageId.reset();
    setFlagDirty(false);
    pinCount = 0;
  }

  public ByteBuffer getBuffer() {
    return buff;
  }

  public PageId getPageId() {
    return pageId;
  }

  public int getPinCount() {
    return pinCount;
  }

  public boolean isDirty() {
    return flagDirty;
  }
}
