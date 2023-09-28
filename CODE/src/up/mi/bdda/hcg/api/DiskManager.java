package up.mi.bdda.hcg.api;

import java.nio.ByteBuffer;

import up.mi.bdda.hcg.main.PageId;

/**
 * <p>
 * Cette API permet de gere la memiore du disque en manipulant la couche bas
 * niveau.
 * <p/>
 * <p>
 * Elle regroupe des methodes qui seront appelées par les couches plus hautes!
 * <p/>
 */
public interface DiskManager {
  /**
   * Cette méthode doit allouer une page, c’est à dire réserver une nouvelle page
   * à la demande d’une des couches au-dessus.
   * 
   * @return une pageId
   */
  PageId allocPage();

  /**
   * <p>
   * Cette méthode doit remplir l’argument buff avec le contenu
   * disque de la page identifiée par l’argument pageId.
   * </p>
   * <p>
   * Il s’agit d’une page qui « existe déjà »,
   * <code>pas d’allocation dans cette méthode !</code>
   * </p>
   * 
   * @param pageId l'identifiant de la page
   * @param buff   un buffer
   */
  void readPage(PageId pageId, ByteBuffer buff);

  /**
   * Cette méthode écrit le contenu de l’argument buff dans
   * le fichier et à la position indiqués par l’argument pageId.
   * 
   * @param pageId l'identifiant de la page
   * @param buff   un buffer
   */
  void writePage(PageId pageId, ByteBuffer buff);

  /**
   * Cette méthode doit désallouer une page, et la rajouter
   * dans la liste des pages « disponibles ».
   * 
   * @param pageId l'identifiant de la page
   */
  void deallocPage(PageId pageId);

  /**
   * <p>
   * Cette méthode doit retourner le nombre courant de pages allouées
   * auprès du <code>DiskManager.</code>
   * </p>
   * <p>
   * Par exemple, après deux appels à <code>AllocPage</code> et un appel
   * à <code>DeallocPage</code> elle doit retourner 1.
   * </p>
   * 
   * @return le nombre courant de pages allouées
   */
  int getCurrentCountAllocPages();
}
