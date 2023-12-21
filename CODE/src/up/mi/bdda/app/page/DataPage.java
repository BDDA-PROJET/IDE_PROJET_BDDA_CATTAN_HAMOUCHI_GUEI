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

public class DataPage implements Iterable<Record> {
  private static final int PAGE_ID_LENGTH = 8;

  private PageId followingPageId;
  private final SlotDirectory slotDir;
  private final TableInfo resource;
  private ByteBuffer byteBuffer;

  public DataPage(ByteBuffer byteBuffer, TableInfo resource) {
    followingPageId = new PageId(-1, -1);
    slotDir = new SlotDirectory();
    this.resource = resource;
    this.byteBuffer = byteBuffer;
  }

  public void load() throws IOException {
    followingPageId = new PageId(byteBuffer.getInt(0), byteBuffer.getInt(4));
    slotDir.loadFromDisk(byteBuffer);
  }

  public void save() throws IOException {
    byteBuffer.putInt(0, followingPageId.getFileIdx());
    byteBuffer.putInt(4, followingPageId.getPageIdx());
    slotDir.saveToDisk(byteBuffer);
  }

  private int calculateFreeSpace(ByteBuffer byteBuffer) {
    int newRecordPositionSize = 8;
    int size = slotDir.getFreeSpaceIndex() + slotDir.getDirectorySize() + newRecordPositionSize;
    return byteBuffer.capacity() - size;
  }

  public boolean checkSpaceAvailability(int recordSize) {
    return calculateFreeSpace(byteBuffer) >= recordSize;
  }

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

  public void removeRecord(RecordId recordId) {
    int slotIdx = recordId.getSlotIdx();
    slotDir.clear(byteBuffer);
    int[] recordPosition = slotDir.removeRecordStartPosition(slotIdx);
    int recordOffset = recordPosition[0];
    int recordSize = recordPosition[1];
    byteBuffer.position(recordOffset);
    for (int i = 0; i < recordSize; i++) {
      byteBuffer.put((byte) 0);
    }
    slotDir.saveToDisk(byteBuffer);
  }

  private Collection<Record> retrieveAllRecords() {
    Collection<Record> records = new ArrayList<>();
    Iterator<Record> recordIterator = iterator();
    while (recordIterator.hasNext()) {
      Record record = recordIterator.next();
      records.add(record);
    }
    return records;
  }

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

  public PageId getFollowingPageId() {
    return followingPageId;
  }

  @Override
  public Iterator<Record> iterator() {
    return new Iterator<Record>() {
      private int currentIndex = 0;
      private int startPosition = 0;

      @Override
      public boolean hasNext() {
        return currentIndex < slotDir.getEntryCount() / 2;
      }

      @Override
      public Record next() {
        if (!hasNext()) {
          throw new NoSuchElementException("No more elements to iterate over.");
        }
        startPosition = slotDir.getRecordStartPosition(currentIndex)[0];
        Record record = new Record(resource);
        record.readDataFromBuffer(byteBuffer, startPosition);
        record.setRecordId(new RecordId());
        record.getRecordId().setSlotIdx(currentIndex);
        currentIndex++;
        return record;
      }
    };
  }

  public int countRecords() {
    return slotDir.getEntryCount() / 2;
  }

  @Override
  public String toString() {
    return "DataPage [followingPageId=" + followingPageId + ", " + slotDir + "]";
  }
}