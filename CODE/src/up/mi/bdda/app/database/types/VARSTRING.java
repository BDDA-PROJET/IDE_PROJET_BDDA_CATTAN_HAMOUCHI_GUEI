package up.mi.bdda.app.database.types;

/**
 * VARSTRING class represents the variable string data type in the database.
 * It implements the DataType interface.
 * The size of the string is defined at the time of object creation.
 */
public class VARSTRING implements DataType {

  /**
   * The size of the VARSTRING.
   */
  private int size;

  /**
   * Constructor for the VARSTRING class.
   * 
   * @param size The maximum size of the string.
   */
  public VARSTRING(int size) {
    this.size = size;
  }

  @Override
  public String name() {
    return "VARSTRING";
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
    if (!(obj instanceof VARSTRING)) {
      return false;
    }
    VARSTRING other = (VARSTRING) obj;
    return other.size == size;
  }

  @Override
  public String toString() {
    return String.format("VARSTRING(%d)", size);
  }
}
