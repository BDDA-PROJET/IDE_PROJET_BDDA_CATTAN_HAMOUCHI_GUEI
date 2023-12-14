package up.mi.bdda.app.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import up.mi.bdda.app.DBParams;
import up.mi.bdda.app.database.resource.TableInfo;

public final class DatabaseInfo {
  private final String DB_INFO_FILE = String.format("%s/DBInfo.save", DBParams.DBPath);
  private Map<String, TableInfo> resources;

  public DatabaseInfo() {
    resources = new HashMap<>();
  }

  public void init() throws IOException {
    File dbFile = new File(DB_INFO_FILE);
    if (dbFile.exists()) {
      try (FileInputStream fileIn = new FileInputStream(DB_INFO_FILE);
          ObjectInputStream in = new ObjectInputStream(fileIn)) {
        resources = (Map<String, TableInfo>) in.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new IOException("Error while loading the database info", e);
      }
    }
  }

  public void reset() throws IOException {
    resources.clear();
    File dbFile = new File(DB_INFO_FILE);
    if (dbFile.exists()) {
      if (!dbFile.delete()) {
        throw new IOException("Error while deleting the database info");
      }
    }
  }

  public void finish() throws IOException {
    try (FileOutputStream fileOut = new FileOutputStream(DB_INFO_FILE);
        ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(resources);
    } catch (IOException e) {
      throw new IOException("Error while saving the database info", e);
    }
  }

  public void addTableInfo(TableInfo resource) {
    resources.computeIfAbsent(resource.name(), resourceName -> {
      return resource;
    });
  }

  public TableInfo getTableInfo(String resourceName) throws IllegalArgumentException {
    if (!resources.containsKey(resourceName)) {
      throw new IllegalArgumentException(String.format("The resource `%s` does not exist", resourceName));
    }
    return resources.get(resourceName);
  }

  public int getRelationCount() {
    return resources.size();
  }
}
