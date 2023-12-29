package up.mi.bdda.app.database.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import up.mi.bdda.app.database.DBManager;
import up.mi.bdda.app.database.resource.FieldInfo;
import up.mi.bdda.app.database.resource.Scheme;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.types.DataType;
import up.mi.bdda.app.file.DBFileManager;

/**
 * This class represents a database operation for creating a new table.
 * It implements the DatabaseOperation interface.
 */
public class TableCreationOperation implements DatabaseOperation {

  /**
   * The name of the table to be created.
   */
  private String resourceName;

  /**
   * The collection of fields (columns) for the new table.
   */
  private Collection<FieldInfo> fields;

  /**
   * Constructor for the TableCreationOperation class.
   * Initializes the fields collection.
   */
  public TableCreationOperation() {
    fields = new ArrayList<>();
  }

  /**
   * Initializes the operation with the provided query map.
   * Extracts the table name and fields from the query map.
   * 
   * @param query The map containing the query parameters.
   */
  private void initialize(Map<String, String> query) {
    resourceName = query.get("RESOURCE");
    String[] fields = query.get("COLUMNS").split(",");
    for (String field : fields) {
      String[] fieldInfo = field.split(":");
      this.fields.add(new FieldInfo(fieldInfo[0], DataType.of(fieldInfo[1])));
    }
  }

  /**
   * Performs the table creation operation.
   * Initializes the operation with the provided query map, creates a new table
   * and adds it to the database.
   * 
   * @param query The map containing the query parameters.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public void perform(Map<String, String> query) throws IOException {
    DBManager dbManager = DBManager.getInstance();
    TableInfo resource = dbManager.getDBInfo().getTableDetails(query.get("RESOURCE"));
    if (resource != null) {
      System.out.println("Table already exists!");
      return;
    }

    if (query.get("OPERATION") == "") {
      throw new IllegalArgumentException("No table name provided!");
    }

    initialize(query);

    DBFileManager fileManager = DBFileManager.getInstance();
    resource = new TableInfo(resourceName, fileManager.generateHeaderPage());
    Scheme.of(resource, fields.toArray(FieldInfo[]::new));
    DBManager.getInstance().getDBInfo().addResourceDetails(resource);

    // Print a message to the user
    System.out.println(" ... done!");
  }

}