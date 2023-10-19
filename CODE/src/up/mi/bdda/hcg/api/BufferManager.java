package up.mi.bdda.hcg.api;

import java.nio.ByteBuffer;

import up.mi.bdda.hcg.main.buffer.BManager;
import up.mi.bdda.hcg.main.disk.PageId;

public interface BufferManager {
  /**
   * Cette méthode doit retourner un des buffers associés à une frame. Le buffer
   * sera rempli avec le contenu de la page désignée par l’argument pageId.
   * 
   * @param pageId l'identifiant de la page
   * @param buff   un buffer pour stocker le contenu de la page
   */
  ByteBuffer getPage(PageId pageId);

  /**
   * Cette méthode devra décrémenter le {@code pin_count} et actualiser le
   * {@code flag dirty} de la page (et aussi potentiellement actualiser des
   * informations concernant la politique de remplacement).
   * 
   * @param pageId   l'identifiant de la page
   * @param valdirty
   */
  void freePage(PageId pageId, boolean valdirty);

  /**
   * Cette méthode s’occupe de :
   * <ul>
   * <li>l’écriture de toutes les pages dont le {@code flag dirty = 1} sur disque
   * <li>la remise à 0 de tous les flags/informations et contenus des buffers
   * (buffer pool « vide »)
   * 
   */
  void flushAll();

  /**
   * Retourne l'unique instance de {@code BufferManager} .
   * 
   * @return une instance de BufferManager
   */
  public static BufferManager getSingleton() {
    return BManager.getSingleton();
  }
}
