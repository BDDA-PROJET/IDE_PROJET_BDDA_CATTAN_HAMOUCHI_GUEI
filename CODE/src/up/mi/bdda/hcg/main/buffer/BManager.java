package up.mi.bdda.hcg.main.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import up.mi.bdda.hcg.api.BufferManager;
import up.mi.bdda.hcg.main.DBParams;
import up.mi.bdda.hcg.main.disk.PageId;

public class BManager implements BufferManager {
  // Liste des frame
  private List<Frame> frameList;
  // list des frame vide disponible
  private Queue<Frame> freeFrames;
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

  @Override
  public ByteBuffer getPage(PageId pageId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPage'");
  }

  @Override
  public void freePage(PageId pageId, boolean valdirty) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'freePage'");
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
