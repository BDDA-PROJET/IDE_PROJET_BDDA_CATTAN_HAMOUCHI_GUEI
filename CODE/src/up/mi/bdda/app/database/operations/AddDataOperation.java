package up.mi.bdda.app.database.operations;

import java.io.IOException;
import java.util.Map;

import up.mi.bdda.app.database.DBManager;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.file.DBFileManager;

/**
 * This class represents an operation to add data to a database table.
 * It implements the DatabaseOperation interface.
 */
public class AddDataOperation implements DatabaseOperation {

  /**
   * The name of the table where the data will be added.
   */
  private String resourceName;

  /**
   * The values of the record that will be added to the table.
   */
  private String[] recordValues;

  /**
   * Default constructor for the AddDataOperation class.
   */
  public AddDataOperation() {
  }

  /**
   * Initializes the resourceName and recordValues fields using the provided query
   * parameters.
   * 
   * @param queryParameters A map containing the query parameters.
   */
  private void initialize(Map<String, String> queryParameters) {
    resourceName = queryParameters.get("RESOURCE");
    recordValues = queryParameters.get("VALUES").split(",");
  }

  /**
   * Performs the operation of adding a record to a table.
   * 
   * @param queryParameters A map containing the query parameters.
   * @throws IOException If there is an error while adding the record to the
   *                     table.
   */
  @Override
  public void perform(Map<String, String> queryParameters) throws IOException {
    initialize(queryParameters);

    TableInfo tableInfo = DBManager.getInstance().getDBInfo().getTableDetails(resourceName);
    if (tableInfo == null) {
      System.out.println("Table " + resourceName + " does not exist");
      return;
    }

    Record newRecord = Record.of(tableInfo.scheme(), recordValues);
    DBFileManager.getInstance().addRecordToTable(newRecord);

    // Print a message to the user
    System.out.println(" ... done!");
  }

}