package up.mi.bdda.app.database.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import up.mi.bdda.app.database.DBManager;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.types.DataType;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.file.DBFileManager;
import up.mi.bdda.app.settings.DBParams;

/**
 * This class represents a select operation on a database.
 * It is responsible for processing a query and displaying the results.
 */
public class SelectDataOperation implements DatabaseOperation {
  /**
   * The name of the resource (table) to perform the select operation on.
   */
  private String resourceName;

  /**
   * The collection of conditions to be applied on the select operation.
   */
  private Collection<Predicate<Record>> conditions;

  /**
   * Constructor for the SelectDataOperation class.
   * Initializes the conditions collection.
   */
  public SelectDataOperation() {
    conditions = new ArrayList<>();
  }

  /**
   * Initializes the select operation with the provided query parameters.
   * 
   * @param query The query parameters to initialize the select operation with.
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
   * Processes a where condition and adds it to the conditions collection.
   * 
   * @param field    The field to apply the condition on.
   * @param operator The operator of the condition.
   * @param value    The value to compare the field with.
   */
  @SuppressWarnings("unchecked")
  private void processWhereCondition(String field, String operator, String value) {
    TableInfo resource = DBManager.getInstance().getDBInfo().getTableDetails(resourceName);
    DataType type = resource.scheme().getField(field).type;
    Object parsedValue = type.parse(value);

    switch (operator) {
      case "=":
        conditions.add(record -> {
          Object fieldValue = record.getDataElement(field) instanceof String
              ? ((String) record.getDataElement(field)).trim()
              : record.getDataElement(field);
          return fieldValue.equals(parsedValue);
        });
        break;
      case "<":
        conditions.add(record -> ((Comparable<Object>) record.getDataElement(field)).compareTo(parsedValue) < 0);
        break;
      case ">":
        conditions.add(record -> ((Comparable<Object>) record.getDataElement(field)).compareTo(parsedValue) > 0);
        break;
      case "<=":
        conditions.add(record -> ((Comparable<Object>) record.getDataElement(field)).compareTo(parsedValue) <= 0);
        break;
      case ">=":
        conditions.add(record -> ((Comparable<Object>) record.getDataElement(field)).compareTo(parsedValue) >= 0);
        break;
      case "<>":
        conditions.add(record -> {
          Object fieldValue = record.getDataElement(field) instanceof String
              ? ((String) record.getDataElement(field)).trim()
              : record.getDataElement(field);
          return !fieldValue.equals(parsedValue);
        });
        break;
    }
  }

  /**
   * Displays the records that match the select operation conditions.
   * 
   * @param records The records to display.
   */
  private void displayRecords(Collection<Record> records) {
    System.out.println();

    if (records.isEmpty()) {
      System.out.println("No records found!");
      return;
    }

    if (DBParams.displayRecordsValues) {
      for (Record record : records) {
        System.out.println(record);
      }
    }

    System.out.println(String.format("Total record(s): %d", records.size()));
  }

  /**
   * Performs the select operation with the provided query parameters.
   * 
   * @param queryParameters The query parameters to perform the select operation
   *                        with.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public void perform(Map<String, String> queryParameters) throws IOException {
    initialize(queryParameters);

    TableInfo resource = DBManager.getInstance().getDBInfo().getTableDetails(resourceName);
    Collection<Record> records = DBFileManager.getInstance().retrieveAllRecords(resource);

    Optional<Predicate<Record>> filter = conditions.stream().reduce(Predicate::and);
    if (filter.isPresent()) {
      records.removeIf(filter.get().negate());
    }

    displayRecords(records);
  }
}