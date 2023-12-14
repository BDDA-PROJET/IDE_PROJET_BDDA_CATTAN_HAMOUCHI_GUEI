package up.mi.bdda.app.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import up.mi.bdda.app.DBParams;

/**
 * This is a utility class named FileUtility in the package. It provides static
 * methods for file operations, specifically related to accessing files in a
 * database.
 */
public class FileUtility {
  /**
   * This method returns the path of the file with the given index.
   * 
   * @param fileIdx the index of the file
   * 
   * @return the path of the file
   */
  public static Optional<Path> getFilePath(int fileIdx) {
    String fileName = String.format("F%d.data", fileIdx);
    Path path = Path.of(DBParams.DBPath).resolve(fileName);
    if (!Files.exists(path)) {
      return Optional.empty();
    }
    return Optional.of(path);
  }

  /**
   * This method returns the random access file of the file with the given index.
   * 
   * @param fileIdx the index of the file
   * 
   * @return the random access file of the file
   * 
   * @throws IOException if an {@link IOException} occurs during the operation
   */
  public static Optional<RandomAccessFile> getAccessFile(int fileIdx, int pageIdx) throws IOException {
    Optional<Path> filePath = getFilePath(fileIdx);
    if (!filePath.isPresent()) {
      return Optional.empty();
    }

    try {
      RandomAccessFile file = new RandomAccessFile(filePath.get().toAbsolutePath().toString(), "rw");

      file.seek(pageIdx * DBParams.SGBDPageSize);

      return Optional.of(file);
    } catch (IOException e) {
      throw new IOException("Failed to get access file: " + e.getMessage());
    }
  }

  /**
   * This method creates a file with the given index.
   * 
   * @param fileIdx the index of the file
   * 
   * @throws IOException if an {@link IOException} occurs during the operation
   */
  public static void createFile(int fileIdx) throws IOException {
    Optional<Path> filePath = getFilePath(fileIdx);
    if (!filePath.isPresent()) {
      String fileName = String.format("F%d.data", fileIdx);
      Path path = Path.of(DBParams.DBPath).resolve(fileName);
      try {
        if (!Files.exists(path.getParent())) {
          Files.createDirectories(path.getParent());
        }
        Files.createFile(path);
      } catch (IOException e) {
        throw new IOException("Error creating file: " + e.getMessage());
      }
    }
  }
}
