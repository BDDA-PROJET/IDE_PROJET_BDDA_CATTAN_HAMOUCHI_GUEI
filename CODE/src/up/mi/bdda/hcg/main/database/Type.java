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
   * Correspondant à un type char sur 2 octets
   */
  CHAR(2),
  /**
   * Correspondant à une chaîne de caractères de taille (nombre de caractères)
   * exactement T
   */
  STRING(null),
  /**
   * Correspondant à une chaîne de caractères de taille variable, mais dont la
   * taille maximale (nombre maximal de caractères) est T
   */
  VARSTRING(null);

  /** La taille d'un type de donné. */
  private int size;

  /**
   * This is how a constructor is invoked for enum types.
   * 
   * @param size the enum `types` size field
   */
  private Type(Integer size) {
    this.size = size;
  }

  /**
   * 
   * @param value
   * @return
   */
  public Type size(int value) {
    size = value;
    return this;
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