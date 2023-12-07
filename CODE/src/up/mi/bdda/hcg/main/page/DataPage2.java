package up.mi.bdda.hcg.main.page;

import java.nio.ByteBuffer;

import up.mi.bdda.hcg.main.DBParams;

public class DataPage2 {
      ByteBuffer buff;
      int freeSpace;
    
      public DataPage2(ByteBuffer buff) {
        this.buff = buff;
        this.freeSpace = buff.capacity();
  }

  public void setOffsetDeb(int reste){
    buff.position(buff.capacity()-4);
    buff.putInt(reste);
  }


   public PageId getNextPageId() {
    buff.position(0);
    int f = buff.getInt();
    int p = buff.getInt();
    return new PageId(f, p);
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
      this.freeSpace -= size;
  }

  public int getFreeSpace(){
    return freeSpace;
  }

  
  public void setNextPageId(PageId nextPageId) {
    buff.position(0);
    buff.putInt(nextPageId.getFileIdx());
    buff.putInt(nextPageId.getPageIdx());
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
    