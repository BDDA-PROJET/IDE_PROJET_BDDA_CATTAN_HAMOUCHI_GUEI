package up.mi.bdda.app.database.commands;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import up.mi.bdda.app.database.DatabaseManager;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.file.FileManager;

public class SelectCommand implements Command {
  private String resourceName;
  private Map<String, String> conditions;

  public SelectCommand(String[] args) {
    init(args);
  }

  private void init(String[] queries) {
    resourceName = queries[3];
  }

  private void printRecords(Collection<Record> records) {
    if (records.isEmpty()) {
      System.out.println("No records found");
      return;
    }

    for (Record record : records) {
      System.out.println(record);
    }
    System.out.println(String.format("Total records: %d", records.size()));
  }

  @Override
  public void execute() throws IOException {
    TableInfo resource = DatabaseManager.getSingleton().getDatabaseInfo().getTableInfo(resourceName);
    if (resource == null) {
      System.out.println("Table " + resourceName + " does not exist");
      return;
    }

    Collection<Record> records = FileManager.getSingleton().getAllRecords(resource);
    printRecords(records);
  }
}
