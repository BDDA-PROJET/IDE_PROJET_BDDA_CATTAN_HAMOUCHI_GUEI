package up.mi.bdda.app.database.resource;

import up.mi.bdda.app.database.types.Type;

public class Field {
  private final int size;
  private final Object value;

  public Field(Type type, Object value) throws IllegalArgumentException {
    if (type.name().equals("STRING")) {
      this.value = zFill((String) value, type.size() / 2);
    } else {
      this.value = value;
    }

    if (type.name().equals("VARSTRING")) {
      int width = ((String) value).length();
      size = width < type.size() / 2 ? width * 2 : type.size();
    } else {
      size = type.size();
    }
  }

  public Object value() {
    return value;
  }

  public int size() {
    return size;
  }

  private String zFill(String str, int width) {
    if (str.length() >= width) {
      return str;
    }
    String flag = "%1$" + width; // fill Left. {PS: use `-width` to fill right}

    return String.format(flag.concat("s"), str)
        .replace(" ", "*");
  }
}
