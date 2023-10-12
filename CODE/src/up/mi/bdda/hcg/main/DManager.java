package up.mi.bdda.hcg.main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import up.mi.bdda.hcg.api.DiskManager;

public class DManager implements DiskManager {
	/** la liste des pages « <code>allouées</code> ». */
	private Set<Float> allocIdSet;
	/** la liste des pages « <code>disponibles</code> ». */
	private Queue<Float> deallocIdQueue;
	/** L'identifiant de la prochaine page à allouée. */
	private PageId currentPageId;
	/** L'unique instance du <code>DiskManager</code>. */
	private static DiskManager gSingleton = new DManager();

	private DManager() {
		allocIdSet = new HashSet<>();
		deallocIdQueue = new LinkedList<>();
		currentPageId = new PageId(0, 0);
	}

	private RandomAccessFile getAccessFile(PageId pageId) throws IOException{
		RandomAccessFile file =  new RandomAccessFile(getFilePath(pageId).toString(),"rw");
			file.seek((pageId.getPageIdx())*DBParams.SGBDPageSize);
			return file;

	}

	/**
	 * Retourne le chemin d'un fichier en fonction du pageId.
	 * 
	 * @param pageId le pageId
	 * @return le chemin du fichier
	 */
	private Path getFilePath(PageId pageId) {
		String fileName = "F".concat(String.valueOf(pageId.getFileIdx())).concat(".data");
		Path path = Paths.get(DBParams.DBPath, fileName);

		return path;
	}

	/**
	 * Creation d'un fichier à partir du <code>PageId</code>.
	 */

	private void createFile() {
		Path path = getFilePath(currentPageId);

		try {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		} catch (Exception e) {
			if (!(e instanceof FileAlreadyExistsException))
				e.printStackTrace(System.err);
		}
	}

	/**
	 * Mise à jour du <code>PageId</code> pour la prochaine allocation de page.
	 */
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

		if (!(deallocIdQueue.isEmpty())) {
			float deallocId = deallocIdQueue.poll();
			int fId = (int) deallocId;
			int pId = (int) Math.round((deallocId % 1) * 100);
			copy.set(fId, pId);
		} else {
			next();
		}

		allocIdSet.add(Float.parseFloat(copy.toString()));
		return copy;
	}

	@Override
	public void deallocPage(PageId pageId) {
		float id = Float.parseFloat(pageId.toString());

		if (allocIdSet.remove(id))
			deallocIdQueue.add(id);

		if (allocIdSet.isEmpty()) {
			deallocIdQueue.clear();
			currentPageId.reset();
		}
	}

	@Override
	public int getCurrentCountAllocPages() {
		int count = allocIdSet.size();

		return count;
	}

	@Override
	public void readPage(PageId pageId, ByteBuffer buff) {
		try {
			RandomAccessFile file  =  getAccessFile(pageId);
			file.read(buff.array(), 0, DBParams.SGBDPageSize);
			file.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			
		}
	}
	
	@Override
	public void writePage(PageId pageId, ByteBuffer buff) {
		try {
			RandomAccessFile file  =  getAccessFile(pageId);
			file.write(buff.array(), 0, DBParams.SGBDPageSize);
			file.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	 
	



	/**
	 * Retourne l'unique instance du <code>DiskManager</code>.
	 * 
	 * @return le DiskManager
	 */
	public static DiskManager getSingleton() {
		return gSingleton;
	}

	

	
}
