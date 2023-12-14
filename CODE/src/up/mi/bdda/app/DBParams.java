package up.mi.bdda.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import up.mi.bdda.app.database.DatabaseManager;

/**
 * This class represents application parameters and provides default values for
 * these parameters.
 */
public class DBParams {

  /** The path to the DB folder. */
  public static String DBPath;

  /** Page size. */
  public static int SGBDPageSize;

  /** The maximum number of files managed by the Disk Manager. */
  public static int DMFFileCount;

  /** The maximum number of frames managed by the Buffer Manager. */
  public static int frameCount;

  /**
   * Sets the default values for the application parameters.
   * 
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    DBPath = "DB";
    SGBDPageSize = 4096; // 4KB
    DMFFileCount = 4;
    frameCount = 2;

    List<String> queryList = new ArrayList<>();

    readFromFile(queryList);

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
      System.out.println("Welcome to the DBMS!, please enter your queries:");
      String query = reader.readLine();
      if (query.equals("EXIT")) {
        System.out.println("Bye bye!");
        break;
      }
      String[] queries = query.split(" ", 2);
      switch (queries[0]) {
        case "LIST":
          System.out.println("===");
          System.out.println("Here are the list of queries:");
          System.out.println("===");
          for (String person : queryList) {
            System.out.println(person);
          }
          break;
        case "ADD":
          queryList.add(queries[1]);
          break;
        default:
          System.out.println("Unknown command!");
          break;
      }
    }
    reader.close();
    writeToFile(queryList);

    DatabaseManager databaseManager = DatabaseManager.getSingleton();

    // evaluate the various DBParams parameters
    if (queryList.size() == 0) {
      return;
    }

    // init the DatabaseManager
    databaseManager.init();

    // process the queries
    for (String query : queryList) {
      if (query.equals("EXIT")) {
        break;
      }
      databaseManager.processQuery(query);
    }

  }

  public static void readFromFile(List<String> list) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader("test_case.txt"))) {
      String line;
      while ((line = reader.readLine()) != null) {
        list.add(line);
      }
    } catch (IOException e) {
      System.out.println("No file found! It doesn't matter ... : )");
    }
  }

  public static void writeToFile(List<String> list) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("test_case.txt", false))) {
      for (String line : list) {
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException e) {
      throw new IOException("Error writing to file", e);
    }
  }

}
