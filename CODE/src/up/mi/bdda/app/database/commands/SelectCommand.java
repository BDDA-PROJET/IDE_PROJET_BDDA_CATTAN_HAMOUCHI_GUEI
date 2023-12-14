package up.mi.bdda.app.database.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import up.mi.bdda.app.database.DatabaseManager;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.types.Type;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.file.FileManager;

public class SelectCommand implements Command {
  private String resourceName;
  private Collection<Predicate<Record>> filters;

  public SelectCommand(String[] args) {
    filters = new ArrayList<>();
    resourceName = args[0];
    init(args);
  }

  private void init(String[] queries) {
    if (queries.length > 2 && queries[1].equals("WHERE")) {
      String[] operators = { "=", "<>", "<=", ">=", "<", ">" };
      for (int i = 2; i < queries.length; i++) {
        for (String operator : operators) {
          if (queries[i].contains(operator)) {
            String[] whereCondition = queries[i].split(operator);
            handleWhereCondition(whereCondition[0], operator, whereCondition[1]);
            break;
          }
        }
      }
    }
  }

  private void handleWhereCondition(String field, String operator, String value) {
    // implement where condition with filters (=, <, >, <=, >=, <>)

    TableInfo resource = DatabaseManager.getSingleton().getDatabaseInfo().getTableInfo(resourceName);
    Type type = resource.schema().getColumn(field).type();
    Object parseValue = type.parse(value);

    switch (operator) {
      case "=":
        filters.add(record -> record.get(field).equals(parseValue));
        break;
      case "<":
        filters.add(record -> ((Comparable) record.get(field)).compareTo(parseValue) < 0);
        break;
      case ">":
        filters.add(record -> ((Comparable) record.get(field)).compareTo(parseValue) > 0);
        break;
      case "<=":
        filters.add(record -> ((Comparable) record.get(field)).compareTo(parseValue) <= 0);
        break;
      case ">=":
        filters.add(record -> ((Comparable) record.get(field)).compareTo(parseValue) >= 0);
        break;
      case "<>":
        filters.add(record -> !record.get(field).equals(parseValue));
        break;
    }
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

    for (Predicate<Record> filter : filters) {
      records.removeIf(filter.negate());
    }

    printRecords(records);
  }
}
