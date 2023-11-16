package up.mi.bdda.hcg.api;

import java.nio.ByteBuffer;

import up.mi.bdda.hcg.main.disk.DManager;
import up.mi.bdda.hcg.main.page.PageId;

/**
 * Cette API permet de gerer la memiore du disque en manipulant la couche bas
 * niveau. Elle regroupe des methodes qui seront appelées par les couches plus
 * hautes!
 * <p>
 * Elle fournit des méthodes pour {@code allouer}, {@code lire}, {@code écrire}
 * et {@code désallouer} des pages dans la mémoire du disque. Elle fournit
 * également une méthode pour récupérer le nombre actuel de pages allouées.
 */
public interface DiskManager {
   /**
    * Cette méthode doit allouer une page, c’est à dire réserver une nouvelle page
    * à la demande d’une des couches au-dessus.
    * 
    * @return l'identifiant de la page allouée.
    */
   PageId allocPage();

   /**
    * Cette méthode doit remplir l’argument buff avec le contenu
    * disque de la page identifiée par l’argument pageId.
    * <p>
    * S'il s’agit d’une page qui « existe déjà »,
    * {@code pas d’allocation dans cette méthode} !
    * 
    * @param pageId l'identifiant de la page à lire
    * @param buff   un buffer pour stocker le contenu de la page
    */
   void readPage(PageId pageId, ByteBuffer buff);

   /**
    * Cette méthode écrit le contenu de l’argument buff dans
    * le fichier et à la position indiqués par l’argument pageId.
    * 
    * @param pageId l'identifiant de la page sur laquelle le contenu doit être
    *               écrit
    * @param buff   un buffer contenant le contenu à écrire
    */
   void writePage(PageId pageId, ByteBuffer buff);

   /**
    * Cette méthode doit désallouer une page, et la rajouter
    * dans la liste des pages « disponibles ».
    * 
    * @param pageId l'identifiant de la page à désallouer
    */
   void deallocPage(PageId pageId);

   /**
    * Cette méthode doit retourner le nombre courant de pages allouées
    * auprès du {@code DiskManager}.
    * <p>
    * Par exemple, après deux appels à {@code AllocPage()} et un appel à
    * {@code DeallocPage()} elle doit retourner 1.
    * 
    * @return le nombre de pages actuellement allouées
    * 
    * @see up.mi.bdda.hcg.api.DiskManager#allocPage()
    * @see up.mi.bdda.hcg.api.DiskManager#deallocPage(PageId)
    */
   int getCurrentCountAllocPages();

   /**
    * Retourne l'unique instance de {@code DiskManager} .
    * 
    * @return une instance de DiskManager
    */
   public static DiskManager getSingleton() {
      return DManager.getSingleton();
   }
}
