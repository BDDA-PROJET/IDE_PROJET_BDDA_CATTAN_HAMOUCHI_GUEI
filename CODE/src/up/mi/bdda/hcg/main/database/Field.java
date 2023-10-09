package up.mi.bdda.hcg.main.database;

import java.util.Objects;

public class Field {
  private Object value;
  private Type type;
  private int size;

  /**
   * 
   * @param value
   * @param type
   */
  public Field(Object value, Type type) {
    Objects.requireNonNull(value);
    Objects.requireNonNull(type);

    this.type = type;
    this.value = value;

    switch (type().name()) {
      case "INT": {
        checkType(value(), Integer.class);
        break;
      }
      case "FLOAT": {
        checkType(value(), Float.class);
        break;
      }
      case "STRING": {
        checkType(value(), String.class);
        String str = value().toString();
        int size = type().size();
        this.value = (size == str.length()) ? str : zFill(str, size);
        break;
      }
      case "VARSTRING": {
        checkType(value(), String.class);
        break;
      }

      default:
        break;
    }

    this.size = Type.VARSTRING.equals(type()) ? value.toString().length() : type().size();
  }

  /**
   * 
   * @param obj
   * @param c
   */
  private void checkType(Object obj, Class<?> c) {
    assert obj.getClass().equals(c) : "The Object don't match the provided type.";
    if (c.equals(String.class))
      assert obj.toString().length() <= type().size() : "The Object size is bigger than the provided type size.";
  }

  /**
   * Pad a numeric string with zeros on the left, to fill a field of the given
   * width.
   * <p>
   * The string is never truncated.
   * 
   * @param str
   * @param width
   * @return
   */
  private String zFill(String str, int width) {
    if (str.length() >= width)
      return str;

    String flag = "%1$" + width; // fill Left. {PS: use `-width` to fill right}

    return String.format(flag.concat("s"), str)
        .replace(" ", "*");
  }

  /**
   * 
   * @return
   */
  public Object value() {
    return value;
  }

  /**
   * 
   * @return
   */
  public Type type() {
    return type;
  }

  /**
   * 
   * @return
   */
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return value().toString();
  }

}
