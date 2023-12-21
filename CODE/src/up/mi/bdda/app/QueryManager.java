package up.mi.bdda.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import up.mi.bdda.app.database.DBManager;
import up.mi.bdda.app.settings.DBParams;

/**
 * The QueryManager class is responsible for managing user queries.
 * It allows users to input queries, stores them, and executes them on a
 * database.
 * It also provides functionality to load and save queries from and to a file.
 */
public class QueryManager {

  /**
   * The main method is the entry point of the QueryManager.
   * It sets up the database parameters, loads queries from a file if provided,
   * and enters a loop to accept and process user queries.
   * 
   * @param args Command line arguments. The first argument is expected to be the
   *             path to a file containing queries.
   * @throws Exception If an error occurs while processing queries or interacting
   *                   with the database.
   */
  public static void main(String[] args) throws Exception {
    DBParams.databaseFolderPath = "DB";
    DBParams.pageSize = 4096; // 4KB
    DBParams.maxFileCount = 4;
    DBParams.maxFrameCount = 2;

    List<String> storedQueries = new ArrayList<>();

    if (args.length > 0) {
      loadQueriesFromFile(storedQueries, args[0]);
    } else {
      System.out.println("No file found! It doesn't matter ... : )");
    }

    BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

    System.out.println("!! Welcome to the DBMS !!");
    while (true) {
      System.out.print(":: Please enter your queries\n-> ");
      String userQuery = userInputReader.readLine();
      if (userQuery.equals("EXIT")) {
        System.out.println("Bye bye!");
        break;
      }
      String[] parsedQueries = userQuery.split(" ", 2);
      switch (parsedQueries[0]) {
        case "LIST":
          System.out.println("===");
          System.out.println("Here are the list of queries:");
          System.out.println("===");
          if (storedQueries.size() == 0) {
            System.out.println("No queries found!");
          } else {
            for (String query : storedQueries) {
              System.out.println(query);
            }
            System.out.println("===");
          }
          break;
        case "ADD":
          storedQueries.add(parsedQueries[1]);
          break;
        default:
          System.out.println("Unknown command!");
          break;
      }
    }
    userInputReader.close();

    if (args.length > 0) {
      saveQueriesToFile(storedQueries, args[0]);
    }

    if (storedQueries.size() == 0) {
      return;
    }

    DBManager dbManager = DBManager.getInstance();
    dbManager.startInitialization();
    for (String query : storedQueries) {
      if (query.equals("EXIT")) {
        break;
      }
      dbManager.executeQuery(query);
    }
    dbManager.endProcess();
  }

  /**
   * Loads queries from a file and adds them to a list.
   * 
   * @param queries  The list to which the queries will be added.
   * @param filename The path to the file containing the queries.
   * @throws IOException If an error occurs while reading the file.
   */
  public static void loadQueriesFromFile(List<String> queries, String filename) throws IOException {
    try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = fileReader.readLine()) != null) {
        queries.add(line);
      }
    } catch (IOException e) {
      throw new IOException("Error reading file", e);
    }
  }

  /**
   * Saves queries from a list to a file.
   * 
   * @param queries  The list of queries to be saved.
   * @param filename The path to the file where the queries will be saved.
   * @throws IOException If an error occurs while writing to the file.
   */
  public static void saveQueriesToFile(List<String> queries, String filename) throws IOException {
    try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filename, false))) {
      for (String query : queries) {
        fileWriter.write(query);
        fileWriter.newLine();
      }
    } catch (IOException e) {
      throw new IOException("Error writing to file", e);
    }
  }

}
