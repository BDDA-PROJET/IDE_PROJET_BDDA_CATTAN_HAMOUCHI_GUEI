package up.mi.bdda.app.page;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import up.mi.bdda.app.DBParams;
import up.mi.bdda.app.utils.FileUtility;

/**
 * This Java class represents a page identifier. It is used to uniquely identify
 * a page within a file. The {@link PageId} class has two main properties:
 * {@link #fileIdx} and {@link #pageIdx}, which represent the file index and the
 * page index, respectively.
 * <p>
 * Here's a brief description of the methods in this class:
 * <ul>
 * <li>{@link #PageId()} - This is the default constructor that reset the
 * {@link #fileIdx} and {@link #pageIdx} to -1.
 * <li>{@link #PageId(int, int)} - This is a constructor that sets the
 * {@link #fileIdx} and {@link #pageIdx} to the provided values.
 * <li>{@link #getFileIdx()} and {@link #getPageIdx()} - These methods return
 * the file index and page index respectively.
 * <li>{@link #setFileIdx(int)} and {@link #setPageIdx(int)} - These methods set
 * the file index and page index respectively.
 * <li>{@link #set(int, int)} - This method sets both the file index and the
 * page index.
 * <li>{@link #set(PageId)} - This method sets the {@link #fileIdx} and
 * {@link #pageIdx} based on another {@link PageId} object.
 * <li>{@link #reset()} - This method resets the {@link #fileIdx} and
 * {@link #pageIdx} to -1.
 * <li>{@link #clone()} - this method creates and returns a copy of the
 * {@link PageId} object.
 * <li>{@link #toString()} - This method returns a string representation of the
 * {@link PageId} object.
 * <li>{@link #equals(Object)} - This method checks if the current
 * {@link PageId} object is equal to another {@link PageId} object.
 * <li>{@link #isValid()} - This method checks if the {@link #fileIdx} and
 * {@link #pageIdx} are valid (i.e, greater than -1).
 * <li>{@link #next()} - This method calculates the next page identifier based
 * on the current {@link fileIdx} and {@link pageIdx}.
 * <li>{@link #getFilePath()} - This method constructs a file path based on the
 * {@link #fileIdx}.
 * <li>{@link #getAccessFile()} - This method opens and returns a
 * {@link RandomAccessFile} object for the file at the path returned by
 * {@link #getFilePath()}.
 * <li>{@link #createFile()} - This method is responsible for creating a new
 * file based on the page identifier.
 * </ul>
 */
public class PageId implements Cloneable {

  /** The file index. */
  private int fileIdx;

  /** The page index. */
  private int pageIdx;

  /**
   * Default constructor. It resets the {@link #fileIdx} and {@link #pageIdx} to
   * -1.
   */
  public PageId() {
    reset();
  }

  /**
   * This is a constructor that sets the {@link #fileIdx} and {@link #pageIdx} to
   * the provided values.
   * 
   * @param fileIdx the file index value
   * @param pageIdx the page index value
   */
  public PageId(int fileIdx, int pageIdx) {
    this.fileIdx = fileIdx;
    this.pageIdx = pageIdx;
  }

  /**
   * This method returns the file index.
   * 
   * @return the file index value
   */
  public int getFileIdx() {
    return fileIdx;
  }

  /**
   * This method returns the page index.
   * 
   * @return the page index value
   */
  public int getPageIdx() {
    return pageIdx;
  }

  /**
   * This method sets the file index.
   * 
   * @param fileIdx the file index value to set
   * 
   * @throws IllegalArgumentException if the file index is negative
   */
  public void setFileIdx(int fileIdx) throws IllegalArgumentException {
    if (fileIdx < 0) {
      throw new IllegalArgumentException("File index cannot be negative");
    }
    this.fileIdx = fileIdx;
  }

  /**
   * This method sets the page index.
   * 
   * @param pageIdx the page index value to set
   * 
   * @throws IllegalArgumentException if the page index is negative
   */
  public void setPageIdx(int pageIdx) {
    if (pageIdx < 0) {
      throw new IllegalArgumentException("Page index cannot be negative");
    }
    this.pageIdx = pageIdx;
  }

  /**
   * This method sets both the file index and the page index.
   * 
   * @param fileIdx the file index value to set
   * @param pageIdx the page index value to set
   * 
   * @throws IllegalArgumentException if the file index or page index is negative
   */
  public void set(int fileIdx, int pageIdx) throws IllegalArgumentException {
    setFileIdx(fileIdx);
    setPageIdx(pageIdx);
  }

  /**
   * This method sets the {@link #fileIdx} and {@link #pageIdx} based on another.
   * 
   * @param pageId the {@link PageId} object to set
   * 
   * @throws IllegalArgumentException if the {@link PageId} object is {@code null}
   *                                  or {@code invalid}
   */
  public void set(PageId pageId) {
    if (!pageId.isValid()) {
      throw new IllegalArgumentException("Invalid PageId object");
    }
    this.fileIdx = pageId.fileIdx;
    this.pageIdx = pageId.pageIdx;
  }

  /**
   * This method resets the {@link #fileIdx} and {@link #pageIdx} to -1 if they
   * are not already -1.
   *
   * @return {@code true} if the reset was successful, {@code false} otherwise
   */
  public void reset() {
    fileIdx = -1;
    pageIdx = -1;
  }

  /**
   * This method returns a string representation of the {@link PageId} object.
   * The string includes the file index and page index.
   *
   * @return a string representation of the {@link PageId} object
   */
  @Override
  public String toString() {
    return String.format("%d.%d", fileIdx, pageIdx);
  }

  /**
   * This method creates and returns a copy of the {@link PageId} object.
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
   * Checks if the current {@link PageId} object is equal to another.
   *
   * @param obj the object to compare with the current {@link PageId} object.
   * @return {@code true} if the current {@link PageId} object is equal to the
   *         other {@link PageId} object, {@code false} otherwise.
   */
  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof PageId)) {
      return false;
    }
    PageId otherPageId = (PageId) obj;

    return Objects.equals(this.fileIdx, otherPageId.fileIdx)
        && Objects.equals(this.pageIdx, otherPageId.pageIdx);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(fileIdx, pageIdx);
  }

  /**
   * This method checks if the {@link #fileIdx} and {@link #pageIdx} are valid.
   * 
   * @return {@code true} if the {@link #fileIdx} and {@link #pageIdx} are valid,
   *         {@code false} otherwise
   */
  public boolean isValid() {
    return fileIdx >= 0 && pageIdx >= 0;
  }

  /**
   * This method calculates the next page identifier based on the current
   * {@link fileIdx} and {@link pageIdx}.
   */
  public void next() {
    int nextFileId = (fileIdx + 1) % DBParams.DMFFileCount;
    int nextPageId = nextFileId > 0 ? pageIdx : pageIdx + 1;

    fileIdx = nextFileId;
    pageIdx = nextPageId;
  }

  /**
   * This method constructs a file path based on the file index (fileIdx). It uses
   * DBParams.DBPath as the base directory and the file name is formatted as
   * {@code F%d.data} where %d is the file index.
   * 
   * @throws FileNotFoundException if the file does not exist at the constructed
   *                               path
   */
  public Path getFilePath() throws FileNotFoundException {
    Optional<Path> filePath = FileUtility.getFilePath(fileIdx);
    if (!filePath.isPresent()) {
      throw new FileNotFoundException("File does not exist: " + filePath.toString());
    }
    return filePath.get();
  }

  /**
   * This method opens and returns a RandomAccessFile for the file based on the
   * page identifier.
   * 
   * @throws IOException if an {@link IOException} occurs during the operation
   */
  public RandomAccessFile getAccessFile() throws IOException {
    Optional<RandomAccessFile> file = FileUtility.getAccessFile(fileIdx, pageIdx);
    if (!file.isPresent()) {
      throw new IOException(String.format("Failed to get access file for page identifier: %d.%d", fileIdx, pageIdx));
    }
    return file.get();
  }

  /**
   * This method is responsible for creating a new file based on the page
   * identifier.
   * 
   * @throws IOException if an {@link IOException} occurs during the operation
   */
  public void createFile() throws IOException {
    FileUtility.createFile(fileIdx);
  }
}
