package up.mi.bdda.app.database.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Schema {
  private final TableInfo resource;
  private final Collection<ColumnInfo> columns;

  public Schema(TableInfo resource, ColumnInfo[] columns) {
    this.resource = resource;
    this.columns = List.of(columns);
  }

  public static Schema of(TableInfo resource, ColumnInfo... columns) {
    Schema schema = new Schema(resource, columns);
    resource.setSchema(schema);
    return schema;
  }

  public boolean validate(Collection<Object> values) {
    if (values.size() != columns.size()) {
      return false;
    }
    Collection<Object> valueCollection = new ArrayList<>();
    Iterator<Object> valuesIterator = values.iterator();
    Iterator<ColumnInfo> schemaIterator = columns.iterator();
    while (valuesIterator.hasNext() && schemaIterator.hasNext()) {
      ColumnInfo column = schemaIterator.next();
      Object value = valuesIterator.next();
      if (!column.type().isInstance(value)) {
        return false;
      }
      valueCollection.add(column.type().parse(value.toString()));
    }
    values.clear();
    values.addAll(valueCollection);
    return true;
  }

  public TableInfo resource() {
    return resource;
  }

  public int size() {
    return columns.size();
  }

  public Iterator<ColumnInfo> iterator() {
    return columns.iterator();
  }

  public String toString() {
    return String.format("Schema(%s, %s)", resource.name(), columns);
  }

  public ColumnInfo getColumn(String field) {
    Iterator<ColumnInfo> schemaIterator = columns.iterator();
    while (schemaIterator.hasNext()) {
      ColumnInfo column = schemaIterator.next();
      if (column.name().equals(field)) {
        return column;
      }
    }
    return null;
  }
}
