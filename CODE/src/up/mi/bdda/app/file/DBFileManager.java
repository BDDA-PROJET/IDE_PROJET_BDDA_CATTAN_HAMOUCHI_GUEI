package up.mi.bdda.app.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import up.mi.bdda.app.buffer.BufferManager;
import up.mi.bdda.app.database.api.DatabaseAPI;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.resource.RecordId;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.disk.DiskManager;
import up.mi.bdda.app.page.DataPage;
import up.mi.bdda.app.page.HeaderPage;
import up.mi.bdda.app.page.PageId;
import up.mi.bdda.app.settings.DBParams;

/**
 * DBFileManager is a class that manages the file operations for a database.
 * It implements the DatabaseAPI interface and provides methods for adding,
 * removing and retrieving records from a table.
 * It also manages the allocation and release of pages in the database.
 * This class follows the Singleton design pattern.
 */
public final class DBFileManager implements DatabaseAPI {

  /**
   * Private constructor to prevent instantiation.
   */
  private DBFileManager() {
  }

  /**
   * Fetches the buffer for a given page.
   * 
   * @param pageId The ID of the page to fetch the buffer for.
   * @return The buffer for the given page.
   * @throws IOException If an I/O error occurs.
   */
  private ByteBuffer fetchBufferForPage(PageId pageId) throws IOException {
    return BufferManager.getInstance().getPageBuffer(pageId);
  }

  /**
   * Generates a new page in the database.
   * 
   * @return The ID of the newly generated page.
   * @throws IOException If an I/O error occurs.
   */
  private PageId generateNewPage() throws IOException {
    return DiskManager.getInstance().allocatePage();
  }

  /**
   * Releases a page in the database.
   * 
   * @param pageId     The ID of the page to release.
   * @param isModified Whether the page has been modified.
   * @throws IOException If an I/O error occurs.
   */
  private void releasePage(PageId pageId, boolean isModified) throws IOException {
    BufferManager.getInstance().releasePage(pageId, isModified);
  }

  /**
   * Fetches the details of a header page.
   * 
   * @param pageId The ID of the page to fetch the details for.
   * @return An array of PageId objects containing the free page ID and the full
   *         page ID.
   * @throws IOException If an I/O error occurs.
   */
  private PageId[] fetchHeaderPageDetails(PageId pageId) throws IOException {
    HeaderPage headerPage = new HeaderPage(fetchBufferForPage(pageId));
    PageId fullPageId = headerPage.getFullPageId();
    PageId freePageId = headerPage.getFreePageId();
    releasePage(pageId, false);
    return new PageId[] { freePageId, fullPageId };
  }

  /**
   * Checks if a page is a header page.
   * 
   * @param pageId The ID of the page to check.
   * @return true if the page is a header page, false otherwise.
   * @throws IOException If an I/O error occurs.
   */
  private boolean checkIfHeaderPage(PageId pageId) throws IOException {
    PageId[] headerPageDetails = fetchHeaderPageDetails(pageId);
    PageId pageIdZero = new PageId(0, 0);
    boolean isFreePageIdNotZero = !headerPageDetails[0].equals(pageIdZero);
    boolean isFullPageInFileIdValid = headerPageDetails[1].getFileIdx() < DBParams.maxFileCount;
    return isFreePageIdNotZero && isFullPageInFileIdValid;
  }

  /**
   * Checks if a page is a free page.
   * 
   * @param pageId The ID of the page to check.
   * @return true if the page is a free page, false otherwise.
   * @throws IOException If an I/O error occurs.
   */
  private boolean checkIfFreePage(PageId pageId) throws IOException {
    PageId[] headerPageDetails = fetchHeaderPageDetails(pageId);
    PageId pageIdZero = new PageId(0, 0);
    return headerPageDetails[0].equals(pageIdZero) && headerPageDetails[1].equals(pageIdZero);
  }

  /**
   * Fetches the ID of a header page.
   * 
   * @param headerPageId The ID of the header page to fetch.
   * @return The ID of the header page.
   * @throws IOException If an I/O error occurs.
   */
  private PageId fetchHeaderPageId(PageId headerPageId) throws IOException {
    if (checkIfFreePage(headerPageId)) {
      HeaderPage headerPage = new HeaderPage(fetchBufferForPage(headerPageId));
      headerPage.setFreePageId(new PageId(-1, -1));
      headerPage.setFullPageId(new PageId(-1, -1));
      releasePage(headerPageId, true);
      return headerPageId;
    }
    if (checkIfHeaderPage(headerPageId)) {
      return headerPageId;
    }
    return fetchHeaderPageId(generateNewPage());
  }

  /**
   * Fetches the ID of a free data page.
   * 
   * @param resource   The table information resource.
   * @param sizeRecord The size of the record.
   * @return The ID of the free data page.
   * @throws IOException If an I/O error occurs.
   */
  private PageId fetchFreeDataPageId(TableInfo resource, int sizeRecord) throws IOException {
    HeaderPage headerPage = new HeaderPage(fetchBufferForPage(resource.getHeaderPageId()));
    PageId freePageId = null;
    Iterator<PageId> freePageIdIterator = headerPage.iterator();
    while (freePageIdIterator.hasNext()) {
      PageId freePageIdCandidate = freePageIdIterator.next();
      DataPage releasePage = new DataPage(fetchBufferForPage(freePageIdCandidate), resource);
      releasePage.load();

      if (releasePage.checkSpaceAvailability(sizeRecord)) {
        freePageId = freePageIdCandidate;
        releasePage(freePageIdCandidate, false);
        break;
      } else if (freePageIdIterator.hasNext()) {
        releasePage(freePageIdCandidate, false);
      } else {
        int leftOverSpace = releasePage.optimize();
        if (leftOverSpace >= sizeRecord) {
          freePageId = freePageIdCandidate;
        }
        releasePage(freePageIdCandidate, true);
      }
    }
    releasePage(resource.getHeaderPageId(), false);
    return freePageId;
  }

  /**
   * Fetches the ID of a free page.
   * 
   * @param dataPageId The ID of the data page to fetch.
   * @return The ID of the free page.
   * @throws IOException If an I/O error occurs.
   */
  private PageId fetchFreePageId(PageId dataPageId) throws IOException {
    if (checkIfFreePage(dataPageId)) {
      return dataPageId;
    }
    return fetchFreePageId(generateNewPage());
  }

  /**
   * Fetches the data pages of a table.
   * 
   * @param resource The table information resource.
   * @return A collection of PageId objects representing the data pages of the
   *         table.
   * @throws IOException If an I/O error occurs.
   */
  private Collection<PageId> fetchDataPage(TableInfo resource) throws IOException {
    Collection<PageId> dataPageIds = new ArrayList<>();
    Iterator<PageId> freePageIdIterator = new HeaderPage(fetchBufferForPage(resource.getHeaderPageId())).iterator();
    while (freePageIdIterator.hasNext()) {
      dataPageIds.add(freePageIdIterator.next());
    }
    releasePage(resource.getHeaderPageId(), false);
    return dataPageIds;
  }

  /**
   * Links a data page to a table.
   * 
   * @param dataPageId The ID of the data page to link.
   * @param resource   The table information resource.
   * @throws IOException If an I/O error occurs.
   */
  private void linkPage(PageId dataPageId, TableInfo resource) throws IOException {
    Collection<PageId> dataPageIds = fetchDataPage(resource);
    PageId freePageId = dataPageIds.stream().reduce((first, second) -> second).orElse(resource.getHeaderPageId());
    HeaderPage releasePage = new HeaderPage(fetchBufferForPage(freePageId));
    releasePage.setFreePageId(dataPageId);
    releasePage(freePageId, true);
  }

  /**
   * Generates a new data page for a table.
   * 
   * @param resource The table information resource.
   * @return The ID of the newly generated data page.
   * @throws IOException If an I/O error occurs.
   */
  private PageId generateDataPage(TableInfo resource) throws IOException {
    PageId dataPageId = fetchFreePageId(generateNewPage());
    linkPage(dataPageId, resource);
    DataPage dataPage = new DataPage(fetchBufferForPage(dataPageId), resource);
    dataPage.save();
    releasePage(dataPageId, true);
    return dataPageId;
  }

  /**
   * Stores a record to a data page.
   * 
   * @param record     The record to store.
   * @param dataPageId The ID of the data page to store the record to.
   * @return The ID of the record.
   * @throws IOException If an I/O error occurs.
   */
  private RecordId storeRecordToDataPage(Record record, PageId dataPageId) throws IOException {
    DataPage dataPage = new DataPage(fetchBufferForPage(dataPageId), record.resource());
    dataPage.load();
    RecordId recordId = dataPage.storeRecord(record, dataPageId);
    releasePage(dataPageId, true);
    return recordId;
  }

  /**
   * Fetches the records from a data page of a table.
   * 
   * @param resource   The table information resource.
   * @param dataPageId The ID of the data page to fetch the records from.
   * @return A collection of Record objects representing the records in the data
   *         page.
   * @throws IOException If an I/O error occurs.
   */
  private Collection<Record> fetchRecordsFromDataPage(TableInfo resource, PageId dataPageId) throws IOException {
    DataPage dataPage = new DataPage(fetchBufferForPage(dataPageId), resource);
    dataPage.load();
    Collection<Record> records = new ArrayList<>();
    Iterator<Record> recordIterator = dataPage.iterator();
    while (recordIterator.hasNext()) {
      Record record = recordIterator.next();
      record.getRecordId().setPageId(dataPageId);
      records.add(record);
    }
    releasePage(dataPageId, false);
    return records;
  }

  @Override
  public RecordId addRecordToTable(Record record) throws IOException {
    TableInfo resource = record.resource();
    PageId dataPageId = fetchFreeDataPageId(resource, record.size());
    if (dataPageId == null) {
      dataPageId = generateDataPage(resource);
    }
    RecordId recordId = storeRecordToDataPage(record, dataPageId);
    return recordId;
  }

  @Override
  public void removeRecordFromTable(Record record) throws IOException {
    TableInfo resource = record.resource();
    RecordId recordId = record.getRecordId();
    PageId dataPageId = recordId.getPageId();
    DataPage dataPage = new DataPage(fetchBufferForPage(dataPageId), resource);
    dataPage.load();
    dataPage.removeRecord(recordId);
    releasePage(dataPageId, true);
  }

  @Override
  public Collection<Record> retrieveAllRecords(TableInfo resource) throws IOException {
    Collection<Record> records = new ArrayList<>();
    Collection<PageId> dataPageIds = fetchDataPage(resource);
    for (PageId dataPageId : dataPageIds) {
      Collection<Record> dataPageRecords = fetchRecordsFromDataPage(resource, dataPageId);
      records.addAll(dataPageRecords);
    }
    return records;
  }

  /**
   * Generates a new header page in the database.
   * 
   * @return The ID of the newly generated header page.
   * @throws IOException If an I/O error occurs.
   */
  public PageId generateHeaderPage() throws IOException {
    PageId headerPageId = fetchHeaderPageId(generateNewPage());
    return headerPageId;
  }

  /**
   * Returns the unique instance of DBFileManager. If it doesn't exist, it creates
   * it.
   * 
   * @return the unique instance of DBFileManager
   */
  public static DBFileManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  /**
   * SingletonHolder is a private static class that holds the unique instance of
   * DBFileManager.
   */
  private static class SingletonHolder {
    /**
     * The unique instance of DBFileManager.
     */
    private static final DBFileManager INSTANCE = new DBFileManager();
  }
}