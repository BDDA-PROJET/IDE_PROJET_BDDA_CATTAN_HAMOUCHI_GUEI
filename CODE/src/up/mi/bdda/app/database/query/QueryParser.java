package up.mi.bdda.app.database.query;

import java.io.IOException;

import javax.print.DocFlavor.STRING;

import up.mi.bdda.app.database.commands.Command;

public class QueryParser {
  private String query;

  public QueryParser(String query) {
    this.query = query;
  }

  public Query parse() throws IllegalArgumentException {
      String command = "";
      String query = ""; 
      if (this.query.startsWith("CREATE TABLE")) {
      command = "CREATE";
      query = this.query.substring(13);
      
    } else if (this.query.startsWith("INSERT INTO")) {
      command = "INSERT";
      query = this.query.substring(12);
    } else if (this.query.startsWith("SELECT")) {
      command = "SELECT";
      query = this.query.substring(14);
    } else if (this.query.startsWith("RESETDB")) {
      command = "RESET";
    } else if (this.query.startsWith("DELETE")){
      command = "DELETE";
      query = this.query.substring(12);
    } else if(this.query.startsWith("IMPORT INTO")){
      command = "IMPORT";
      query = this.query.substring(12);
    }
    else {
      throw new IllegalArgumentException("Unknown query type");
    }
    return new BasicQuery(command,query);
  }
}

class BasicQuery implements Query {
  private String query;
  private String command;

  public BasicQuery(String command, String query) {
    this.command = command;
    this.query = query;
  }

  @Override
  public void execute() throws IOException {
    // Here you would execute the query against your database.
    // This is a simplified example, in a real-world application you would use a
    // library like JDBC to execute the query.
    System.out.println(String.format("Executing query: %s %s", command,query ));

    String[] queries = query.split(" ");

    Command.execute(command,queries);
  }
}
