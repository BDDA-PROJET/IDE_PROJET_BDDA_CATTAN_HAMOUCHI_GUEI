package up.mi.bdda.app.settings;

/**
 * The DBParams class holds configuration parameters for the database.
 * These parameters include the path to the database folder, the size of each
 * page in the database,
 * the maximum number of files that the Disk Manager can manage, and the maximum
 * number of frames that the Buffer Manager can manage.
 * These parameters are static and can be accessed directly from the class.
 */
public class DBParams {

  /**
   * The path to the database folder.
   * This is where all the database files will be stored.
   */
  public static String databaseFolderPath;

  /**
   * The size of each page in the database, in bytes.
   * This determines how much data can be stored in a single page.
   */
  public static int pageSize;

  /**
   * The maximum number of files that the Disk Manager can manage.
   * This limits the number of separate files that can be used to store data in
   * the database.
   */
  public static int maxFileCount;

  /**
   * The maximum number of frames that the Buffer Manager can manage.
   * This limits the number of pages that can be held in memory at once.
   */
  public static int maxFrameCount;

  /**
   * Tells whether the records should be displayed or not.
   */
  public static boolean displayRecordsValues;

}