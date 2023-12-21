package up.mi.bdda.app.database;

import java.io.IOException;

import up.mi.bdda.app.buffer.BufferManager;
import up.mi.bdda.app.database.query.Query;
import up.mi.bdda.app.database.query.QueryParser;
import up.mi.bdda.app.disk.DiskManager;

/**
 * This class is responsible for managing the database.
 * It provides methods to initialize, clear, save, and retrieve database
 * information, and to execute queries.
 * It follows the Singleton design pattern to ensure that only one instance of
 * DBManager exists in the application.
 */
public class DBManager {

  /**
   * The DatabaseInfo object that contains the details of the database.
   */
  private DatabaseInfo dbInfo;

  /**
   * Private constructor that initializes the dbInfo.
   * It is private to prevent the creation of additional instances of DBManager.
   */
  private DBManager() {
    dbInfo = new DatabaseInfo();
  }

  /**
   * Starts the initialization process of the database.
   * It initializes the database information, the disk manager, and the buffer
   * manager.
   * 
   * @throws IOException if there is an error during the initialization process.
   */
  public void startInitialization() throws IOException {
    dbInfo.initialize();
    DiskManager.getInstance().initialize();
    BufferManager.getInstance().initialize();
  }

  /**
   * Ends the process of the database.
   * It saves the database information, terminates the disk manager, and completes
   * the buffer manager.
   * 
   * @throws IOException if there is an error during the end process.
   */
  public void endProcess() throws IOException {
    dbInfo.saveData();
    DiskManager.getInstance().terminate();
    BufferManager.getInstance().complete();
  }

  /**
   * Clears all the data in the database.
   * It clears the database information, the disk manager, and the buffer manager.
   * 
   * @throws IOException if there is an error during the clear process.
   */
  public void clearAll() throws IOException {
    dbInfo.clearData();
    DiskManager.getInstance().clear();
    BufferManager.getInstance().clearMemory();
  }

  /**
   * Executes a query on the database.
   * It parses the query, prints it, and then executes it.
   * 
   * @param query The query to execute.
   * @throws Exception if there is an error during the execution of the query.
   */
  public void executeQuery(String query) throws Exception {
    QueryParser parser = new QueryParser(query);
    Query queryObject = parser.parse();
    System.out.println(String.format("Executing query: %s", query));
    queryObject.run();
  }

  /**
   * Returns the DatabaseInfo object that contains the details of the database.
   * 
   * @return The DatabaseInfo object.
   */
  public DatabaseInfo getDBInfo() {
    return dbInfo;
  }

  /**
   * Returns the unique instance of DBManager.
   * If it doesn't exist, it creates it.
   * 
   * @return The unique instance of DBManager.
   */
  public static DBManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  /**
   * This class holds the unique instance of DBManager.
   * It is a private static class to ensure that only one instance of DBManager
   * exists.
   */
  private static class SingletonHolder {
    /**
     * The unique instance of DBManager.
     */
    private static final DBManager INSTANCE = new DBManager();
  }
}