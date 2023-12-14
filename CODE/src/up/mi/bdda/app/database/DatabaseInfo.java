package up.mi.bdda.app.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.settings.DBParams;

/**
 * This class is responsible for managing the database information.
 * It provides methods to initialize, clear, save, and retrieve database
 * information.
 */
public final class DatabaseInfo {

  /**
   * The path to the file where the database information is stored.
   */
  private final String DB_INFO_FILE = String.format("%s/DBInfo.save", DBParams.databaseFolderPath);

  /**
   * A map that stores the details of each table in the database.
   * The key is the name of the table, and the value is a TableInfo object that
   * contains the details of the table.
   */
  private Map<String, TableInfo> tableDetailsMap;

  /**
   * Constructor that initializes the tableDetailsMap.
   */
  public DatabaseInfo() {
    tableDetailsMap = new HashMap<>();
  }

  /**
   * Initializes the database information by loading it from a file.
   * If the file does not exist, it does nothing.
   * 
   * @throws IOException if there is an error while loading the database info.
   */
  public void initialize() throws IOException {
    File dbFile = new File(DB_INFO_FILE);
    if (dbFile.exists()) {
      try (FileInputStream fileIn = new FileInputStream(DB_INFO_FILE);
          ObjectInputStream in = new ObjectInputStream(fileIn)) {
        tableDetailsMap = (Map<String, TableInfo>) in.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new IOException("Error while loading the database info", e);
      }
    }
  }

  /**
   * Clears the database information and deletes the file where it is stored.
   * 
   * @throws IOException if there is an error while deleting the database info.
   */
  public void clearData() throws IOException {
    tableDetailsMap.clear();
    File dbFile = new File(DB_INFO_FILE);
    if (dbFile.exists()) {
      if (!dbFile.delete()) {
        throw new IOException("Error while deleting the database info");
      }
    }
  }

  /**
   * Saves the current database information to a file.
   * 
   * @throws IOException if there is an error while saving the database info.
   */
  public void saveData() throws IOException {
    try (FileOutputStream fileOut = new FileOutputStream(DB_INFO_FILE);
        ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(tableDetailsMap);
    } catch (IOException e) {
      throw new IOException("Error while saving the database info", e);
    }
  }

  /**
   * Adds the details of a table to the database information.
   * If the table already exists, it does nothing.
   * 
   * @param resource The details of the table to add.
   */
  public void addResourceDetails(TableInfo resource) {
    tableDetailsMap.computeIfAbsent(resource.name(), resourceName -> {
      return resource;
    });
  }

  public TableInfo getTableDetails(String resourceName) throws IllegalArgumentException {
    return tableDetailsMap.get(resourceName);
  }

  public int getResourceCount() {
    return tableDetailsMap.size();
  }
}
