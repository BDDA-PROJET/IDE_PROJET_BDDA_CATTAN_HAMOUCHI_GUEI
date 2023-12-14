package up.mi.bdda.app.database.commands;

import java.io.IOException;

public interface Command {
  void execute() throws IOException;

  static void execute(String command, String[] args) throws IOException {
    switch (command) {
      case "CREATE TABLE":
        new CreateTableCommand(args).execute();
        break;
      case "INSERT INTO":
        new InsertCommand(args).execute();
        break;
      case "SELECT":
        new SelectCommand(args).execute();
        break;
      case "RESETDB":
        new ResetDBCommand().execute();
        break;
      default:
        System.out.println("Unknown command");
        break;
    }
  }
}
