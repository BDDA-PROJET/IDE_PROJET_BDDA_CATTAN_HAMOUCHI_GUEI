package up.mi.bdda.app.database.query;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import up.mi.bdda.app.database.operations.DatabaseOperation;

/**
 * The QueryParser class is responsible for parsing SQL-like queries into a more
 * manageable format.
 * It takes a query as a string and breaks it down into a map of key-value
 * pairs, where each key is a part of the query (like "OPERATION", "RESOURCE",
 * etc.),
 * and each value is the corresponding part of the query.
 */
public class QueryParser {
  /**
   * The query to be parsed.
   */
  private String query;

  /**
   * Constructs a new QueryParser with the specified query.
   * 
   * @param query the query to be parsed
   */
  public QueryParser(String query) {
    this.query = query;
  }

  /**
   * Parses the query into a map of key-value pairs.
   * 
   * @return a Query object representing the parsed query
   * @throws IllegalArgumentException if the query cannot be parsed
   */
  public Query parse() throws IllegalArgumentException {
    Map<String, String> queries = new HashMap<>();
    int index;

    if ((index = query.indexOf("CREATE TABLE")) != -1) {
      String query = this.query.substring(index + 13);
      String[] split = query.split(" ");
      String resource = split[0];
      String columns = query.substring(resource.length() + 2, query.length() - 1);
      queries.put("OPERATION", "CREATE");
      queries.put("RESOURCE", resource);
      queries.put("COLUMNS", columns);
    } else if ((index = query.indexOf("RESETDB")) != -1) {
      queries.put("OPERATION", "RESET");
    } else if ((index = query.indexOf("INSERT INTO")) != -1) {
      String query = this.query.substring(index + 12);
      String[] split = query.split(" ");
      String resource = split[0];
      String values = split[2].substring(1, split[2].length() - 1);
      queries.put("OPERATION", "INSERT");
      queries.put("RESOURCE", resource);
      queries.put("VALUES", values);
    } else if ((index = query.indexOf("SELECT")) != -1) {
      String query = this.query.substring(index + 7);
      String[] split = query.split(" ");
      String resource = split[2];
      String condition = Arrays.stream(split).skip(4).collect(Collectors.joining(" "));
      queries.put("OPERATION", "SELECT");
      queries.put("RESOURCE", resource);
      queries.put("WHERE", condition);
    } else if ((index = query.indexOf("DELETE")) != -1) {
      String query = this.query.substring(index + 7);
      String[] split = query.split(" ");
      String resource = split[2];
      String condition = Arrays.stream(split).skip(4).collect(Collectors.joining(" "));
      queries.put("OPERATION", "DELETE");
      queries.put("RESOURCE", resource);
      queries.put("WHERE", condition);
    } else if ((index = query.indexOf("IMPORT INTO")) != -1) {
      String query = this.query.substring(index + 12);
      String[] split = query.split(" ");
      String resource = split[0];
      String file = split[1];
      queries.put("OPERATION", "IMPORT");
      queries.put("RESOURCE", resource);
      queries.put("FILE", file);
    }

    return new SimpleQuery(queries);
  }
}

/**
 * The SimpleQuery class implements the Query interface and represents a simple
 * database query.
 * It encapsulates a map of parameters that are used to perform a database
 * operation.
 */
class SimpleQuery implements Query {

  /**
   * A map of parameters for the database operation. The keys are parameter names
   * and the values are parameter values.
   */
  private Map<String, String> parameters;

  /**
   * Constructs a new SimpleQuery with the given parameters.
   *
   * @param parameters a map of parameters for the database operation
   */
  public SimpleQuery(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  /**
   * Performs the database operation specified by the parameters.
   *
   * @throws IOException if an I/O error occurs during the operation
   */
  @Override
  public void run() throws IOException {
    DatabaseOperation operation = DatabaseOperation.fetch(parameters.get("OPERATION"));
    operation.perform(parameters);
  }
}
