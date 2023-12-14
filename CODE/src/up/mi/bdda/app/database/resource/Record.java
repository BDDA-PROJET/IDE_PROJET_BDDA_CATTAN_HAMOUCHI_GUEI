package up.mi.bdda.app.database.resource;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Record {
  private TableInfo resource;
  private Map<String, Field> values;
  private int size;

  public Record(TableInfo resource) {
    this.resource = resource;
    values = new HashMap<>();
    size = 0;
  }

  public void addValues(Object... values) {
    Collection<Object> valuesCollection = new ArrayList<>(List.of(values));
    // check if the values respect the schema of the resource
    if (!resource.schema().validate(valuesCollection)) {
      throw new IllegalArgumentException(String.format("Invalid values for resource: %s", resource.name()));
    }
    // the values are valid, so clear the current values of the record
    this.values.clear();

    // create record fields based on resource schema
    Iterator<Object> valuesIterator = valuesCollection.iterator();
    Iterator<ColumnInfo> schemaIterator = resource.schema().iterator();
    while (valuesIterator.hasNext() && schemaIterator.hasNext()) {
      // get the column and the value associated with it
      ColumnInfo column = schemaIterator.next();
      Object value = valuesIterator.next();
      // create a new field with the column type and the value
      Field field = new Field(column.type(), value);
      // add the field to the record
      this.values.putIfAbsent(column.name(), field);
      // update the size of the record
      size += field.size();
    }
    size += (resource.schema().size() + 1) * 4;
  }

  public interface Consumer<T> {
    void accept(T t);
  }

  private int applyOffsetToEachField(int initialOffset, Consumer<Integer> consumer) {
    // the offset of the first value
    int offset = initialOffset + (resource.schema().size() + 1) * 4;
    // iterate over the schema
    Iterator<ColumnInfo> schemaIterator = resource.schema().iterator();
    while (schemaIterator.hasNext()) {
      // call the consumer with the offset
      consumer.accept(offset);

      // there is a column, so get associated field
      ColumnInfo column = schemaIterator.next();
      if (values.size() == resource.schema().size()) {
        Field field = values.get(column.name());
        // increment the offset
        offset += field.size();
      }
    }
    // call the consumer with the last offset
    consumer.accept(offset);

    // return current position in the buffer
    return offset;
  }

  public int write(ByteBuffer buff, int position) {
    // set buffer position
    buff.position(position);
    // apply the offset to each field of the record and get the number of bytes
    // written
    int writeOffset = applyOffsetToEachField(position, buff::putInt);

    // iterate over the schema and offsets
    Iterator<ColumnInfo> schemaIterator = resource.schema().iterator();
    while (schemaIterator.hasNext()) {
      ColumnInfo column = schemaIterator.next();
      Field field = values.get(column.name());
      switch (column.type().name()) {
        case "INT":
          buff.putInt((int) field.value());
          break;
        case "FLOAT":
          buff.putFloat((float) field.value());
          break;
        case "STRING":
        case "VARSTRING":
          for (int i = 0; i < field.size(); i += 2) {
            buff.putChar(field.value().toString().charAt(i / 2));
          }
          break;
        default:
          throw new IllegalArgumentException(
              String.format("Invalid type %s for column %s", column.type().name(), column.name()));
      }
    }
    // return the number of bytes written
    return writeOffset;
  }

  public int read(ByteBuffer buff, int position) {
    // set buffer position
    buff.position(position);
    List<Integer> offsets = new ArrayList<>();
    // apply the offset to each field of the record and get the number of bytes read
    int readOffset = applyOffsetToEachField(position, (offset) -> offsets.add(buff.getInt()));

    // reset the values
    values.clear();

    // initialize the next offset to null
    Integer nextOffset = null;
    // iterate over the schema and offsets
    Iterator<ColumnInfo> schemaIterator = resource.schema().iterator();
    Iterator<Integer> offsetsIterator = offsets.iterator();
    while (schemaIterator.hasNext() && offsetsIterator.hasNext()) {
      // there is a column, and an offset, so use them to read the field value from
      // the buffer
      ColumnInfo column = schemaIterator.next();
      Integer offset = nextOffset != null ? nextOffset : offsetsIterator.next();
      switch (column.type().name()) {
        case "INT":
          // add the field to the record
          values.putIfAbsent(column.name(), new Field(column.type(), buff.getInt(offset)));
          nextOffset = null;
          break;
        case "FLOAT":
          // add the field to the record
          values.putIfAbsent(column.name(), new Field(column.type(), buff.getFloat(offset)));
          nextOffset = null;
          break;
        case "STRING":
        case "VARSTRING":
          // get the next offset
          nextOffset = offsetsIterator.next();
          // create a string builder
          StringBuilder sb = new StringBuilder();
          // iterate over the characters
          for (int i = 0; offset < (nextOffset - i); i += 2) {
            // append the character to the string builder
            sb.append(buff.getChar(offset + i));
          }
          // add the field to the record
          values.putIfAbsent(column.name(), new Field(column.type(), sb.toString().trim()));
          break;
        default:
          throw new IllegalArgumentException(
              String.format("Invalid type %s for column %s", column.type().name(), column.name()));
      }
    }
    // return the number of bytes read
    return readOffset;
  }

  public int size() {
    // the size of the record is number of bytes read or written from the buffer
    return size;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // sb.append("Record(");
    Iterator<ColumnInfo> schemaIterator = resource.schema().iterator();
    while (schemaIterator.hasNext()) {
      ColumnInfo column = schemaIterator.next();
      Field field = values.get(column.name());
      sb.append(String.format("%s=%s", column.name(), field.value()));
      if (schemaIterator.hasNext()) {
        sb.append(" ; ");
      }
    }
    // sb.append(")");
    return sb.toString();
  }

  public TableInfo getResource() {
    return resource;
  }

  public static Record of(Schema userSchema, Object[] values) {
    Record record = new Record(userSchema.resource());
    record.addValues(values);
    return record;
  }

  public Object get(String field) {
    return values.get(field).value();
  }
}
