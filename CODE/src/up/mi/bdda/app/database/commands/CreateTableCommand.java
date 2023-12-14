package up.mi.bdda.app.database.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import up.mi.bdda.app.database.DatabaseManager;
import up.mi.bdda.app.database.resource.ColumnInfo;
import up.mi.bdda.app.database.resource.Schema;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.types.Type;
import up.mi.bdda.app.file.FileManager;

public class CreateTableCommand implements Command {
  private String resourceName;
  private Collection<ColumnInfo> columns;

  public CreateTableCommand(String[] args) {
    columns = new ArrayList<>();
    init(args);
  }

  private void init(String[] queries) {
    resourceName = queries[0];
    String[] columns = queries[1].substring(1, queries[1].length() - 1).split(",");
    for (String column : columns) {
      String[] columnInfo = column.split(":");
      this.columns.add(new ColumnInfo(columnInfo[0], Type.of(columnInfo[1])));
    }
  }

  @Override
  public void execute() throws IOException {
    FileManager fileManager = FileManager.getSingleton();
    TableInfo resource = new TableInfo(resourceName, fileManager.createHeaderPage());
    Schema.of(resource, columns.toArray(ColumnInfo[]::new));
    DatabaseManager.getSingleton().getDatabaseInfo().addTableInfo(resource);
  }
}
