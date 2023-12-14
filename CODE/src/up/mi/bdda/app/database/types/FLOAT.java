package up.mi.bdda.app.database.types;

public class FLOAT implements Type {
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
    return value instanceof Float || value instanceof String && ((String) value).matches("-?\\d+\\.\\d+");
  }

  @Override
  public Object parse(String value) {
    return Float.parseFloat(value);
  }
}
