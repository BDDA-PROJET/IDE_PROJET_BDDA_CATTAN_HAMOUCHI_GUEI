package up.mi.bdda.app.database.types;

/**
 * INT class represents the integer data type in the database.
 * It implements the DataType interface.
 */
public class INT implements DataType {

  @Override
  public String name() {
    return "INT";
  }

  @Override
  public int size() {
    return 4; // size of int in bytes
  }

  @Override
  public boolean isInstance(Object value) {
    return value instanceof Integer || value instanceof String && ((String) value).matches("-?\\d+");
  }

  @Override
  public Object parse(String value) {
    return Integer.parseInt(value);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof INT;
  }

  @Override
  public String toString() {
    return "INT";
  }
}
