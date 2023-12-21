package up.mi.bdda.app.database.types;

/**
 * FLOAT class represents a floating-point number data type in the database.
 * It implements the DataType interface.
 */
public class FLOAT implements DataType {

  @Override
  public String name() {
    return "FLOAT";
  }

  @Override
  public int size() {
    return 4; // size of float in bytes
  }

  @Override
  public boolean isInstance(Object value) {
    return value instanceof Float || value instanceof String && ((String) value).matches("-?\\d+(\\.\\d+)?");
  }

  @Override
  public Object parse(String value) {
    return Float.parseFloat(value);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof FLOAT;
  }

  @Override
  public String toString() {
    return "FLOAT";
  }
}
