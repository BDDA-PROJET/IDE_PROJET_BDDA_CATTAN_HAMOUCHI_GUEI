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

	public int getFileIdx() {
		return fileIdx;
	}

	public int getPageIdx() {
		return pageIdx;
	}

	public void setFileIdx(int fileIdx) {
		this.fileIdx = fileIdx;
	}

	public void setPageIdx(int pageIdx) {
		this.pageIdx = pageIdx;
	}

	public void set(int fileIdx, int pageIdx) {
		setFileIdx(fileIdx);
		setPageIdx(pageIdx);
	}

	public void reset() {
		set(0, 0);
	}

	@Override
	public String toString() {
		return fileIdx + "." + pageIdx;
	}

}
