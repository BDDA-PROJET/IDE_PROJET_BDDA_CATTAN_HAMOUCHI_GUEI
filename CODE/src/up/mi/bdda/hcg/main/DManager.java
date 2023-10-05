package up.mi.bdda.hcg.main;

import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import up.mi.bdda.hcg.api.DiskManager;

public class DManager implements DiskManager {
	private ByteBuffer buffer;
	private Set<Float> pageIdHistory;
	private PageId currentPageId;
	private static DiskManager gSingleton = new DiskManager();

	private DManager() {
		buffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
		pageIdHistory = new HashSet<>();
		currentPageId = new PageId();
	}


	private void createFile() {
		Path path = Paths.get(DBParams.DBPath, "F" + 0 + ".data");

		try {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		} catch (Exception e) {
			if (e instanceof FileAlreadyExistsException)
				System.out.println("Already exists: ".concat(e.getMessage()));
			else
				System.out.println(e.getMessage());
		}
	}

	@Override
	public PageId allocPage() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'allocPage'");
	}

	@Override
	public void readPage(PageId a, ByteBuffer b) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'readPage'");
	}

	@Override
	public void writePage(PageId a, ByteBuffer b) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'writePage'");
	}

	@Override
	public void deallocPage(PageId a) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'deallocPage'");
	}

	@Override
	public int getCurrentCountAllocPages() {
		// TODO Auto-generated method stub
		return pageIdHistory.size();
	}


	public static DiskManager getSingleton(){
		return gSingleton;
	}
}
