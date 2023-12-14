package up.mi.bdda.app.database.types;

public class INT implements Type {
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
}
