package up.mi.bdda.app.database.types;

/**
 * STRING class represents the string data type in the database.
 * It implements the DataType interface.
 * The size of the string is defined at the time of object creation.
 */
public class STRING implements DataType {

  /**
   * The size of the STRING.
   */
  private int size;

  /**
   * Constructor for the STRING class.
   * 
   * @param size The size of the string.
   */
  public STRING(int size) {
    this.size = size;
  }

  @Override
  public String name() {
    return "STRING";
  }

  @Override
  public int size() {
    // 2 bytes per character
    return size * 2;
  }

  @Override
  public boolean isInstance(Object value) {
    if (!(value instanceof String)) {
      return false;
    }
    String str = (String) value;

    return str.length() <= size();
  }

  @Override
  public Object parse(String value) {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof STRING)) {
      return false;
    }
    STRING other = (STRING) obj;
    return other.size == size;
  }

  @Override
  public String toString() {
    return String.format("STRING(%d)", size);
  }
}
