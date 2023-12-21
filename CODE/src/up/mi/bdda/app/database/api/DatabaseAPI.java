package up.mi.bdda.app.database.api;

import java.io.IOException;
import java.util.Collection;

import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.resource.RecordId;
import up.mi.bdda.app.database.resource.TableInfo;

/**
 * The DatabaseAPI interface provides a contract for interacting with a
 * database.
 * It defines methods for adding, removing, and retrieving records from a
 * database table.
 */
public interface DatabaseAPI {

  /**
   * Adds a record to a table in the database.
   *
   * @param record The record to be added to the table.
   * @return The ID of the added record.
   * @throws IOException If an input or output exception occurred.
   */
  RecordId addRecordToTable(Record record) throws IOException;

  /**
   * Removes a record from a table in the database.
   *
   * @param record The record to be removed from the table.
   * @throws IOException If an input or output exception occurred.
   */
  void removeRecordFromTable(Record record) throws IOException;

  /**
   * Retrieves all records from a specific table in the database.
   *
   * @param resource The table from which to retrieve all records.
   * @return A collection of all records in the specified table.
   * @throws IOException If an input or output exception occurred.
   */
  Collection<Record> retrieveAllRecords(TableInfo resource) throws IOException;
}