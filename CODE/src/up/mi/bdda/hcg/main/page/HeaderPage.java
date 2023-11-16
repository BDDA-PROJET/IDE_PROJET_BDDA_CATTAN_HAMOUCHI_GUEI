package up.mi.bdda.hcg.main.page;

import java.nio.ByteBuffer;

public class HeaderPage {
  ByteBuffer buff;

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
}
