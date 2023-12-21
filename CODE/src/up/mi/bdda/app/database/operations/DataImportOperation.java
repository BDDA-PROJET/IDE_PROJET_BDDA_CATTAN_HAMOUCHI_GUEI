package up.mi.bdda.app.database.operations;

import java.io.IOException;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;

import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.DBManager;
import up.mi.bdda.app.database.resource.Scheme;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.file.DBFileManager;

/**
 * This class represents an operation to import data from a file into a database
 * table.
 * It implements the DatabaseOperation interface.
 */
public class DataImportOperation implements DatabaseOperation {

  /**
   * The name of the table where the data will be imported.
   */
  private String resourceName;

  /**
   * The path of the file from which the data will be imported.
   */
  private String dataFilePath;

  /**
   * Default constructor.
   */
  public DataImportOperation() {
  }

  /**
   * Initializes the operation with the provided query parameters.
   * 
   * @param query A map containing the query parameters. Expected keys are
   *              "RESOURCE" for the table name and "FILE" for the file path.
   */
  private void initialize(Map<String, String> query) {
    resourceName = query.get("RESOURCE");
    dataFilePath = query.get("FILE");
  }

  /**
   * Performs the data import operation.
   * 
   * @param query A map containing the query parameters. Expected keys are
   *              "RESOURCE" for the table name and "FILE" for the file path.
   * @throws IOException If an error occurs while reading the file.
   */
  @Override
  public void perform(Map<String, String> query) throws IOException {
    initialize(query);

    TableInfo resource = DBManager.getInstance().getDBInfo().getTableDetails(resourceName);
    Scheme scheme = resource.scheme();

    try (BufferedReader br = new BufferedReader(new FileReader(dataFilePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        Record record = Record.of(scheme, values);
        DBFileManager.getInstance().addRecordToTable(record);
      }
    }
  }
}