package up.mi.bdda.app.database.commands;

import java.io.IOException;

import up.mi.bdda.app.database.DatabaseManager;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.file.FileManager;

public class InsertCommand implements Command {
  private String resourceName;
  private String[] values;

  public InsertCommand(String[] args) {
    init(args);
  }

  private void init(String[] queries) {
    resourceName = queries[2];
    values = queries[4].substring(1, queries[4].length() - 1).split(",");
  }

  @Override
  public void execute() throws IOException {
    TableInfo resource = DatabaseManager.getSingleton().getDatabaseInfo().getTableInfo(resourceName);
    if (resource == null) {
      System.out.println("Table " + resourceName + " does not exist");
      return;
    }

    Record record = Record.of(resource.schema(), values);
    FileManager.getSingleton().insertRecordIntoTable(record);
  }
}
