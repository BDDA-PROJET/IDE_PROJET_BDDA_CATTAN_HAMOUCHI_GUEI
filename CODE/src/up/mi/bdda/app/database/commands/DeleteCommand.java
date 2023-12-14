package up.mi.bdda.app.database.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.database.DatabaseManager;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.types.Type;

import up.mi.bdda.app.file.FileManager;

public class DeleteCommand implements Command {
    private String resourceName;
  private Collection<Predicate<Record>> filters;

  public DeleteCommand(String[] args) {
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

  @Override
  public void execute() throws IOException {
    TableInfo resource = DatabaseManager.getSingleton().getDatabaseInfo().getTableInfo(resourceName);
    if (resource == null) {
      System.out.println("Table " + resourceName + " does not exist");
      return;
    }

    Collection<Record> records = FileManager.getSingleton().getAllRecords(resource);
    int total = records.size();
   
    Optional<Predicate<Record>> combined = filters.stream().reduce(Predicate::and);

    if (combined.isPresent()) {
      records.removeIf(combined.get());
    }
     System.out.println(String.format("Total deleted records = %d", total - records.size()));
    
  }
}