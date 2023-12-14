package up.mi.bdda.app.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import up.mi.bdda.app.DBParams;
import up.mi.bdda.app.buffer.BufferManager;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.resource.RecordId;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.disk.DiskManager;
import up.mi.bdda.app.page.DataPage;
import up.mi.bdda.app.page.HeaderPage;
import up.mi.bdda.app.page.PageId;

public final class FileManager implements API {
  // private constructor to prevent instantiation
  private FileManager() {
  }

  // get a buffer for a page
  private ByteBuffer getBufferForPage(PageId pageId) throws IOException {
    return BufferManager.getSingleton().getPage(pageId);
  }

  // allocate a new page
  private PageId allocateNewPage() throws IOException {
    return DiskManager.getSingleton().allocPage();
  }

  // unpin a page
  private void unpinPage(PageId pageId, boolean isDirty) throws IOException {
    BufferManager.getSingleton().freePage(pageId, isDirty);
  }

  // get the header page identifier of a tableInfo object
  private PageId[] getHeaderPageDetails(PageId pageId) throws IOException {
    HeaderPage headerPage = new HeaderPage(getBufferForPage(pageId));
    PageId fullPageId = headerPage.getFullPageId();
    PageId freePageId = headerPage.getFreePageId();
    unpinPage(pageId, false);
    return new PageId[] { freePageId, fullPageId };
  }

  // check if a page is a header page
  private boolean isHeaderPage(PageId pageId) throws IOException {
    PageId[] headerPageDetails = getHeaderPageDetails(pageId);
    PageId pageIdZero = new PageId(0, 0);
    boolean isFreePageIdNotZero = !headerPageDetails[0].equals(pageIdZero);
    boolean isFullPageInFileIdValid = headerPageDetails[1].getFileIdx() < DBParams.DMFFileCount;
    return isFreePageIdNotZero && isFullPageInFileIdValid;
  }

  // check if a page is free
  private boolean isFreePage(PageId pageId) throws IOException {
    PageId[] headerPageDetails = getHeaderPageDetails(pageId);
    PageId pageIdZero = new PageId(0, 0);
    return headerPageDetails[0].equals(pageIdZero) && headerPageDetails[1].equals(pageIdZero);
  }

  // get the header page identifier of a tableInfo object
  private PageId getHeaderPageId(PageId headerPageId) throws IOException {
    if (isFreePage(headerPageId)) {
      HeaderPage headerPage = new HeaderPage(getBufferForPage(headerPageId));
      headerPage.setFreePageId(new PageId(-1, -1));
      headerPage.setFullPageId(new PageId(-1, -1));
      unpinPage(headerPageId, true);
      return headerPageId;
    }
    if (isHeaderPage(headerPageId)) {
      return headerPageId;
    }
    return getHeaderPageId(allocateNewPage());
  }

  // create a header page
  public PageId createHeaderPage() throws IOException {
    PageId headerPageId = getHeaderPageId(allocateNewPage());
    // return the page identifier of the header page
    return headerPageId;
  }

  // get a free data page identifier
  private PageId getFreeDataPageId(TableInfo resource, int sizeRecord) throws IOException {
    HeaderPage headerPage = new HeaderPage(getBufferForPage(resource.getHeaderPageId()));
    PageId freePageId = null;
    Iterator<PageId> freePageIdIterator = headerPage.iterator();
    while (freePageIdIterator.hasNext()) {
      PageId freePageIdCandidate = freePageIdIterator.next();
      ByteBuffer freePageBuf = getBufferForPage(freePageIdCandidate);
      DataPage freePage = new DataPage(freePageBuf, resource);
      freePage.read();
      unpinPage(freePageIdCandidate, false);
      if (freePage.hasSpaceLeft(sizeRecord)) {
        freePageId = freePageIdCandidate;
        break;
      }
    }
    unpinPage(resource.getHeaderPageId(), false);
    return freePageId;
  }

  // get a free page identifier
  private PageId getFreePageId(PageId dataPageId) throws IOException {
    if (isFreePage(dataPageId)) {
      return dataPageId;
    }
    return getFreePageId(allocateNewPage());
  }

  // get the data page identifiers of a tableInfo object
  private Collection<PageId> getDataPage(TableInfo resource) throws IOException {
    Collection<PageId> dataPageIds = new ArrayList<>();
    Iterator<PageId> freePageIdIterator = new HeaderPage(getBufferForPage(resource.getHeaderPageId())).iterator();
    while (freePageIdIterator.hasNext()) {
      dataPageIds.add(freePageIdIterator.next());
    }
    unpinPage(resource.getHeaderPageId(), false);
    return dataPageIds;
  }

  // chain the page in the list of pages "where there's space left"
  private void heapFile(PageId dataPageId, TableInfo resource) throws IOException {
    Collection<PageId> dataPageIds = getDataPage(resource);
    // get the last page identifier in the list
    PageId freePageId = dataPageIds.stream().reduce((first, second) -> second).orElse(resource.getHeaderPageId());
    HeaderPage freePage = new HeaderPage(getBufferForPage(freePageId));
    freePage.setFreePageId(dataPageId);
    unpinPage(freePageId, true);
  }

  // add a data page to a tableInfo object
  private PageId addDataPage(TableInfo resource) throws IOException {
    PageId dataPageId = getFreePageId(allocateNewPage());
    heapFile(dataPageId, resource);
    DataPage dataPage = new DataPage(getBufferForPage(dataPageId), resource);
    dataPage.write();
    unpinPage(dataPageId, true);
    return dataPageId;
  }

  // write a record to a data page
  private RecordId writeRecordToDataPage(Record record, PageId dataPageId) throws IOException {
    DataPage dataPage = new DataPage(getBufferForPage(dataPageId), record.getResource());
    dataPage.read();
    RecordId recordId = dataPage.writeRecord(record, dataPageId);
    unpinPage(dataPageId, true);
    return recordId;
  }

  // insert a record into a tableInfo object
  @Override
  public RecordId insertRecordIntoTable(Record record) throws IOException {
    TableInfo resource = record.getResource();
    PageId dataPageId = getFreeDataPageId(resource, record.size());
    if (dataPageId == null) {
      dataPageId = addDataPage(resource);
    }
    RecordId recordId = writeRecordToDataPage(record, dataPageId);
    return recordId;
  }

  // get a record from a data page
  private Collection<Record> getRecordsInDataPage(TableInfo resource, PageId dataPageId) throws IOException {
    DataPage dataPage = new DataPage(getBufferForPage(dataPageId), resource);
    dataPage.read();
    Collection<Record> records = new ArrayList<>();
    Iterator<Record> recordIterator = dataPage.iterator();
    while (recordIterator.hasNext()) {
      Record record = recordIterator.next();
      records.add(record);
    }
    unpinPage(dataPageId, false);
    return records;
  }

  // get all the records from a tableInfo object
  @Override
  public Collection<Record> getAllRecords(TableInfo resource) throws IOException {
    Collection<Record> records = new ArrayList<>();
    Collection<PageId> dataPageIds = getDataPage(resource);
    for (PageId dataPageId : dataPageIds) {
      Collection<Record> dataPageRecords = getRecordsInDataPage(resource, dataPageId);
      records.addAll(dataPageRecords);
    }
    return records;
  }

  /**
   * This method returns the unique instance of {@code FileManager}. If it doesn't
   * exist, it creates it.
   * 
   * @return the unique instance of {@code FileManager}
   */
  public static FileManager getSingleton() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    /**
     * The unique instance of {@code FileManager}.
     */
    private static final FileManager INSTANCE = new FileManager();
  }
}
