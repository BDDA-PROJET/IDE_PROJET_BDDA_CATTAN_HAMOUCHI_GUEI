package up.mi.bdda.app.database.types;

public interface Type {
  String name();

  int size();

  boolean isInstance(Object value);

  Object parse(String value);

  static Type of(String name) {
    // extract size from name (e.g. TYPENAME(10))
    if (name.contains("INT")) {
      return new INT();
    } else if (name.contains("FLOAT")) {
      return new FLOAT();
    } else if (name.contains("VARSTRING")) {
      int size = Integer.parseInt(name.substring(name.indexOf('(') + 1, name.indexOf(')')));
      return new VARSTRING(size);
    } else if (name.contains("STRING")) {
      int size = Integer.parseInt(name.substring(name.indexOf('(') + 1, name.indexOf(')')));
      return new STRING(size);
    } else {
      return null;
    }
  }
}