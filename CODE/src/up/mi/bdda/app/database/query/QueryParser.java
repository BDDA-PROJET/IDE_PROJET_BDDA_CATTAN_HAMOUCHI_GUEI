package up.mi.bdda.app.database.query;

import java.io.IOException;

import up.mi.bdda.app.database.commands.Command;

public class QueryParser {
  private String query;

  public QueryParser(String query) {
    this.query = query;
  }

  public Query parse() {
    // Here you would parse the query string and create a Query object.
    // This is a simplified example, in a real-world application you would have more
    // complex parsing logic.
    return new BasicQuery(this.query);
  }
}

class BasicQuery implements Query {
  private String query;

  public BasicQuery(String query) {
    this.query = query;
  }

  @Override
  public void execute() throws IOException {
    // Here you would execute the query against your database.
    // This is a simplified example, in a real-world application you would use a
    // library like JDBC to execute the query.
    System.out.println("Executing query: " + query);

    String[] queries = query.split(" ");

    if (query.startsWith("CREATE TABLE")) {
      System.out.println("Creating table " + queries[2]);
      Command.execute("CREATE TABLE", queries);
    } else if (query.startsWith("INSERT INTO")) {
      System.out.println("Inserting into table " + queries[2]);
      Command.execute("INSERT INTO", queries);
    } else if (query.startsWith("SELECT")) {
      System.out.println("Selecting from table " + queries[3]);
      Command.execute("SELECT", queries);
    } else if (query.startsWith("RESETDB")) {
      System.out.println("Resetting database");
      Command.execute("RESETDB", queries);
    } else {
      System.out.println("Unknown query type");
    }
  }
}
