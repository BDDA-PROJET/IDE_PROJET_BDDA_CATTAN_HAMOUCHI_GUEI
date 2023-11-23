package up.mi.bdda.hcg.main.page;

import java.util.ArrayList;
import java.util.List;

import up.mi.bdda.hcg.main.DBParams;

public class Slot {
  private int offsetFreeSpace;
  private int nbCells;
  private List<Integer> cellsDirectory;
  private static int RES = DBParams.SGBDPageSize;
  private int id;
  private static int nbslot;


  public Slot(int offsetFreeSpace, int nbCells) {
    this.offsetFreeSpace = offsetFreeSpace;
    this.nbCells = nbCells;
    this.cellsDirectory = new ArrayList<Integer>();
    id +=nbslot;
    nbslot +=1;
  }

  public int getOffsetFreeSpace() {
      return offsetFreeSpace;
  }

  public List<Integer> getCellsDirectory() {
      return cellsDirectory;
  }

  public int setOffsetFreeSpace(int size) {
      RES -= size - 2*4 - (nbCells +1)+2*4;
      this.offsetFreeSpace += size;
      return RES;
    }

    public int getId() {
        return id;
    }
}
