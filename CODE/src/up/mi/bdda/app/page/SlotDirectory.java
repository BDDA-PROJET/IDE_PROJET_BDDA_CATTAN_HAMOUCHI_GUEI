package up.mi.bdda.app.page;

import java.nio.ByteBuffer;
import java.util.ArrayList;
<<<<<<< HEAD
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
=======
import java.util.List;

/**
 * Class representing a Slot Directory.
 */
public class SlotDirectory {
  // Position (byte) at which the free space on the page (=where records can be
  // added)
  private int freeSpacePointer;
  // Number of cells in the directory
  private int numberOfCells;
  // Start position (byte) in the page of each record
  private List<int[]> recordPositions; // [recordIdx][start/size]

  /**
   * Constructor for SlotDirectory.
   */
  public SlotDirectory() {
    freeSpacePointer = 8;
    numberOfCells = 0;
    recordPositions = new ArrayList<>();
>>>>>>> 5df7839 (query in process)
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
   * Clears the slot directory in the given buffer.
   * 
   * @param buffer The ByteBuffer to clear.
   */
  public void clear(ByteBuffer buffer) {
    int size = 8 + getRecordStartPositionSize();
    buffer.position(buffer.capacity() - size);
    for (int i = 0; i < getRecordStartPositionSize(); i++) {
      buffer.put((byte) 0);
    }
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

  // Getters
  public int getFreeSpaceIndex() {
    return freeSpaceIndex;
  }

  public int getEntryCount() {
    return entryCount;
  }

  public int[] getRecordStartPosition(int slotIdx) {
    return recordStartPositions.get(slotIdx);
  }

  public int[] removeRecordStartPosition(int slotIdx) {
    int[] recordStartPosition = recordStartPositions.remove(slotIdx);
    entryCount -= 2;
    return recordStartPosition;
  }

  public int getDirectorySize() {
    return 8 + getRecordStartPositionSize();
  }

  // Setters
  public void setFreeSpaceIndex(int offset) {
    freeSpaceIndex = offset;
  }

  public void setEntryCount(int i) {
    entryCount = i;
  }

  public void setRecordStartPosition(Collection<int[]> recordStartPositions) {
    this.recordStartPositions = new ArrayList<>(recordStartPositions);
  }

  public void addRecordStartPosition(int start, int size) {
    recordStartPositions.add(new int[] { start, size });
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("SlotDirectory [freeSpaceIndex=").append(freeSpaceIndex)
        .append(", entryCount=").append(entryCount)
        .append(", recordStartPositions=[");
    for (int i = 0; i < recordStartPositions.size(); i++) {
      sb.append("[start=").append(recordStartPositions.get(i)[0]).append(", size=")
          .append(recordStartPositions.get(i)[1]).append("]");
      if (i < recordStartPositions.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append("]]");
    return sb.toString();
  }
}