package up.mi.bdda.app.database.resource;

import up.mi.bdda.app.database.types.DataType;

/**
 * The DataElement class represents a single data element in the database.
 * It contains the content of the data and its length.
 */
public class DataElement {
  /**
   * The length of the data element.
   */
  public final int length;

  /**
   * The content of the data element.
   */
  public final Object content;

  /**
   * Constructs a new DataElement with the specified type and content.
   * If the type is STRING, the content is filled with zeros until it reaches the
   * specified size.
   * If the type is VARSTRING, the length is either the length of the content or
   * the specified size, whichever is smaller.
   *
   * @param type    the type of the data element
   * @param content the content of the data element
   * @throws IllegalArgumentException if the type is not recognized
   */
  public DataElement(DataType type, Object content) throws IllegalArgumentException {
    if (type.name().equals("STRING")) {
      this.content = zFill((String) content, type.size() / 2);
    } else {
      this.content = content;
    }

    if (type.name().equals("VARSTRING")) {
      int width = ((String) content).length();
      length = width < type.size() / 2 ? width * 2 : type.size();
    } else {
      length = type.size();
    }
  }

  /**
   * Fills the specified string with zeros until it reaches the specified width.
   * If the string is already longer than the specified width, it is returned as
   * is.
   *
   * @param str   the string to fill
   * @param width the desired width of the string
   * @return the filled string
   */
  private String zFill(String str, int width) {
    if (str.length() >= width) {
      return str;
    }
    String flag = "%1$" + width; // fill Left. {PS: use `-width` to fill right}

    return String.format(flag.concat("s"), str)
        .replace(" ", " ");
  }
}