package up.mi.bdda.app.database.commands;

import java.io.IOException;

public interface Command {
  void execute() throws IOException;

  static void execute(String command, String[] args) throws IOException {
    switch (command) {
      case "CREATE":
        new CreateTableCommand(args).execute();
        break;
      case "INSERT":
        new InsertCommand(args).execute();
        break;
      case "SELECT":
        new SelectCommand(args).execute();
        break;
      case "RESET":
        new ResetDBCommand().execute();
        break;
      case "IMPORT" :
        new ImportCommand(args).execute();
        break;
      case "DELETE" :
        new DeleteCommand(args).execute();
        break;
      default:
        System.out.println("Unknown command");
        break;
    }
  }
}
