package up.mi.bdda.app.database.types;

import java.io.Serializable;

/**
 * DataType class is a factory class that creates and returns instances of
 * different data types.
 * The data types currently supported are INT, FLOAT, VARSTRING, and STRING.
 * Each data type is represented as a separate class.
 */
public interface DataType extends Serializable {

  /**
   * Returns the name of the data type.
   * 
   * @return a String representing the name of the data type.
   */
  String name();

  /**
   * Returns the size of the data type in bytes.
   * 
   * @return an integer representing the size of the data type.
   */
  int size();

  /**
   * Checks if the given value is an instance of the data type.
   * 
   * @param value the object to check.
   * @return a boolean indicating whether the value is an instance of the data
   *         type.
   */
  boolean isInstance(Object value);

  /**
   * Parses the given string value to the data type.
   * 
   * @param value the string to parse.
   * @return an Object representing the parsed value.
   */
  Object parse(String value);

  /**
   * Checks if the given object is equal to the current instance.
   * 
   * @param obj the object to compare with.
   * @return a boolean indicating whether the object is equal to the current
   *         instance.
   */
  boolean equals(Object obj);

  /**
   * This method takes a string representation of a data type and returns an
   * instance of that data type.
   * The string representation can also include a size in parentheses for
   * VARSTRING and STRING types.
   * For example, "VARSTRING(10)" would return a VARSTRING instance with a size of
   * 10.
   *
   * @param name The string representation of the data type.
   * @return An instance of the data type represented by the string, or null if
   *         the string does not represent a known data type.
   */
  static DataType of(String name) {
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