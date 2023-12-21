package up.mi.bdda.app.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import up.mi.bdda.app.settings.DBParams;

/**
 * The FileHandler class provides utility methods for file operations.
 * It includes methods to retrieve file paths, access files, and generate files.
 */
public class FileHandler {

  /**
   * The path to the database folder.
   */
  private static String databaseFolderPath = DBParams.databaseFolderPath;

  /**
   * Retrieves the path of a file given its index.
   * 
   * @param fileIndex The index of the file.
   * @return An Optional containing the Path if the file exists, or an empty
   *         Optional if the file does not exist.
   */
  public static Optional<Path> retrieveFilePath(int fileIndex) {
    String fileName = String.format("F%d.data", fileIndex);
    Path path = Path.of(databaseFolderPath).resolve(fileName);
    if (!Files.exists(path)) {
      return Optional.empty();
    }
    return Optional.of(path);
  }

  /**
   * Retrieves a RandomAccessFile for a given file and page index.
   * 
   * @param fileIndex The index of the file.
   * @param pageIndex The index of the page.
   * @return An Optional containing the RandomAccessFile if the file exists, or an
   *         empty Optional if the file does not exist.
   * @throws IOException If an I/O error occurs.
   */
  public static Optional<RandomAccessFile> retrieveAccessFile(int fileIndex, int pageIndex) throws IOException {
    Optional<Path> filePath = retrieveFilePath(fileIndex);
    if (!filePath.isPresent()) {
      return Optional.empty();
    }

    try {
      RandomAccessFile file = new RandomAccessFile(filePath.get().toAbsolutePath().toString(), "rw");

      file.seek(pageIndex * DBParams.pageSize);

      return Optional.of(file);
    } catch (IOException e) {
      throw new IOException("Failed to retrieve access file: " + e.getMessage());
    }
  }

  /**
   * Generates a new file given its index.
   * 
   * @param fileIndex The index of the file.
   * @throws IOException If an I/O error occurs.
   */
  public static void generateFile(int fileIndex) throws IOException {
    Optional<Path> filePath = retrieveFilePath(fileIndex);
    if (!filePath.isPresent()) {
      String fileName = String.format("F%d.data", fileIndex);
      Path path = Path.of(databaseFolderPath).resolve(fileName);
      try {
        if (!Files.exists(path.getParent())) {
          Files.createDirectories(path.getParent());
        }
        Files.createFile(path);
      } catch (IOException e) {
        throw new IOException("Error generating file: " + e.getMessage());
      }
    }
  }
}