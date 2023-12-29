package up.mi.bdda.app.page;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import up.mi.bdda.app.database.resource.RecordId;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.resource.TableInfo;

/**
 * The DataPage class represents a page of data in a database.
 * It contains a directory of slots (records), and methods to manipulate these
 * records.
 */
public class DataPage implements Iterable<Record> {

  /**
   * The length of the page ID in bytes.
   */
  private static final int PAGE_ID_LENGTH = 8;

  /**
   * The ID of the next page in the database.
   */
  private PageId followingPageId;

  /**
   * The directory of slots in the page.
   */
  private final SlotDirectory slotDir;

  /**
   * The table that this page belongs to.
   */
  private final TableInfo resource;

  /**
   * The buffer containing the data of the page.
   */
  private ByteBuffer byteBuffer;

  /**
   * Constructor for DataPage.
   * Initializes the following page ID to (-1, -1), the slot directory to a new
   * SlotDirectory, and the resource and byteBuffer to the given parameters.
   */
  public DataPage(ByteBuffer byteBuffer, TableInfo resource) {
    followingPageId = new PageId(-1, -1);
    slotDir = new SlotDirectory();
    this.resource = resource;
    this.byteBuffer = byteBuffer;
  }

  /**
   * Loads the page from disk into the byte buffer.
   */
  public void load() throws IOException {
    followingPageId = new PageId(byteBuffer.getInt(0), byteBuffer.getInt(4));
    slotDir.loadFromDisk(byteBuffer);
  }

  /**
   * Saves the page from the byte buffer to disk.
   */
  public void save() throws IOException {
    byteBuffer.putInt(0, followingPageId.getFileIdx());
    byteBuffer.putInt(4, followingPageId.getPageIdx());
    slotDir.saveToDisk(byteBuffer);
  }

  /**
   * Calculates the amount of free space in the page.
   */
  private int calculateFreeSpace(ByteBuffer byteBuffer) {
    int newRecordPositionSize = 8;
    int size = slotDir.getFreeSpaceIndex() + slotDir.getDirectorySize() + newRecordPositionSize;
    return byteBuffer.capacity() - size;
  }

  /**
   * Checks if there is enough space in the page to store a record of the given
   * size.
   */
  public boolean checkSpaceAvailability(int recordSize) {
    return calculateFreeSpace(byteBuffer) >= recordSize;
  }

  /**
   * Stores a record in the page and returns its ID.
   */
  public RecordId storeRecord(Record record, PageId recordPageId) throws IllegalArgumentException {
    int cellCount = slotDir.getEntryCount();
    int freeSpacePointer = slotDir.getFreeSpaceIndex();
    if (!checkSpaceAvailability(record.size())) {
      throw new IllegalArgumentException("Error while writing the record: not enough free space");
    }
    int offset = record.writeDataToBuffer(byteBuffer, freeSpacePointer);
    slotDir.setFreeSpaceIndex(offset);
    slotDir.setEntryCount(cellCount + 2);
    slotDir.addRecordStartPosition(freeSpacePointer, record.size());
    slotDir.saveToDisk(byteBuffer);
    return new RecordId(recordPageId, cellCount / 2);
  }

  /**
   * Removes a record from the page.
   */
  public void removeRecord(RecordId recordId) {
    int slotIdx = recordId.getSlotIdx();
    int[] recordPosition = slotDir.removeRecordStartPosition(slotIdx);
    int recordOffset = recordPosition[0];
    int recordSize = recordPosition[1];
    byteBuffer.position(recordOffset);
    for (int i = 0; i < recordSize; i++) {
      byteBuffer.put((byte) 0);
    }
    slotDir.saveToDisk(byteBuffer);
  }

  /**
   * Retrieves all records from the page.
   */
  private Collection<Record> retrieveAllRecords() {
    Collection<Record> records = new ArrayList<>();
    Iterator<Record> recordIterator = iterator();
    while (recordIterator.hasNext()) {
      Record record = recordIterator.next();
      records.add(record);
    }
    return records;
  }

  /**
   * Optimizes the page by compacting the records and returns the amount of free
   * space.
   */
  public int optimize() throws IOException {
    Collection<Record> records = retrieveAllRecords();
    ByteBuffer newBuffer = ByteBuffer.allocate(byteBuffer.capacity());
    Collection<int[]> recordPositions = new ArrayList<>();
    int freeSpacePointer = PAGE_ID_LENGTH;
    for (Record record : records) {
      int offset = record.writeDataToBuffer(newBuffer, freeSpacePointer);
      recordPositions.add(new int[] { freeSpacePointer, offset - freeSpacePointer });
      freeSpacePointer = offset;
    }
    slotDir.setFreeSpaceIndex(freeSpacePointer);
    slotDir.setRecordStartPosition(recordPositions);
    byteBuffer.position(0);
    byteBuffer.put(newBuffer.array());
    save();
    return calculateFreeSpace(byteBuffer);
  }

  /**
   * Returns the ID of the next page.
   */
  public PageId getFollowingPageId() {
    return followingPageId;
  }

  /**
   * Checks if a slot is empty.
   */
  private boolean isEmptySlot(int slotIdx) {
    int[] recordStartPosition = slotDir.getRecordStartPosition(slotIdx);
    return recordStartPosition[0] == 0;
  }

  /**
   * Returns the record at a given slot.
   */
  private Record getRecordAtSlot(int slotIdx) {
    int[] recordStartPosition = slotDir.getRecordStartPosition(slotIdx);
    int startPosition = recordStartPosition[0];
    Record record = new Record(resource);
    record.readDataFromBuffer(byteBuffer, startPosition);
    record.setRecordId(new RecordId());
    record.getRecordId().setSlotIdx(slotIdx);
    return record;
  }

  /**
   * Returns an iterator over the records in the page.
   */
  @Override
  public Iterator<Record> iterator() {
    return new Iterator<Record>() {
      private int currentSlot = 0;
      private int totalSlots = slotDir.getEntryCount() / 2;

      @Override
      public boolean hasNext() {
        while (currentSlot < totalSlots && isEmptySlot(currentSlot)) {
          currentSlot++;
        }
        return currentSlot < totalSlots;
      }

      @Override
      public Record next() {
        if (!hasNext()) {
          throw new NoSuchElementException("No more elements to iterate over.");
        }
        currentSlot++;
        return getRecordAtSlot(currentSlot - 1);
      }
    };
  }

  /**
   * Returns the number of records in the page.
   */
  public int countRecords() {
    return slotDir.getEntryCount() / 2;
  }

}