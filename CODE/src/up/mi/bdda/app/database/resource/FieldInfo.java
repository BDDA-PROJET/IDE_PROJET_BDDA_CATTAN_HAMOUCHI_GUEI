package up.mi.bdda.app.database.resource;

import java.io.Serializable;

import up.mi.bdda.app.database.types.DataType;

/**
 * The FieldInfo class represents the information of a field in a database.
 * This class implements Serializable, allowing instances of this class to be
 * converted into a byte stream.
 */
public class FieldInfo implements Serializable {

  /**
   * The name of the field.
   */
  public final String name;

  /**
   * The type of the field.
   */
  public final DataType type;

  /**
   * Constructor for the FieldInfo class.
   * 
   * @param name The name of the field.
   * @param type The type of the field.
   */
  public FieldInfo(String name, DataType type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String toString() {
    return name + ":" + type.name();
  }
}