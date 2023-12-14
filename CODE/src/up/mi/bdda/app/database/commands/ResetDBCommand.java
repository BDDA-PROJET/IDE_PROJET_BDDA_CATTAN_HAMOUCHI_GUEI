package up.mi.bdda.app.database.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import up.mi.bdda.app.DBParams;
import up.mi.bdda.app.database.DatabaseManager;

public class ResetDBCommand implements Command {
  public ResetDBCommand() {
  }

  @Override
  public void execute() throws IOException {
    // delete all files from the DB folder if it exists
    Path dbPath = Paths.get(DBParams.DBPath);
    if (Files.exists(dbPath)) {
      Files.walk(dbPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
    // Path dbFolderPath = Paths.get(DBParams.DBPath);
    // Files.walk(dbFolderPath)
    // .sorted(Comparator.reverseOrder())
    // .forEach(path -> {
    // try {
    // Files.delete(path);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // });

    // reset the database
    DatabaseManager.getSingleton().reset();
  }

}
