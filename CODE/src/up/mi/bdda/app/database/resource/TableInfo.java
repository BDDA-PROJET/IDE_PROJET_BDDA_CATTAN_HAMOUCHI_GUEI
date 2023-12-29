package up.mi.bdda.app.database.resource;

import java.io.Serializable;
import up.mi.bdda.app.page.PageId;

/**
 * The TableInfo class represents the metadata of a table in a database.
 * It includes the table name, the ID of the header page, and the schema of the
 * table.
 */
public class TableInfo implements Serializable {

  /**
   * The ID of the header page of the table.
   */
  private final PageId headerPageId;

  /**
   * The name of the table.
   */
  private String name;

  /**
   * The schema of the table.
   */
  private Scheme scheme;

  /**
   * Constructs a new TableInfo object.
   *
   * @param name         The name of the table.
   * @param headerPageId The ID of the header page of the table.
   */
  public TableInfo(String name, PageId headerPageId) {
    this.name = name;
    this.headerPageId = headerPageId;
  }

  /**
   * Returns the name of the table.
   *
   * @return The name of the table.
   */
  public String name() {
    return name;
  }

  /**
   * Returns the schema of the table.
   *
   * @return The schema of the table.
   */
  public Scheme scheme() {
    return scheme;
  }

  /**
   * Returns the ID of the header page of the table.
   *
   * @return The ID of the header page of the table.
   */
  public PageId getHeaderPageId() {
    return headerPageId;
  }

  /**
   * Sets the schema of the table.
   *
   * @param scheme The new schema of the table.
   */
  public void setScheme(Scheme scheme) {
    this.scheme = scheme;
  }

  public String toString() {
    return String.format("TableInfo: name=%s, headerPageId=%s, scheme=%s", name, headerPageId, scheme);
  }
}