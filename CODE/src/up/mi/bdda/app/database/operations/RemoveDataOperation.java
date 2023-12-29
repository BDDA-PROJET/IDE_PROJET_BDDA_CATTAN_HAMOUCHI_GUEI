package up.mi.bdda.app.database.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.DBManager;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.types.DataType;

import up.mi.bdda.app.file.DBFileManager;

/**
 * This class represents an operation to remove data from a database.
 * It implements the DatabaseOperation interface.
 */
public class RemoveDataOperation implements DatabaseOperation {
  /**
   * The name of the resource (table) from which data will be removed.
   */
  private String resourceName;

  /**
   * A collection of conditions (predicates) that records must meet to be removed.
   */
  private Collection<Predicate<Record>> conditions;

  /**
   * Constructor for the RemoveDataOperation class.
   * Initializes the conditions collection.
   */
  public RemoveDataOperation() {
    conditions = new ArrayList<>();
  }

  /**
   * Initializes the operation with the provided query.
   * Extracts the resource name and conditions from the query.
   *
   * @param query A map representing the query. Keys are query parts (e.g.,
   *              "RESOURCE", "WHERE"), values are their corresponding values.
   */
  private void initialize(Map<String, String> query) {
    resourceName = query.get("RESOURCE");
    if (query.containsKey("WHERE")) {
      String[] conditions = query.get("WHERE").split(" AND ");
      String[] operators = { "<>", "<=", ">=", "<", ">", "=" };
      for (String condition : conditions) {
        for (String operator : operators) {
          if (condition.contains(operator)) {
            String[] split = condition.split(operator);
            processWhereCondition(split[0], operator, split[1]);
            break;
          }
        }
      }
    }
  }

  /**
   * Processes a WHERE condition and adds a corresponding predicate to the
   * conditions collection.
   *
   * @param field    The field the condition applies to.
   * @param operator The operator used in the condition (e.g., "=", "<>", "<=",
   *                 ">=", "<", ">").
   * @param value    The value used in the condition.
   */
  @SuppressWarnings("unchecked")
  private void processWhereCondition(String field, String operator, String value) {
    TableInfo resource = DBManager.getInstance().getDBInfo().getTableDetails(resourceName);
    DataType type = resource.scheme().getField(field).type;
    Object parseValue = type.parse(value);

    switch (operator) {
      case "=":
        conditions.add(record -> {
          Object fieldValue = record.getDataElement(field) instanceof String
              ? ((String) record.getDataElement(field)).trim()
              : record.getDataElement(field);
          return fieldValue.equals(parseValue);
        });
        break;
      case "<":
        conditions.add(record -> ((Comparable<Object>) record.getDataElement(field)).compareTo(parseValue) < 0);
        break;
      case ">":
        conditions.add(record -> ((Comparable<Object>) record.getDataElement(field)).compareTo(parseValue) > 0);
        break;
      case "<=":
        conditions.add(record -> ((Comparable<Object>) record.getDataElement(field)).compareTo(parseValue) <= 0);
        break;
      case ">=":
        conditions.add(record -> ((Comparable<Object>) record.getDataElement(field)).compareTo(parseValue) >= 0);
        break;
      case "<>":
        conditions.add(record -> {
          Object fieldValue = record.getDataElement(field) instanceof String
              ? ((String) record.getDataElement(field)).trim()
              : record.getDataElement(field);
          return !fieldValue.equals(parseValue);
        });
        break;
    }
  }

  /**
   * Performs the remove data operation.
   * Retrieves all records from the resource, removes those that don't meet the
   * conditions, and then removes the remaining records from the table.
   *
   * @param query A map representing the query. Keys are query parts (e.g.,
   *              "RESOURCE", "WHERE"), values are their corresponding values.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public void perform(Map<String, String> query) throws IOException {
    initialize(query);

    TableInfo resource = DBManager.getInstance().getDBInfo().getTableDetails(resourceName);
    Collection<Record> records = DBFileManager.getInstance().retrieveAllRecords(resource);
    Optional<Predicate<Record>> combined = conditions.stream().reduce(Predicate::and);

    if (combined.isPresent()) {
      records.removeIf(combined.get().negate());
    }

    for (Record record : records) {
      DBFileManager.getInstance().removeRecordFromTable(record);
    }

    System.out.println();
    System.out.println(String.format("Total deleted record(s): %d", records.size()));
  }
}