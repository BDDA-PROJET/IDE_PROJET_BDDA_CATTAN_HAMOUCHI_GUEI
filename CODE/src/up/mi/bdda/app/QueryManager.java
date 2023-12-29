package up.mi.bdda.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
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
    DBParams.displayRecordsValues = true;

    List<String> storedQueries = new ArrayList<>();

    System.out.println("!! Welcome to the DBMS !!");

    if (args.length > 0) {
      try {
        loadQueriesFromFile(storedQueries, args[0]);
        List<String> availableOperations = List.of("RESETDB", "CREATE", "IMPORT", "INSERT", "SELECT", "DELETE", "EXIT");
        Iterator<String> storedQueriesIterator = storedQueries.iterator();
        while (storedQueriesIterator.hasNext()) {
          String query = storedQueriesIterator.next();
          if (!availableOperations.contains(query.split(" ")[0])) {
            storedQueriesIterator.remove();
          }
        }
        System.out.println(String.format(":: (Info) Loaded %d %s from file: %s", storedQueries.size(),
            storedQueries.size() > 1 ? "queries" : "query", args[0]));
      } catch (IOException e) {
        System.out.println(String.format(":: (Error) Could not load queries from file: %s", args[0]));
      }

    } else {
      System.out.println(":: (Info) No file provided for loading queries, starting with an empty list of queries.");
    }

    BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
    System.out.println(
        ":: (Info) You can not execute queries one by one, you must enter them all and execute them at once.");
    System.out.println(
        ":: (Info) Please note that only lines that follow the available operations (RESETDB, CREATE, INSERT, IMPORT, SELECT, EXIT) will be executed.");
    System.out.println("\n:: Please enter all your queries below :");
    while (true) {
      System.out.println(":: Enter `LIST` to see all queries.");
      System.out.println(":: Enter `ADD <query>` to add a query.");
      System.out.print(":: Enter `START` to execute all queries at once.\n-> ");
      String userQuery = userInputReader.readLine();
      if (userQuery.equals("START")) {
        break;
      }
      String[] parsedQueries = userQuery.split(" ", 2);
      switch (parsedQueries[0]) {
        case "LIST":
          System.out.println("===");
          System.out.println(":: Here is the list of queries:");
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
          System.out.println("Query added!");
          break;
        default:
          System.out.println(":: (Error) Unknown command!");
          break;
      }
    }
    userInputReader.close();

    if (args.length > 0) {
      try {
        saveQueriesToFile(storedQueries, args[0]);
        System.out.println(String.format(":: (Info) Saved %d %s to file: %s", storedQueries.size(),
            storedQueries.size() > 1 ? "queries" : "query", args[0]));
      } catch (IOException e) {
        System.out.println(String.format(":: (Error) Could not save queries to file: %s", args[0]));
      }
    }

    if (storedQueries.size() == 0) {
      System.out.println("No queries found!, exiting ...");
      System.out.println("\n:: Thank you for using the DBMS!");

      return;
    }

    DBParams.displayRecordsValues = args.length > 1 && args[1].equals("true");

    DBManager dbManager = DBManager.getInstance();
    dbManager.startInitialization();
    for (String query : storedQueries) {
      if (query.equals("EXIT")) {
        System.out.print("Executing query: EXIT");
        dbManager.endProcess();
        System.out.println(" ... done!");
      } else {
        dbManager.executeQuery(query);
      }
    }
    dbManager.endProcess();

    System.out.println("\n:: Thank you for using the DBMS!");
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
      throw new IOException("Error reading file: " + filename);
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
      throw new IOException("Error writing to file: " + filename);
    }
  }

}
