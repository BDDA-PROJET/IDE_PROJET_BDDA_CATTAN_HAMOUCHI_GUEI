package up.mi.bdda.app.page;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import up.mi.bdda.app.database.resource.RecordId;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.resource.TableInfo;

public class DataPage implements Iterable<Record> {
  private static final int PAGE_ID_SIZE = 8;

  private PageId nextPageId;
  private final SlotDirectory slotDirectory;
  private final TableInfo resource;
  private final ByteBuffer buffer;

  public DataPage(ByteBuffer buffer, TableInfo resource) {
    nextPageId = new PageId(-1, -1);
    slotDirectory = new SlotDirectory();
    this.resource = resource;
    this.buffer = buffer;
  }

  public void read() throws IOException {
    nextPageId = new PageId(buffer.getInt(0), buffer.getInt(4));
    slotDirectory.read(buffer);
  }

  public void write() throws IOException {
    buffer.putInt(0, nextPageId.getFileIdx());
    buffer.putInt(4, nextPageId.getPageIdx());
    slotDirectory.write(buffer);
  }

  private int getFreeSpace(ByteBuffer buffer) {
    int newRecordPositionSize = PAGE_ID_SIZE;
    int size = PAGE_ID_SIZE + newRecordPositionSize + slotDirectory.getSize();
    return buffer.capacity() - size;
  }

  public boolean hasSpaceLeft(int recordSize) {
    return getFreeSpace(buffer) >= recordSize;
  }

  public RecordId writeRecord(Record record, PageId dataPageId) {
    int numberOfCells = slotDirectory.getNumberOfCells();
    int freeSpacePointer = slotDirectory.getFreeSpacePointer();
    if (!hasSpaceLeft(record.size())) {
      throw new IllegalArgumentException("Error while writing the record: not enough free space");
    }
    int offset = record.write(buffer, freeSpacePointer);
    slotDirectory.setFreeSpacePointer(offset);
    slotDirectory.setNumberOfCells(numberOfCells + 2);
    slotDirectory.addRecordPosition(freeSpacePointer, record.size());
    slotDirectory.write(buffer);
    return new RecordId(dataPageId, numberOfCells / 2);
  }

  public PageId getNextPageId() {
    return nextPageId;
  }

  @Override
  public Iterator<Record> iterator() {
    return new Iterator<Record>() {
      private int index = 0;
      private int start = PAGE_ID_SIZE;

      @Override
      public boolean hasNext() {
        return index < slotDirectory.getNumberOfCells() / 2;
      }

      @Override
      public Record next() {
        if (!hasNext()) {
          throw new NoSuchElementException("No more elements to iterate over.");
        }
        Record record = new Record(resource);
        record.read(buffer, start);
        start += slotDirectory.getRecordPosition(index)[1];
        index++;
        return record;
      }
    };
  }

  public int getRecordCount() {
    return slotDirectory.getNumberOfCells() / 2;
  }

  @Override
  public String toString() {
    return "DataPage [nextPageId=" + nextPageId + ", " + slotDirectory + "]";
  }
}