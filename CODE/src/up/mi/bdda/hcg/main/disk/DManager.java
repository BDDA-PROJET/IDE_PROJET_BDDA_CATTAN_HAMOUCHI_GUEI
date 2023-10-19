package up.mi.bdda.hcg.main.disk;

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
import up.mi.bdda.hcg.main.DBParams;

/**
 * Cette classe implémente l'interface {@code DiskManager}.
 * <p>
 * Elle est responsable de la gestion des opérations sur le disque, telles que
 * {@code l'allocation} et {@code la désallocation} de pages ainsi que
 * {@code la lecture} et {@code l'écriture} de données dans les fichiers du
 * disque.
 * <p>
 * Elle maintient des {@code ensembles} et les {@code files d'attente} afin de
 * garder une trace des identifiants de page allouées et désallouées.
 * <p>
 * Elle utilise également la classe {@code PageId} pour répresnter des
 * identifiants de page et construire des chemins d'accès aux fichier du disque
 * en fonction de l'identifiant de la page à allouée.
 * 
 * @see up.mi.bdda.hcg.api.DiskManager
 */
public class DManager implements DiskManager {
	/** l'ensemble des identifiants de page alloués. */
	private Set<Float> allocIdSet;
	/** la file d'attente des identifiants de page désalloués. */
	private Queue<Float> deallocIdQueue;
	/** L'identifiant de la page à allouée. */
	private PageId currentlyAvailablePageId;
	/** L'unique instance du {@code DiskManager}. */
	private static DiskManager gSingleton = new DManager();

	/**
	 * Le constructeur par défaut de la classe DManager. Il initialise les attributs
	 * de la classe et empêche la creation de nouvelle instance de DManager.
	 */
	private DManager() {
		allocIdSet = new HashSet<>();
		deallocIdQueue = new LinkedList<>();
		currentlyAvailablePageId = new PageId(0, 0);
	}

	private RandomAccessFile getAccessFile(PageId pageId) throws IOException {
		RandomAccessFile file = new RandomAccessFile(getFilePath(pageId).toString(), "rw");
		file.seek((pageId.getPageIdx()) * DBParams.SGBDPageSize);
		return file;

	}

	/**
	 * Retourne le chemin d'accès au fichier correspondant au pageId passé en
	 * paramètre.
	 * <p>
	 * Elle construit le nom du fichier sur la base de
	 * {@code currentlyAvailablePageId} et
	 * renvoie le chemin à l'aide de la méthode {@code Paths.get()}.
	 * 
	 * @param pageId le pageId du fichier
	 * @return le chemin d'accès du fichier
	 * 
	 * @see java.nio.file.Paths#get(String, String...)
	 */
	private Path getFilePath(PageId pageId) {
		String fileName = "F".concat(String.valueOf(pageId.getFileIdx())).concat(".data");
		Path path = Paths.get(DBParams.DBPath, fileName);

		return path;
	}

	/**
	 * Retourne un objet {@code RandomAccessFile} et positionne
	 * le curseur du fichier en fonction de l'argument pageId afin de permetre la
	 * lecture ou l'écriture dans un fichier.
	 * 
	 * @param pageId l'identifiant de la page où lire et écrire
	 * @return un object RandomAccessFile
	 * @throws IOException if an I/O error occurs.
	 */
	private RandomAccessFile getRandomAccessFile(PageId pageId) throws IOException {
		RandomAccessFile file = new RandomAccessFile(getFilePath(pageId).toString(), "rw");
		int offsetPosition = DBParams.SGBDPageSize * pageId.getPageIdx();

		file.seek(offsetPosition);
		return file;
	}

	/**
	 * Crée un fichier basé sur le {@code currentlyAvailablePageId} .
	 * <p>
	 * Elle appelle la méthode {@code getFilePath()} pour obtenir le chemin d'accès
	 * au fichier, puis utilise les méthodes {@code Files.createDirectories()} et
	 * {@code Files.createFile()} pour créer le fichier et ses répertoires parents
	 * s'ils n'existent pas.
	 * 
	 * @see up.mi.bdda.hcg.main.disk.DManager#getFilePath(PageId)
	 * @see java.nio.file.Files#createDirectories(Path,
	 *      java.nio.file.attribute.FileAttribute...)
	 * @see java.nio.file.Files#createFile(Path,
	 *      java.nio.file.attribute.FileAttribute...)
	 */

	private void createFile() {
		Path path = getFilePath(currentlyAvailablePageId);

		try {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		} catch (Exception e) {
			if (!(e instanceof FileAlreadyExistsException))
				e.printStackTrace(System.err);
		}
	}

	/**
	 * Met à jour le {@code currentlyAvailablePageId} pour l'allocation de la page
	 * suivante.
	 * <p>
	 * Elle incrémente l'index du fichier et l'index de la page en conséquence. Si
	 * l'index de fichier dépasse le nombre maximal de fichiers défini dans
	 * {@code DBParams.DMFFileCount}, elle réinitialise l'index de fichier à 0 et
	 * incrémente l'index de page.
	 */
	private void next() {
		int nextFileId = currentlyAvailablePageId.getFileIdx() + 1;
		int nextPageId = currentlyAvailablePageId.getPageIdx();

		if (DBParams.DMFFileCount == nextFileId) {
			nextFileId = 0;
			nextPageId += 1;
		}

		currentlyAvailablePageId.set(nextFileId, nextPageId);
	}

	@Override
	public PageId allocPage() {
		PageId copy = currentlyAvailablePageId.clone();

		createFile();

		if (!(deallocIdQueue.isEmpty())) {
			float deallocId = deallocIdQueue.poll();
			int fileIdx = (int) deallocId;
			int pageIdx = (int) Math.round((deallocId % 1) * 100);

			copy.set(fileIdx, pageIdx);
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
			currentlyAvailablePageId.reset();
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
			RandomAccessFile file = getAccessFile(pageId);
			file.read(buff.array(), 0, DBParams.SGBDPageSize);
			file.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);

		}
	}

	@Override
	public void writePage(PageId pageId, ByteBuffer buff) {
		try {
			RandomAccessFile file = getRandomAccessFile(pageId);
			file.write(buff.array(), 0, DBParams.SGBDPageSize);
			file.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Retourne l'unique instance de {@code DiskManager} .
	 * 
	 * @return une instance de DiskManager
	 */
	public static DiskManager getSingleton() {
		return gSingleton;
	}

}
