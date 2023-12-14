package up.mi.bdda.app.page;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import up.mi.bdda.app.settings.DBParams;
import up.mi.bdda.app.utils.FileHandler;

/**
 * The PageId class represents a unique identifier for a page in a file.
 * It implements Cloneable and Serializable interfaces for object cloning and
 * serialization.
 */
public class PageId implements Cloneable, Serializable {

  /**
   * The index of the file.
   */
  private int fileIdx;

  /**
   * The index of the page within the file.
   */
  private int pageIdx;

  /**
   * Default constructor that resets the indexes.
   */
  public PageId() {
    resetIndexes();
  }

  /**
   * Constructor that sets the file and page indexes.
   *
   * @param fileIdx The index of the file.
   * @param pageIdx The index of the page within the file.
   */
  public PageId(int fileIdx, int pageIdx) {
    this.fileIdx = fileIdx;
    this.pageIdx = pageIdx;
  }

  /**
   * Returns the file index.
   *
   * @return The file index.
   */
  public int getFileIdx() {
    return fileIdx;
  }

  /**
   * Returns the page index.
   *
   * @return The page index.
   */
  public int getPageIdx() {
    return pageIdx;
  }

  /**
   * Sets the file index.
   *
   * @param fileIdx The new file index.
   * @throws IllegalArgumentException If the file index is negative.
   */
  public void setFileIdx(int fileIdx) throws IllegalArgumentException {
    if (fileIdx < 0) {
      throw new IllegalArgumentException("File index cannot be negative");
    }
    this.fileIdx = fileIdx;
  }

  /**
   * Sets the page index.
   *
   * @param pageIdx The new page index.
   * @throws IllegalArgumentException If the page index is negative.
   */
  public void setPageIdx(int pageIdx) {
    if (pageIdx < 0) {
      throw new IllegalArgumentException("Page index cannot be negative");
    }
    this.pageIdx = pageIdx;
  }

  /**
   * Sets the file and page indexes.
   *
   * @param fileIdx The new file index.
   * @param pageIdx The new page index.
   * @throws IllegalArgumentException If any of the indexes is negative.
   */
  public void setIndexes(int fileIdx, int pageIdx) throws IllegalArgumentException {
    setFileIdx(fileIdx);
    setPageIdx(pageIdx);
  }

  /**
   * Sets the file and page indexes based on another PageId object.
   *
   * @param pageIdentifier The PageId object to copy the indexes from.
   * @throws IllegalArgumentException If the PageId object is invalid.
   */
  public void setIndexes(PageId pageIdentifier) {
    if (!pageIdentifier.isValid()) {
      throw new IllegalArgumentException("Invalid PageId object");
    }
    this.fileIdx = pageIdentifier.fileIdx;
    this.pageIdx = pageIdentifier.pageIdx;
  }

  /**
   * Resets the file and page indexes to -1.
   */
  public void resetIndexes() {
    fileIdx = -1;
    pageIdx = -1;
  }

  /**
   * Returns a string representation of the PageId object.
   *
   * @return A string in the format "fileIdx.pageIdx".
   */
  @Override
  public String toString() {
    return String.format("%d.%d", fileIdx, pageIdx);
  }

  /**
   * Creates and returns a copy of this PageId object.
   *
   * @return A clone of this PageId object.
   * @throws RuntimeException If the object cannot be cloned.
   */
  @Override
  public final PageId clone() {
    try {
      PageId clonedObject = (PageId) super.clone();

      Objects.requireNonNull(clonedObject, "Cloned object is null");

      return clonedObject;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks if this PageId object is equal to another object.
   *
   * @param obj The object to compare with.
   * @return true if the objects are the same or if the other object is a PageId
   *         with the same indexes, false otherwise.
   */
  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof PageId)) {
      return false;
    }
    PageId otherPageIdentifier = (PageId) obj;

    return Objects.equals(this.fileIdx, otherPageIdentifier.fileIdx)
        && Objects.equals(this.pageIdx, otherPageIdentifier.pageIdx);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return A hash code value for this object.
   */
  @Override
  public final int hashCode() {
    return Objects.hash(fileIdx, pageIdx);
  }

  /**
   * Checks if the PageId object is valid.
   *
   * @return true if both file and page indexes are non-negative, false otherwise.
   */
  public boolean isValid() {
    return fileIdx >= 0 && pageIdx >= 0;
  }

  /**
   * Increments the file index by 1 and the page index by 1 if the file index is
   * reset to 0.
   */
  public void nextIndex() {
    int nextFileId = (fileIdx + 1) % DBParams.maxFileCount;
    int nextPageId = nextFileId > 0 ? pageIdx : pageIdx + 1;

    fileIdx = nextFileId;
    pageIdx = nextPageId;
  }

  /**
   * Returns the path of the file with the current file index.
   *
   * @return The path of the file.
   * @throws FileNotFoundException If the file does not exist.
   */
  public Path getFilePath() throws FileNotFoundException {
    Optional<Path> filePath = FileHandler.retrieveFilePath(fileIdx);
    if (!filePath.isPresent()) {
      throw new FileNotFoundException("File does not exist: " + filePath.toString());
    }
    return filePath.get();
  }

  /**
   * Returns a RandomAccessFile object for the file with the current file and page
   * indexes.
   *
   * @return A RandomAccessFile object for the file.
   * @throws IOException If the file cannot be accessed.
   */
  public RandomAccessFile getAccessFile() throws IOException {
    Optional<RandomAccessFile> file = FileHandler.retrieveAccessFile(fileIdx, pageIdx);
    if (!file.isPresent()) {
      throw new IOException(String.format("Failed to get access file for page identifier: %d.%d", fileIdx, pageIdx));
    }
    return file.get();
  }

  /**
   * Creates a new file with the current file index.
   *
   * @throws IOException If the file cannot be created.
   */
  public void createFile() throws IOException {
    FileHandler.generateFile(fileIdx);
  }
}
