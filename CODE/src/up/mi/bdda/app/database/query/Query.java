package up.mi.bdda.app.database.query;

/**
 * The Query interface represents a database query.
 * It provides a method to execute the query.
 */
public interface Query {

  /**
   * Run the query.
   * 
   * @throws Exception if any error occurs during the execution of the query.
   */
  void run() throws Exception;
}