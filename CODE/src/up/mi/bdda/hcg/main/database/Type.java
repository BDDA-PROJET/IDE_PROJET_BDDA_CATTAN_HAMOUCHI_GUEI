package up.mi.bdda.hcg.main.database;

public enum Type {
  /**
   * Correspondant à un type entier sur 4 octets
   */
  INT(4),
  /**
   * Correspondant à un type float sur 4 octets
   */
  FLOAT(4),
  /**
   * Correspondant à une chaîne de caractères de taille (nombre de caractères)
   * exactement T
   */
  STRING(4),
  /**
   * Correspondant à une chaîne de caractères de taille variable, mais dont la
   * taille maximale (nombre maximal de caractères) est T
   */
  VARSTRING(20);

  /** La taille d'un type de donné. */
  private final int size;

  /**
   * This is how a constructor is invoked for enum types.
   * 
   * @param size the enum `types` size field
   */
  private Type(int size) {
    this.size = size;
  }

  /**
   * Renvoie le champs `taille` de l'enum types.
   * 
   * @return la taille d'un type
   */
  public int size() {
    return size;
  }

}