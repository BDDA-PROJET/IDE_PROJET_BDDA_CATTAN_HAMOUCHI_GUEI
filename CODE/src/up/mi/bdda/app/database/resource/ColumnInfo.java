package up.mi.bdda.app.database.resource;

import java.util.Objects;

import up.mi.bdda.app.database.types.Type;

public record ColumnInfo(String name, Type type) {
  public ColumnInfo {
    Objects.requireNonNull(name);
    Objects.requireNonNull(type);
  }
}
