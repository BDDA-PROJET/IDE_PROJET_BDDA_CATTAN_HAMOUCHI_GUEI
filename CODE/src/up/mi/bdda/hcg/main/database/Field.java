package up.mi.bdda.hcg.main.database;

import java.util.Objects;

public record Field(Object value, Type type) {
  /**
   * We ensure that the value and type provided to our Field record aren’t
   * null using the following constructor implementation.
   * 
   * @param value the value of the database record field
   * @param type  the type of the value
   */
  public Field {
    Objects.requireNonNull(value);
    Objects.requireNonNull(type);
  }
}
