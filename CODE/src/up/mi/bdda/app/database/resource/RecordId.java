package up.mi.bdda.app.database.resource;

import java.util.Objects;

import up.mi.bdda.app.page.PageId;

public record RecordId(PageId pageId, int slotIdx) {
  public RecordId {
    Objects.requireNonNull(pageId);
    if (slotIdx < 0) {
      throw new IllegalArgumentException("Slot index must be positive");
    }
  }
}
