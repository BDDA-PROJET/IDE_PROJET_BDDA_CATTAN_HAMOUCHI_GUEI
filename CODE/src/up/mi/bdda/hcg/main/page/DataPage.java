package up.mi.bdda.hcg.main.page;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.database.Record;

public class DataPage {
  private PageId pageId;
  private PageId nextPageId;
  private List<Record> records;
  private Slot slot;
  private int freeSpace;
  private ByteBuffer buff;

  public DataPage(PageId pageId) {
    this.pageId = pageId;
    nextPageId = new PageId(-1, -1);
    records = new ArrayList<>();
    slot = new Slot(2*4, 5); //nbCells choisi de manière arbitraire
    freeSpace = 8;
  }

  public DataPage(ByteBuffer buff) {
    this.buff = buff;
  }

  public void setNextPageId(PageId nextPageId) {
    this.nextPageId = nextPageId;
  }

   public PageId getNextPageId() {
    return nextPageId;
  }


  public void setPosDebutRecord(int i, int valeur) {
    int position = DBParams.SGBDPageSize - 4-4 - 2*4*(i+1);
    buff.position(position);
    buff.putInt(valeur);
  }

  public int getPosDebutRecord(int i) {
    int position = DBParams.SGBDPageSize - 4-4 - 2*4*(i+1);
    buff.position(position);
    return buff.getInt();

    
  }
  public PageId getPageId() {
      return pageId;
  }

  public Slot getSlot() {
      return slot;
  }

  public List<Record> getRecords() {
      return records;
  }

  public int getNbRecords(){
    int pos =  DBParams.SGBDPageSize-8;
    buff.position(pos);
    return buff.getInt()+1;
  }

  public int getTailleRecord(int i){
    int pos = DBParams.SGBDPageSize - 4-4 - 2*4*(i+1) +4;
    buff.position(pos);
    return buff.getInt();
    
  }

  public void setFreeSpace(int size) {
      this.freeSpace = getSlot().setOffsetFreeSpace(size);
  }
}
