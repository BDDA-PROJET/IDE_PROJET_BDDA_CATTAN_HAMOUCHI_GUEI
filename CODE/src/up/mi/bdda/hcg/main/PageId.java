package up.mi.bdda.hcg.main;

/**
 * Cette classe associe une page à un fichier.
 */
public class PageId {
	/** L’identifiant du fichier. */
	private int fileIdx;

	/** l’indice de la page dans le fichier. */
	private int pageIdx;

	public PageId() {
		reset();
	}

	public PageId(int fileIdx, int pageIdx) {
		this.fileIdx = fileIdx;
		this.pageIdx = pageIdx;
	}

	public void reset() {
		fileIdx = 0;
		pageIdx = 0;
	}

	public int getFileIdx() {
		return fileIdx;
	}

	public int getPageIdx() {
		return pageIdx;
	}

	@Override
	public String toString() {
		return fileIdx + "." + pageIdx;
	}

}
