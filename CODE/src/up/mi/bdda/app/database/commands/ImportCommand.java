package up.mi.bdda.app.database.commands;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.io.BufferedReader;
import java.io.FileReader;

import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.buffer.BufferManager;
import up.mi.bdda.app.database.DatabaseManager;
import up.mi.bdda.app.database.resource.ColumnInfo;
import up.mi.bdda.app.database.resource.Schema;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.types.Type;
import up.mi.bdda.app.file.FileManager;

public class ImportCommand implements Command{
    
    private String resourceName;
  private Collection<ColumnInfo> columns;

  public ImportCommand(String[] args) {
    columns = new ArrayList<>();
    init(args);
  }

  private void init(String[] queries) {
    resourceName = queries[0];
  }

  public void execute(String chemin) throws IOException {
    TableInfo ti = DatabaseManager.getSingleton().getDatabaseInfo().getTableInfo(resourceName);
    
    try (BufferedReader bufR = new BufferedReader(new FileReader(chemin))) {
        String line = null;
        while ((line = bufR.readLine()) != null) {
            Record record = new Record(ti);
            String [] values = line.split(",");
            record.addValues(values);
            FileManager.getSingleton().insertRecordIntoTable(record);
  }
}
}

@Override
public void execute() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'execute'");
}
}
