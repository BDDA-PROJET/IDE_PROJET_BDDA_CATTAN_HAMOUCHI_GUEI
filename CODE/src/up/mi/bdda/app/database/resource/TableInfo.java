package up.mi.bdda.app.database.resource;

import java.io.Serializable;

import up.mi.bdda.app.page.PageId;

public class TableInfo implements Serializable {
  private final String name;
  private Schema schema;
  private final PageId headerPageId;

  public TableInfo(String name, PageId headerPageId) {
    this.name = name;
    this.headerPageId = headerPageId;
  }

  public String name() {
    return name;
  }

  public Schema schema() {
    return schema;
  }

  public PageId getHeaderPageId() {
    return headerPageId;
  }

  public void setSchema(Schema schema) {
    this.schema = schema;
  }
}
