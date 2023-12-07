package up.mi.bdda.hcg.main.file;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.platform.reporting.shadow.org.opentest4j.reporting.events.core.Data;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.database.RecordId;
import up.mi.bdda.hcg.main.database.TableInfo;
import up.mi.bdda.hcg.main.page.DataPage;
import up.mi.bdda.hcg.main.page.HeaderPage;
import up.mi.bdda.hcg.main.page.PageId;
import up.mi.bdda.hcg.main.buffer.BManager;
import up.mi.bdda.hcg.main.database.Record;

public final class FileManager {
  private static FileManager singleton = new FileManager();

  private FileManager() {
  }

  public PageId createNewHeaderPage() {
    PageId allocedPageId = DiskManager.getSingleton().allocPage();
    ByteBuffer buff = BufferManager.getSingleton().getPage(allocedPageId);
    buff.putInt(0, -1);
    buff.putInt(4, -1);
    buff.putInt(8, -1);
    buff.putInt(12, -1);

    BufferManager.getSingleton().freePage(allocedPageId, true);

    return allocedPageId;
  }

  public static FileManager getSingleton() {
    return singleton;
  }

  public PageId getFreeDataPageId(TableInfo ti, int sizeRecord){
    PageId p = ti.getHeaderPageId().getFreePageId();
    ByteBuffer buff = BManager.getSingleton().getPage(p);
    DataPage dp = new DataPage(buff);
    int sum = 4096 -(8+2*4+(dp.getNbRecords()*2)*4);
    for(int i = 0; i < dp.getNbRecords(); i++){
      sum -= dp.getTailleRecord(i);
    }
      if(sum >= sizeRecord){
          if(p.isValid()){return p;}
        }
    return null;
  
  }


  public RecordId writeRecordToDataPage(Record r, PageId p){
    //on récupère la dataPage correspondant à la page donnée en argument
    ByteBuffer buff = BManager.getSingleton().getPage(p);
    DataPage dataPage = new DataPage(buff) ;

    //on défini la position du dernier record ecrit dans la DataPage
    int posDernierRec = dataPage.getTailleRecord(dataPage.getNbRecords()-1) +1 ;

    //on écrit le record dans la DataPage a la bonne position
    int offset = r.writeToBuffer(buff , dataPage.getPosDebutRecord(dataPage.getNbRecords()-1+ posDernierRec ));
    dataPage.setOffsetDeb(offset);
    dataPage.setFreeSpace(offset);
    RecordId rid = new RecordId(p, dataPage.getNbRecords());
    return rid;
  }


  public PageId addDataPage(TableInfo tableInfo) {
    //on récupère la HeaderPage de la tableInfo
    HeaderPage hp = tableInfo.getHeaderPageId();
    PageId datPageId = DiskManager.getSingleton().allocPage();
    ByteBuffer buff = BManager.getSingleton().getPage(datPageId);
    //on alloue une nouvelle Page

    //on créer une DataPage avec la page nouvellement allouée.
    DataPage dataPage = new DataPage(buff);

    //on note cette page dans la liste des FreePageId de la HeaderPage
    if(hp.getFreePageId().isValid()){
      dataPage.setNextPageId(new PageId(-1,-1));
      hp.setFreePageId(datPageId);
      hp.addListFreePage(datPageId);
    }
    hp.addListFreePage(datPageId);
    dataPage.setNextPageId(hp.getFreePageId().clone());
    hp.setFreePageId(datPageId);

    BufferManager.getSingleton().freePage(tableInfo.getHeaderPageId(), true);
    BufferManager.getSingleton().freePage(datPageId, true);

    return datPageId;

  }

  public List<Record> getRecordsInDataPage(TableInfo tabInfo, PageId pageId) {
    //on récupère la dataPage correspondant à la page donnée en argument
    ByteBuffer buff = BufferManager.getSingleton().getPage(pageId);
    DataPage dataPage = new DataPage(buff);

    //on créer une liste de record pour les récupérer
    ArrayList<Record> records = new ArrayList<>();
    int offset = 8;
      for (int i = 0; i< dataPage.getNbRecords();i++){
      Record unRecord = new Record(tabInfo);
      offset = unRecord.readFromBuffer(buff, offset);

      records.add(unRecord);
      }
    BufferManager.getSingleton().freePage(pageId, false);
    return records;
  }

  public List<PageId> getDataPages(TableInfo tabInfo) {
    // creer une liste de pages
    ArrayList<PageId> pages = new ArrayList<>();
    //creer un objet headerpage de la table info 
    HeaderPage headerPage = tabInfo.getHeaderPageId();

    // si hp a une freepage valide
    if( headerPage.getFreePageId().isValid()){
      // on l'ajoute a la liste pages
      pages.add(headerPage.getFreePageId());
       
      // recuper la datapage ecris dans la freepage
      DataPage dataPage = new DataPage(headerPage.getFreePageId());

      // on l'ajoute a la liste pages si valide
      if (dataPage.getNextPageId().isValid()) {
        pages.add(dataPage.getNextPageId());
      }   
      while (dataPage.getNextPageId().isValid()) {
       dataPage= new DataPage(dataPage.getNextPageId());
        if (dataPage.getNextPageId().isValid()) {
          pages.add(dataPage.getNextPageId());
        }
      }
    }
    if( headerPage.getFullPageId().isValid()){
      // on l'ajoute a la liste pages
      pages.add(headerPage.getFullPageId());
       
      // recuper la datapage ecris dans la freepage
      DataPage dataPage = new DataPage(headerPage.getFullPageId());

      // on l'ajoute a la liste pages si valide
      if (dataPage.getNextPageId().isValid()) {
        pages.add(dataPage.getNextPageId());
      }   
      while (dataPage.getNextPageId().isValid()) {
       dataPage= new DataPage(dataPage.getNextPageId());
        if (dataPage.getNextPageId().isValid()) {
          pages.add(dataPage.getNextPageId());
        }
      }
    }
    return pages;  
  }

  public RecordId insertRecordIntoTable ( Record record ) {

    PageId pageId = getFreeDataPageId(record.getTabInfo(), record.size());
    ByteBuffer buff = BManager.getSingleton().getPage(pageId);
    DataPage dataPage = new DataPage(buff);
    

    int posDernierRec = dataPage.getTailleRecord(dataPage.getNbRecords()-1) +1 ;

    record.writeToBuffer(buff , dataPage.getPosDebutRecord(dataPage.getNbRecords()-1+ posDernierRec ));

    RecordId rid = new RecordId(pageId, dataPage.getNbRecords());

    return rid;

    }

    public List<Record> getAllRecords (TableInfo tabInfo) {
      // une liste vide pour les reccord
      ArrayList<Record> records = new ArrayList<>();
// recuperer le headerpage de la table info et la freepageId disponible a lire 
     PageId pageId=  tabInfo.getHeaderPageId().getFreePageId();
     //acces au buff  de la pageID 
     ByteBuffer buff = BManager.getSingleton().getPage(pageId);

     // datapage de la pageId pour lire le contenu
     DataPage dataPage = new DataPage(buff);

     // nombre de record dans la datapage
    int nbRecords = dataPage.getNbRecords()-1;

    // boucle pour lire tous les record
    for(int i = 0; i< nbRecords;i++){
      // creation de al record ou stocker ce qu'on lit
      Record unRecord = new Record(tabInfo);

      // recupere la position de debut du record i 
     int posDebRecord = dataPage.getPosDebutRecord(i);

     // recupere la taille du record i 
     int tailleRecord = dataPage.getTailleRecord(i);
     // on se position au debut de la record pour lire 
     buff.position(posDebRecord);
    //!\ on  ne sais pas ou s'arreter si on lit un record 
     unRecord.readFromBuffer(buff, posDebRecord);
     records.add(unRecord);
      
      }

      return records;

      
    }


  
}
