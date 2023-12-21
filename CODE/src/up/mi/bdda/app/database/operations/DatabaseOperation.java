package up.mi.bdda.app.database.operations;

import java.io.IOException;
import java.util.Map;

/**
 * The DatabaseOperation interface represents a database operation that can be
 * performed.
 * It provides a method to perform the operation and a static method to fetch
 * the appropriate operation.
 */
public interface DatabaseOperation {

  /**
   * Performs the database operation.
   *
   * @param parameters The parameters required to perform the operation.
   * @throws IOException If an input or output exception occurred.
   */
  void perform(Map<String, String> parameters) throws IOException;

  /**
   * Fetches the appropriate DatabaseOperation based on the operation string
   * provided.
   *
   * @param operation The operation string. Can be "CREATE", "INSERT", "SELECT",
   *                  "RESET", "IMPORT", or "DELETE".
   * @return The appropriate DatabaseOperation. Returns null if the operation
   *         string is not recognized.
   */
  static DatabaseOperation fetch(String operation) {
    switch (operation) {
      case "CREATE":
        return new TableCreationOperation();
      case "INSERT":
        return new AddDataOperation();
      case "SELECT":
        return new SelectDataOperation();
      case "RESET":
        return new ClearDatabaseOperation();
      case "IMPORT":
        return new DataImportOperation();
      case "DELETE":
        return new RemoveDataOperation();
      default:
        System.out.println("Unknown operation");
        return null;
    }
  }
}