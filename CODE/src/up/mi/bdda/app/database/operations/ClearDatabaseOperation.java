package up.mi.bdda.app.database.operations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;

import up.mi.bdda.app.database.DBManager;
import up.mi.bdda.app.settings.DBParams;

/**
 * This class represents an operation to clear the database.
 * It implements the DatabaseOperation interface.
 */
public class ClearDatabaseOperation implements DatabaseOperation {

  /**
   * Default constructor for the ClearDatabaseOperation class.
   */
  public ClearDatabaseOperation() {
  }

  /**
   * This method performs the operation of clearing the database.
   * It first deletes all files in the database folder, then it clears all data in
   * the database.
   *
   * @param query A map representing the query parameters.
   * @throws IOException If an I/O error occurs during the operation.
   */
  @Override
  public void perform(Map<String, String> query) throws IOException {
    // Path to the database folder
    Path databasePath = Paths.get(DBParams.databaseFolderPath);

    // Delete all files from the database folder if it exists
    if (Files.exists(databasePath)) {
      Files.walk(databasePath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    // Reset the database
    DBManager.getInstance().clearAll();
  }
}