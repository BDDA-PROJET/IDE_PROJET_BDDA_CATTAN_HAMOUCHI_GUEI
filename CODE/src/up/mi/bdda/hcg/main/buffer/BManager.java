package up.mi.bdda.hcg.main.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.api.DiskManager;
import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.disk.PageId;

public class BManager implements BufferManager {
  // Liste des frame
  private List<Frame> frameList;
  // list des frame vide disponible
  private Queue<Frame> freeFrames;
  private Map<Float, Frame> frameMap;
  public static final BufferManager gSingleton = new BManager();

  private BManager() {
    freeFrames = new LinkedList<>();
    frameList = new ArrayList<>(DBParams.frameCount);

    for (int i = 0; i < DBParams.frameCount; i++) {
      Frame frame = new Frame();
      frameList.add(frame);
      freeFrames.add(frame);
    }
  }

  private ByteBuffer getBuffer(Frame frame, PageId pageId) {
    DiskManager disk = DiskManager.getSingleton();

    frame.getPageId().set(pageId.getFileIdx(), pageId.getPageIdx());
    disk.readPage(pageId, frame.getBuffer());
    frame.incrementPinCount();
    frameMap.put(Float.parseFloat(pageId.toString()), frame);
    return frame.getBuffer();
  }

  @Override
  public ByteBuffer getPage(PageId pageId) {
    ByteBuffer buff = null;
    Frame frame = null;

    try {
      frame = freeFrames.remove();
      buff = getBuffer(frame, pageId);
    } catch (NoSuchElementException nsee) {
      if (!(frameMap.isEmpty())) {
        frame = frameMap.get(Float.parseFloat(pageId.toString()));

        if (frame == null) {
          for (Frame frameItem : frameList) {
            if (frameItem.getPinCount() == 0) {
              buff = getBuffer(frameItem, pageId);
              break;
            } else {
              nsee.printStackTrace(System.err);
            }
          }
        } else {
          buff = getBuffer(frame, pageId);
        }
      }
    }

    return buff;
  }

  @Override
  public void freePage(PageId pageId, boolean dirty) {
    DiskManager disk = DiskManager.getSingleton();

    for (Frame frame : frameList) {
      if (frame.getPageId().equals(pageId)) {
        if (dirty) {
          disk.writePage(pageId, frame.getBuffer());
          frame.setFlagDirty(false);
        }

        frame.decrementPinCount();
      }
    }
  }

  @Override
  public void flushAll() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'flushAll'");
  }

  public static BufferManager getSingleton() {
    return gSingleton;
  }
}
