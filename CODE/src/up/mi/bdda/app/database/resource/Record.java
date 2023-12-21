package up.mi.bdda.app.database.resource;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The Record class represents a single record in a database table.
 * It contains information about the table it belongs to, its values, and its
 * size.
 */
public class Record {

  /**
   * Information about the table this record belongs to.
   */
  private TableInfo resource;

  /**
   * The values of this record, mapped by field name.
   */
  private Map<String, DataElement> values;

  /**
   * The size of this record in bytes.
   */
  private int size;

  /**
   * The unique identifier of this record.
   */
  private RecordId recordId;

  /**
   * Constructs a new Record for the given table.
   *
   * @param resource Information about the table this record belongs to.
   */
  public Record(TableInfo resource) {
    this.resource = resource;
    values = new HashMap<>();
    size = 0;
    recordId = null;
  }

  /**
   * Adds values to this record.
   *
   * @param values The values to add.
   * @throws IllegalArgumentException If the values are invalid for the table
   *                                  scheme.
   */
  public void addValues(Object... values) {
    Collection<Object> valuesCollection = new ArrayList<>(List.of(values));
    if (!resource.scheme().validateValues(valuesCollection)) {
      throw new IllegalArgumentException(String.format("Invalid values for resource: %s", resource.name()));
    }
    this.values.clear();

    Iterator<Object> valuesIterator = valuesCollection.iterator();
    Iterator<FieldInfo> schemeIterator = resource.scheme().fields.iterator();
    while (valuesIterator.hasNext() && schemeIterator.hasNext()) {
      FieldInfo field = schemeIterator.next();
      Object value = valuesIterator.next();
      DataElement dataElement = new DataElement(field.type, value);
      this.values.putIfAbsent(field.name, dataElement);
      size += dataElement.length;
    }
    size += (resource.scheme().fields.size() + 1) * 4;
  }

  /**
   * Applies a consumer function to each field of this record, with the field's
   * offset as argument.
   *
   * @param initialOffset The initial offset.
   * @param consumer      The consumer function to apply.
   * @return The final offset after applying the consumer function to all fields.
   */
  private int applyOffsetToEachField(int initialOffset, Consumer<Integer> consumer) {
    int offset = initialOffset + (resource.scheme().fields.size() + 1) * 4;
    Iterator<FieldInfo> schemeIterator = resource.scheme().fields.iterator();
    while (schemeIterator.hasNext()) {
      consumer.accept(offset);

      FieldInfo field = schemeIterator.next();
      if (values.size() == resource.scheme().fields.size()) {
        DataElement dataElement = values.get(field.name);
        offset += dataElement.length;
      }
    }
    consumer.accept(offset);
    return offset;
  }

  /**
   * Writes the data of this record to a ByteBuffer.
   *
   * @param buff     The ByteBuffer to write to.
   * @param position The position in the ByteBuffer to start writing at.
   * @return The offset in the ByteBuffer after writing.
   */
  public int writeDataToBuffer(ByteBuffer buff, int position) {
    buff.position(position);
    int writeOffset = applyOffsetToEachField(position, buff::putInt);
    Iterator<FieldInfo> schemeIterator = resource.scheme().fields.iterator();
    while (schemeIterator.hasNext()) {
      FieldInfo field = schemeIterator.next();
      DataElement dataElement = values.get(field.name);
      switch (field.type.name()) {
        case "INT":
          buff.putInt((int) dataElement.content);
          break;
        case "FLOAT":
          buff.putFloat((float) dataElement.content);
          break;
        case "STRING":
        case "VARSTRING":
          for (int i = 0; i < dataElement.length; i += 2) {
            buff.putChar(dataElement.content.toString().charAt(i / 2));
          }
          break;
        default:
          throw new IllegalArgumentException(
              String.format("Invalid type %s for field %s", field.type.name(), field.name));
      }
    }
    return writeOffset;
  }

  /**
   * Reads the data of this record from a ByteBuffer.
   *
   * @param buff     The ByteBuffer to read from.
   * @param position The position in the ByteBuffer to start reading at.
   * @return The offset in the ByteBuffer after reading.
   */
  public int readDataFromBuffer(ByteBuffer buff, int position) {
    buff.position(position);
    List<Integer> offsets = new ArrayList<>();
    int readOffset = applyOffsetToEachField(position, (offset) -> offsets.add(buff.getInt()));

    values.clear();

    Integer nextOffset = null;
    Iterator<FieldInfo> schemeIterator = resource.scheme().fields.iterator();
    Iterator<Integer> offsetsIterator = offsets.iterator();
    while (schemeIterator.hasNext() && offsetsIterator.hasNext()) {
      FieldInfo field = schemeIterator.next();
      Integer offset = nextOffset != null ? nextOffset : offsetsIterator.next();
      switch (field.type.name()) {
        case "INT":
          values.putIfAbsent(field.name, new DataElement(field.type, buff.getInt(offset)));
          nextOffset = null;
          break;
        case "FLOAT":
          values.putIfAbsent(field.name, new DataElement(field.type, buff.getFloat(offset)));
          nextOffset = null;
          break;
        case "STRING":
        case "VARSTRING":
          nextOffset = offsetsIterator.next();
          StringBuilder sb = new StringBuilder();
          for (int i = 0; offset < (nextOffset - i); i += 2) {
            sb.append(buff.getChar(offset + i));
          }
          values.putIfAbsent(field.name, new DataElement(field.type, sb.toString()));
          break;
        default:
          throw new IllegalArgumentException(
              String.format("Invalid type %s for field %s", field.type.name(), field.name));
      }
    }
    return readOffset;
  }

  /**
   * Returns the size of this record in bytes.
   *
   * @return The size of this record in bytes.
   */
  public int size() {
    return size;
  }

  /**
   * Returns information about the table this record belongs to.
   *
   * @return Information about the table this record belongs to.
   */
  public TableInfo resource() {
    return resource;
  }

  /**
   * Creates a new Record with the given values for the given table scheme.
   *
   * @param resourceScheme The table scheme to create the record for.
   * @param values         The values for the new record.
   * @return The new Record.
   */
  public static Record of(Scheme resourceScheme, Object[] values) {
    Record record = new Record(resourceScheme.resource);
    record.addValues(values);
    return record;
  }

  /**
   * Returns the names of the fields in this record.
   *
   * @return The names of the fields in this record.
   */
  public Set<String> getFields() {
    return values.keySet();
  }

  /**
   * Returns the value of the given field in this record.
   *
   * @param fieldName The name of the field to get the value of.
   * @return The value of the given field in this record.
   */
  public Object getDataElement(String fieldName) {
    return values.get(fieldName).content;
  }

  /**
   * Returns the unique identifier of this record.
   *
   * @return The unique identifier of this record.
   */
  public RecordId getRecordId() {
    return recordId;
  }

  /**
   * Sets the unique identifier of this record.
   *
   * @param recordId The unique identifier to set.
   */
  public void setRecordId(RecordId recordId) {
    this.recordId = recordId;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator<FieldInfo> schemeIterator = resource.scheme().fields.iterator();
    while (schemeIterator.hasNext()) {
      FieldInfo field = schemeIterator.next();
      DataElement dataElement = values.get(field.name);
      Object value = dataElement.content instanceof String ? ((String) dataElement.content).trim()
          : dataElement.content;
      sb.append(String.format("%s=%s", field.name, value));
      if (schemeIterator.hasNext()) {
        sb.append(" ; ");
      }
    }
    return sb.toString();
  }
}
