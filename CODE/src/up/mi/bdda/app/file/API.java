package up.mi.bdda.app.file;

import java.io.IOException;
import java.util.Collection;

import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.resource.RecordId;
import up.mi.bdda.app.database.resource.TableInfo;

public interface API {
  RecordId insertRecordIntoTable(Record record) throws IOException;

  Collection<Record> getAllRecords(TableInfo resource) throws IOException;
}
