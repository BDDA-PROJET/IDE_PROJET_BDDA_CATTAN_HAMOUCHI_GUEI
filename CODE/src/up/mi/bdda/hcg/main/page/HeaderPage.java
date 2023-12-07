package up.mi.bdda.hcg.main.page;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class HeaderPage extends PageId{
  ByteBuffer buff;
  List<PageId> freePage;
  List<PageId> fullPage;

  public HeaderPage(ByteBuffer buff) {
    this.buff = buff;
    LinkedList<PageId> freePage = new LinkedList<>();
    LinkedList<PageId> fullPage = new LinkedList<>();
    freePage.add(new PageId(-1,-1));

  }

  public void setFreePageId(PageId pageId) {
    buff.putInt(0, pageId.getFileIdx());
    buff.putInt(4, pageId.getPageIdx());
  }

  public void setFullPageId(PageId pageId) {
    buff.putInt(8, pageId.getFileIdx());
    buff.putInt(12, pageId.getPageIdx());
  }

  public void addListFreePage(PageId p){
    freePage.add(p);
  }

  public void addListFullPage(PageId p){
    fullPage.add(p);
  }

  public void removeListFreePage(int i){
    freePage.remove(i);
  }
  public PageId getFreePageId() {
    return new PageId(buff.getInt(0), buff.getInt(4));
  }

  public PageId getFullPageId() {
    return new PageId(buff.getInt(8), buff.getInt(12));
  }
}
