package up.mi.bdda.app.database.types;

public class STRING implements Type {
  private int size;

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
}
