package up.mi.bdda.app.database;

import java.io.IOException;

import up.mi.bdda.app.buffer.BufferManager;
import up.mi.bdda.app.database.query.Query;
import up.mi.bdda.app.database.query.QueryParser;
import up.mi.bdda.app.disk.DiskManager;

public class DatabaseManager {
  private DatabaseInfo databaseInfo;

  private DatabaseManager() {
    databaseInfo = new DatabaseInfo();
  }

  public void init() throws IOException {
    databaseInfo.init();
    DiskManager.getSingleton().init();
    BufferManager.getSingleton().init();
  }

  public void finish() throws IOException {
    databaseInfo.finish();
    DiskManager.getSingleton().finish();
    BufferManager.getSingleton().flushAll();
  }

  public void reset() throws IOException {
    databaseInfo.reset();
    DiskManager.getSingleton().reset();
    BufferManager.getSingleton().reset();
  }

  public void processQuery(String query) throws Exception {
    QueryParser parser = new QueryParser(query);
    Query queryObject = parser.parse();
    System.out.println(String.format("Executing query: %s", query));
    queryObject.execute();
  }

  public DatabaseInfo getDatabaseInfo() {
    return databaseInfo;
  }

  /**
   * This method returns the unique instance of {@code DiskManager}. If it doesn't
   * exist, it creates it.
   * 
   * @return the unique instance of {@code DiskManager}
   */
  public static DatabaseManager getSingleton() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    /**
     * The unique instance of {@codeDatabaseManager}.
     */
    private static final DatabaseManager INSTANCE = new DatabaseManager();
  }
}
