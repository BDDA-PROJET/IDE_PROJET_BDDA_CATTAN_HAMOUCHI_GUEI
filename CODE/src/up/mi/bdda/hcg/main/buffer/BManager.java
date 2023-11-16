package up.mi.bdda.hcg.main.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.page.PageId;

/**
 * Cette classe implémente l'interface `BufferManager` et fournit des
 * fonctionnalités de gestion des tampons dans un système de base de données.
 */
public final class BManager implements BufferManager {
  /**
   * La liste des frames.
   * <p>
   * Elle stocke les frames utilisées par le `BufferManager`.
   */
  private final Set<Frame> frameList;

  /**
   * La file d'attente de frames libres.
   * <p>
   * Elle stocke les frames qui sont actuellement libres et disponibles.
   */
  private final Deque<Frame> freeFrames;

  /**
   * La liste des accès à une page.
   * <p>
   * Elle stocke les identifiants des pages auxquelles le `BufferManager` a
   * accédé.
   */
  private final List<PageId> pageAccessList;

  /** L'unique instance de la classe `BManager`. */
  public static final BufferManager gSingleton = new BManager();

  /**
   * Initialise la classe `BManager`.
   */
  private BManager() {
    freeFrames = new LinkedList<>();
    pageAccessList = new ArrayList<>();
    frameList = new HashSet<>(DBParams.frameCount);

    for (int i = 0; i < DBParams.frameCount; i++) {
      Frame frame = new Frame();
      frameList.add(frame);
      freeFrames.add(frame);
    }
  }

  /**
   * Récupère le buffer pour une frame et un identifiant de page donnés.
   * 
   * @param frame  la frame dont le buffer doit être récupéré.
   * @param pageId l'identifiant de la page dont le buffer doit être récupéré.
   * @return le buffer correspondant à la frame et à l'identifiant de page
   *         spécifiés.
   */
  private ByteBuffer getBuffer(Frame frame, PageId pageId) {
    frame.getPageId().set(pageId.getFileIdx(), pageId.getPageIdx());
    DiskManager.getSingleton().readPage(pageId, frame.getBuffer());
    frame.incrementPinCount();
    pageAccessList.add(pageId);
    return frame.getBuffer();
  }

  /**
   * Récupère une frame depuis la liste des frames en fonction d'un prédicat
   * donné.
   * 
   * @param predicate le prédicat utilisé pour filtrer les frames
   * @return la première frame qui satisfait le prédicat, ou null si aucune frame
   *         n'est trouvée
   */
  private Frame getFrame(Predicate<? super Frame> predicate) {
    return frameList.stream()
        .filter(predicate)
        .findFirst()
        .orElse(null);
  }

  /**
   * Renvoie le nombre de fois qu'une page donnée a été consultée.
   * 
   * @param pageId l'identifiant de la page à comptabiliser les accès
   * @return le nombre d'accès à la page
   */
  public int getCountAccess(PageId pageId) {
    return (int) pageAccessList.stream().filter((pageId::equals)).count();
  }

  @Override
  public ByteBuffer getPage(PageId pageId) {
    try {
      return getBuffer(freeFrames.remove(), pageId);
    } catch (NoSuchElementException nsee) {
      Frame frame = getFrame(item -> item.getPageId().equals(pageId));

      if (frame == null) {
        frame = getFrame(item -> item.getPinCount() == 0);

        if (frame == null) {
          nsee.printStackTrace(System.err);
        }
      }

      return getBuffer(frame, pageId);
    }
  }

  @Override
  public void freePage(PageId pageId, boolean dirty) {
    for (Frame frame : frameList) {
      if (frame.getPageId().equals(pageId)) {
        if (dirty) {
          DiskManager.getSingleton().writePage(pageId, frame.getBuffer());
          frame.setFlagDirty(false);
        }

        frame.decrementPinCount();
      }
    }
  }

  @Override
  public void flushAll() {
    for (Frame frame : frameList) {
      if (frame.isDirty()) {
        DiskManager.getSingleton().writePage(frame.getPageId(), frame.getBuffer());
      }

      frame.reset();
      freeFrames.add(frame);
    }
  }

  /**
   * Retourne l'unique instance de la classe `BManager`.
   * 
   * @return l'unique instance de la classe `BManager`
   */
  public static BufferManager getSingleton() {
    return gSingleton;
  }
}
