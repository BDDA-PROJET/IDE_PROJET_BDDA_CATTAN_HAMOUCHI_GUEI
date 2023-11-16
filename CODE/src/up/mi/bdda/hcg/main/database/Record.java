package up.mi.bdda.hcg.main.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import up.mi.bdda.hcg.main.DBParams;

public class Record {
  TableInfo tabInfo;
  List<Field> recvalues;

  public Record(TableInfo tabInfo) {
    this.tabInfo = tabInfo;
    recvalues = new ArrayList<>();
  }

  public void addRecValues(Object[] list) {
    ColInfo[] colInfos = tabInfo.getColInfos();

    recvalues.clear();
    for (int i = 0; i < tabInfo.getNombreColonne(); i += 1) {
      recvalues.add(new Field(list[i], colInfos[i].typeColonne()));
    }
  }

  private int storingRecordOffsetInBuffer(ByteBuffer buff, int pos, Consumer<Integer> consumer) {
    buff.position(pos);

    int nbColonne = tabInfo.getNombreColonne();
    int offset = pos + (nbColonne + 1) * Type.INT.size(); // 1st value offset

    for (int i = 0; i < tabInfo.getNombreColonne() + 1; i += 1) {
      consumer.accept(offset);
      if (i < recvalues.size()) {
        Field field = recvalues.get(i);
        boolean predicate = Type.STRING.equals(field.type()) || Type.VARSTRING.equals(field.type());

        offset += predicate ? Type.CHAR.size() * field.size() : field.size();
      }
    }
    return offset - pos;
  }

  private void writeRecordValue(ByteBuffer buff) {
    for (Field field : recvalues) {
      switch (field.type().name()) {
        case "INT":
          buff.putInt((int) field.value());
          break;
        case "FLOAT":
          buff.putFloat((float) field.value());
          break;
        case "STRING":
        case "VARSTRING":
          for (int i = 0; i < field.size(); i += 1) {
            buff.putChar(field.value().toString().charAt(i));
          }
          break;

        default:
          break;
      }
    }
  }

  private void readRecordValue(ByteBuffer buff, List<Integer> recoffsets, Consumer<Object> consumer) {
    for (int i = 0; i < recoffsets.size() - 1; i += 1) {
      int currentOffset = recoffsets.get(i);

      switch (tabInfo.getColInfos()[i].typeColonne().name()) {
        case "INT": {
          consumer.accept(buff.getInt(currentOffset));
          break;
        }
        case "FLOAT": {
          consumer.accept(buff.getFloat(currentOffset));
          break;
        }
        case "STRING":
        case "VARSTRING": {
          StringBuilder sb = new StringBuilder();
          int nextOffset = i < recoffsets.size() ? recoffsets.get(i + 1) : currentOffset;

          for (int j = 0; currentOffset < (nextOffset - j); j += 2) {
            sb.append(buff.getChar(currentOffset + j));
          }
          consumer.accept(sb.toString().trim());
          break;
        }

        default:
          break;
      }
    }
  }

  public int writeToBuffer(ByteBuffer buff, int pos) {
    if (!(recvalues.isEmpty())) {
      int offset = storingRecordOffsetInBuffer(buff, pos, (currentOffset) -> {
        buff.putInt(currentOffset);
      });

      if (offset > 0) {
        writeRecordValue(buff);
        recvalues.clear();
        return offset;
      }
    }
    return 0;
  }

  public int readFromBuffer(ByteBuffer buff, int pos) {
    List<Integer> recoffsets = new ArrayList<>();
    List<Object> values = new ArrayList<>();

    storingRecordOffsetInBuffer(buff, pos, (e) -> {
      recoffsets.add(buff.getInt());
    });

    int offset = recoffsets.get(recoffsets.size() - 1);

    readRecordValue(buff, recoffsets, (element) -> {
      values.add(element);
    });

    if (offset > 0)
      addRecValues(values.toArray());

    return offset;
  }

  @Override
  public String toString() {
    String str = tabInfo.getRelation().concat("\n");

    if (recvalues.isEmpty())
      return str.concat("[empty]");

    for (int i = 0; i < tabInfo.getNombreColonne(); i += 1) {
      ColInfo colInfo = tabInfo.getColInfos()[i];
      Field field = recvalues.get(i);
      str += colInfo.nomColonnes()
          .concat("(")
          .concat(colInfo.typeColonne().name())
          .concat(")")
          .concat(" -> ")
          .concat(field.value().toString()).concat("\n");
    }

    return str;
  }
}
