package up.mi.bdda.hcg.main.buffer;

import java.nio.ByteBuffer;

import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.disk.PageId;

/**
 * Cette classe représente une frame dans un buffer. Elle contient des méthodes
 * de manipulation de la frame, telles que l'incrémentation/décrémentation du
 * {@code pinCount}, l'activation/la désactivation du {@code flagDirty} et
 * l'obtention du buffer, de l'identifiant d'une page, du pin count et flag
 * dirty.
 */
public class Frame {
  /** Le buffer de la frame. */
  private final ByteBuffer buff;
  /** L'identifiant de page associé à la frame. */
  private final PageId pageId;
  /** Le nombre d'accès à la frame. */
  private int pinCount;
  /** Indique si la frame a modifiée un fichier ou non. */
  private boolean flagDirty;

  /**
   * Il s'agit du constructeur d'une {@code Frame}.
   */
  public Frame() {
    buff = ByteBuffer.allocate(DBParams.SGBDPageSize);
    pageId = new PageId();
    pinCount = 0;
    flagDirty = false;
  }

  /**
   * Incrémente de 1 le pin count de la frame.
   */
  public void incrementPinCount() {
    pinCount += 1;
  }

  /**
   * Décrémente de 1 le pin count de la frame.
   */
  public void decrementPinCount() {
    pinCount -= 1;
  }

  /**
   * Définir le flag dirty de la frame.
   * 
   * @param flagDirty une valeur booléenne indiquant si la frame a modifié un
   *                  fichier ou non
   */
  public void setFlagDirty(boolean flagDirty) {
    this.flagDirty = flagDirty;
  }

  /**
   * Réinitialise la frame.
   */
  public void reset() {
    buff.clear();
    pageId.reset();
    setFlagDirty(false);
    pinCount = 0;
  }

  /**
   * Récupère le buffer de la frame.
   * 
   * @return le buffer de la frame
   */
  public ByteBuffer getBuffer() {
    return buff;
  }

  /**
   * Récupère l'identifiant de page de la frame.
   * 
   * @return l'identifiant de page de la frame
   */
  public PageId getPageId() {
    return pageId;
  }

  /**
   * Récupère le pin count de page de la frame.
   * 
   * @return le pin count de page de la frame
   */
  public int getPinCount() {
    return pinCount;
  }

  /**
   * Vérifie si la frame a modifié un fichier ou non.
   * 
   * @return une valeur booléenne indiquant si la frame a modifié un fichier ou
   *         non.
   */
  public boolean isDirty() {
    return flagDirty;
  }
}
