package up.mi.bdda.app.database.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The Scheme class represents the schema of a table in a database.
 * It includes information about the table and its fields.
 */
public class Scheme implements Serializable {
  /**
   * Information about the table that this schema represents.
   */
  public final TableInfo resource;

  /**
   * Collection of fields in the table.
   */
  public final Collection<FieldInfo> fields;

  /**
   * Constructs a new Scheme object.
   *
   * @param resource The table information.
   * @param fields   The fields in the table.
   */
  public Scheme(TableInfo resource, FieldInfo[] fields) {
    this.resource = resource;
    this.fields = List.of(fields);
  }

  /**
   * Factory method to create a new Scheme object.
   *
   * @param resource The table information.
   * @param fields   The fields in the table.
   * @return A new Scheme object.
   */
  public static Scheme of(TableInfo resource, FieldInfo... fields) {
    Scheme resourceSchema = new Scheme(resource, fields);
    resource.setScheme(resourceSchema);
    return resourceSchema;
  }

  /**
   * Validates the values against the schema.
   *
   * @param values The values to validate.
   * @return true if the values match the schema, false otherwise.
   */
  public boolean validateValues(Collection<Object> values) {
    if (values.size() != fields.size()) {
      return false;
    }
    Collection<Object> valueCollection = new ArrayList<>();
    Iterator<Object> valuesIterator = values.iterator();
    Iterator<FieldInfo> schemaIterator = fields.iterator();
    while (valuesIterator.hasNext() && schemaIterator.hasNext()) {
      FieldInfo field = schemaIterator.next();
      Object value = valuesIterator.next();
      if (!field.type.isInstance(value)) {
        return false;
      }
      valueCollection.add(field.type.parse(value.toString()));
    }
    values.clear();
    values.addAll(valueCollection);
    return true;
  }

  /**
   * Retrieves a field by its name.
   *
   * @param fieldName The name of the field.
   * @return The FieldInfo object for the field.
   * @throws IllegalArgumentException if the field does not exist.
   */
  public FieldInfo getField(String fieldName) {
    Iterator<FieldInfo> schemaIterator = fields.iterator();
    while (schemaIterator.hasNext()) {
      FieldInfo field = schemaIterator.next();
      if (field.name.equals(fieldName)) {
        return field;
      }
    }
    throw new IllegalArgumentException(String.format("Field `%s` does not exist in: %s", fieldName, this));
  }

  /**
   * Returns a string representation of the Scheme object.
   *
   * @return A string representation of the Scheme object.
   */
  public String toString() {
    return String.format("Scheme(%s, %s)", resource.name(), fields);
  }

}