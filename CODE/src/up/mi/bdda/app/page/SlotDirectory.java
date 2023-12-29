package up.mi.bdda.app.page;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The SlotDirectory class represents a directory of slots in a page.
 * It keeps track of the free space in the page and the start positions of each
 * record.
 */
public class SlotDirectory {
  /**
   * The position (in bytes) at which the free space on the page starts.
   * This is where new records can be added.
   */
  private int freeSpaceIndex;

  /**
   * The number of entries in the directory.
   */
  private int entryCount;

  /**
   * A list of start positions (in bytes) in the page of each record.
   * Each entry in the list is an array where the first element is the start
   * position and the second element is the size of the record.
   */
  private List<int[]> recordStartPositions;

  /**
   * Constructor for SlotDirectory.
   * Initializes the free space index to 8, the entry count to 0, and the record
   * start positions list to an empty list.
   */
  public SlotDirectory() {

    freeSpaceIndex = 8;
    entryCount = 0;
    recordStartPositions = new ArrayList<>();
  }

  /**
   * Writes the slot directory to disk.
   * 
   * @param buffer The ByteBuffer to write to.
   */
  public void saveToDisk(ByteBuffer buffer) {
    buffer.position(buffer.capacity() - 4);
    buffer.putInt(freeSpaceIndex);
    buffer.position(buffer.capacity() - 8);
    buffer.putInt(entryCount);
    for (int i = 0; i < entryCount / 2; i++) {
      buffer.position(buffer.capacity() - 8 - 8 * (i + 1));
      buffer.putInt(recordStartPositions.get(i)[0]);
      buffer.putInt(recordStartPositions.get(i)[1]);
    }
  }

  /**
   * Returns the total size of the record start positions in bytes.
   */
  private int getRecordStartPositionSize() {
    return 4 * entryCount;
  }

  /**
   * Reads the slot directory from disk.
   * 
   * @param buffer The ByteBuffer to read from.
   */
  public void loadFromDisk(ByteBuffer buffer) {
    buffer.position(buffer.capacity() - 4);
    freeSpaceIndex = buffer.getInt();
    buffer.position(buffer.capacity() - 8);
    entryCount = buffer.getInt();
    for (int i = 0; i < entryCount / 2; i++) {
      buffer.position(buffer.capacity() - 8 - 8 * (i + 1));
      int[] recordStartPosition = new int[2];
      recordStartPosition[0] = buffer.getInt();
      recordStartPosition[1] = buffer.getInt();
      recordStartPositions.add(recordStartPosition);
    }
  }

  /**
   * Returns the index of the free space in the page.
   */
  public int getFreeSpaceIndex() {
    return freeSpaceIndex;
  }

  /**
   * Returns the number of entries in the directory.
   */
  public int getEntryCount() {
    return entryCount;
  }

  /**
   * Returns the start position and size of the record at the given index.
   * 
   * @param slotIdx The index of the record.
   */
  public int[] getRecordStartPosition(int slotIdx) {
    return recordStartPositions.get(slotIdx);
  }

  /**
   * Removes the start position and size of the record at the given index.
   * 
   * @param slotIdx The index of the record.
   */
  public int[] removeRecordStartPosition(int slotIdx) {
    int[] recordStartPosition = recordStartPositions.get(slotIdx);
    int start = recordStartPosition[0];
    int size = recordStartPosition[1];
    recordStartPosition[0] = 0;
    recordStartPosition[1] = 0;
    return new int[] { start, size };
  }

  /**
   * Returns the total size of the directory in bytes.
   */
  public int getDirectorySize() {
    return 8 + getRecordStartPositionSize();
  }

  /**
   * Sets the index of the free space in the page.
   * 
   * @param offset The new free space index.
   */
  public void setFreeSpaceIndex(int offset) {
    freeSpaceIndex = offset;
  }

  /**
   * Sets the number of entries in the directory.
   * 
   * @param i The new number of entries.
   */
  public void setEntryCount(int i) {
    entryCount = i;
  }

  /**
   * Sets the start positions of the records.
   * 
   * @param recordStartPositions The new list of record start positions.
   */
  public void setRecordStartPosition(Collection<int[]> recordStartPositions) {
    this.recordStartPositions = new ArrayList<>(recordStartPositions);
  }

  /**
   * Adds a new record start position and size.
   * 
   * @param start The start position of the new record.
   * @param size  The size of the new record.
   */
  public void addRecordStartPosition(int start, int size) {
    recordStartPositions.add(new int[] { start, size });
  }

}