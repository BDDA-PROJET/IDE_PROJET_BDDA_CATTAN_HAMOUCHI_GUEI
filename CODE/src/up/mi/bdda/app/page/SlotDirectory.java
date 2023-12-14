package up.mi.bdda.app.page;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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
  }

  /**
   * Writes the slot directory to disk.
   * 
   * @param buffer ByteBuffer to write to.
   */
  public void write(ByteBuffer buffer) {
    buffer.position(buffer.capacity() - 4);
    buffer.putInt(freeSpacePointer);
    buffer.position(buffer.capacity() - 8);
    buffer.putInt(numberOfCells);
    for (int i = 0; i < numberOfCells / 2; i++) {
      buffer.position(buffer.capacity() - 8 - 8 * (i + 1));
      buffer.putInt(recordPositions.get(i)[0]);
      buffer.putInt(recordPositions.get(i)[1]);
    }
  }

  /**
   * Reads the slot directory from disk.
   * 
   * @param buffer ByteBuffer to read from.
   */
  public void read(ByteBuffer buffer) {
    buffer.position(buffer.capacity() - 4);
    freeSpacePointer = buffer.getInt();
    buffer.position(buffer.capacity() - 8);
    numberOfCells = buffer.getInt();
    for (int i = 0; i < numberOfCells / 2; i++) {
      buffer.position(buffer.capacity() - 8 - 8 * (i + 1));
      int[] recordPosition = new int[2];
      recordPosition[0] = buffer.getInt();
      recordPosition[1] = buffer.getInt();
      recordPositions.add(recordPosition);
    }
  }

  // Getters
  public int getFreeSpacePointer() {
    return freeSpacePointer;
  }

  public int getNumberOfCells() {
    return numberOfCells;
  }

  public int[] getRecordPosition(int slotIdx) {
    return recordPositions.get(slotIdx);
  }

  public int getSize() {
    int recordSizes = 0;
    for (int[] recordPosition : recordPositions) {
      recordSizes += recordPosition[1];
    }
    return 8 + 4 * numberOfCells + recordSizes;
  }

  public void setFreeSpacePointer(int offset) {
    freeSpacePointer = offset;
  }

  public void setNumberOfCells(int i) {
    numberOfCells = i;
  }

  public void addRecordPosition(int start, int size) {
    recordPositions.add(new int[] { start, size });
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("SlotDirectory [freeSpacePointer=").append(freeSpacePointer)
        .append(", numberOfCells=").append(numberOfCells)
        .append(", recordPositions=[");
    for (int i = 0; i < recordPositions.size(); i++) {
      sb.append("[start=").append(recordPositions.get(i)[0]).append(", size=")
          .append(recordPositions.get(i)[1]).append("]");
      if (i < recordPositions.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append("]]");
    return sb.toString();
  }
}