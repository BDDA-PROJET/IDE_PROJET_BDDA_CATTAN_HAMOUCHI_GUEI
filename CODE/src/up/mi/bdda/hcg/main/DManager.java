package up.mi.bdda.hcg.main;

import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
// import java.util.LinkedList;
// import java.util.Queue;
import java.util.Set;

import up.mi.bdda.hcg.api.DiskManager;

public class DManager implements DiskManager {
	// private ByteBuffer buffer;
	private Set<Float> allocIdSet;
	// private Queue<Float> deallocIdQueue;
	private PageId currentPageId;
	private static DiskManager gSingleton = new DManager();

	private DManager() {
		// buffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
		allocIdSet = new HashSet<>();
		// deallocIdQueue = new LinkedList<>();
		currentPageId = new PageId(0, 0);
	}

	private void createFile() {
		String fileName = "F".concat(String.valueOf(currentPageId.getFileIdx())).concat(".data");
		Path path = Paths.get(DBParams.DBPath, fileName);

		try {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		} catch (Exception e) {
			if (!(e instanceof FileAlreadyExistsException))
				e.printStackTrace(System.err);
		}
	}

	private void next() {
		int nextFileId = currentPageId.getFileIdx() + 1;
		int nextPageId = currentPageId.getPageIdx();

		if (DBParams.DMFFileCount == nextFileId) {
			nextFileId = 0;
			nextPageId += 1;
		}

		currentPageId.set(nextFileId, nextPageId);
	}

	@Override
	public PageId allocPage() {
		PageId copy = currentPageId.clone();
		createFile();

		// if (!(deallocIdQueue.isEmpty())) {
		// float deallocId = deallocIdQueue.poll();
		// int fId = (int) deallocId;
		// int pId = (int) Math.round((deallocId % 1) * 100);
		// copy.set(fId, pId);
		// } else {
		// next();
		// }

		next();
		allocIdSet.add(Float.parseFloat(copy.toString()));

		System.out.println(allocIdSet);
		return copy;
	}

	// @Override
	// public void deallocPage(PageId pageId) {
	// float id = Float.parseFloat(pageId.toString());

	// if (allocIdSet.remove(id))
	// deallocIdQueue.add(id);

	// if (allocIdSet.isEmpty()) {
	// deallocIdQueue.clear();
	// currentPageId.reset();
	// }
	// }

	@Override
<<<<<<< HEAD
	public void writePage(PageId a, ByteBuffer b) {
		Files.write(a,b);
	}

	@Override
	public void deallocPage(PageId a) {
		// TODO Auto-generated method stub
		pageIdHistory.remove(Float.parseFloat(a.toString()));
=======
	public void deallocPage(PageId pageId) {
		// TODO Auto-generated method stub
>>>>>>> c748a9a (✨ feat(DManager): add allocPage (#2))
	}

	@Override
	public int getCurrentCountAllocPages() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void readPage(PageId pageId, ByteBuffer buff) {
		// TODO Auto-generated method stub
	}

	@Override
	public void writePage(PageId pageId, ByteBuffer buff) {
		// TODO Auto-generated method stub
	}

	public static DiskManager getSingleton() {
		return gSingleton;
	}
}
