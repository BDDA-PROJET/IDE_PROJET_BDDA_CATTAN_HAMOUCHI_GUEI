package up.mi.bdda.hcg.main.database;

import java.nio.ByteBuffer;

public class Record {
  TableInfo tabInfo;
  Field[] recvalues;

  public Record(TableInfo tabInfo) {
    this.tabInfo = tabInfo;
    recvalues = new Field[2];
  }

  // TODO
  public int writeToBuffer(ByteBuffer buff, int pos) {
    int p = pos;
    for (Field field : recvalues) {
      if (field.type() == Type.VARSTRING) {

      } else {
        p += field.type().size();
      }
    }
    return 0;
  }

  // TODO
  public int readFromBuffer(ByteBuffer buff, int pos) {
    return 0;
  }
}
