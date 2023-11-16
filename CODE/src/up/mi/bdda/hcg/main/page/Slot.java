package up.mi.bdda.hcg.main.page;

public class Slot {
  private int offsetFreeSpace;
  private int nbCells;
  private int[] cellsDirectory;

  public Slot(int offsetFreeSpace, int nbCells) {
    this.offsetFreeSpace = offsetFreeSpace;
    this.nbCells = nbCells;
    this.cellsDirectory = new int[2 * nbCells];
  }
}
