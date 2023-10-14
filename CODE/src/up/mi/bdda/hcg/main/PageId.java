package up.mi.bdda.hcg.main;

/**
 * Cette classe associe une page à un fichier et
 * fournit des méthodes pour obtenir et définir les index du fichier et de la
 * page. Elle prend également en charge le clonage et fournit une représentation
 * sous forme de chaîne de caractères de l'objet.
 * 
 * @see java.lang.Cloneable
 */
public class PageId implements Cloneable {
	/** L’identifiant du fichier. */
	private int fileIdx;

	/** l’indice de la page dans le fichier. */
	private int pageIdx;

	/**
	 * Le constructeur par défaut de la classe PageId. Il appelle la méthode
	 * {@code reset()} pour initialiser {@code fileIdx} et {@code pageIdx} à 0.
	 * 
	 * @see up.mi.bdda.hcg.main.PageId#reset()
	 */
	public PageId() {
		reset();
	}

	/**
	 * Ce constructeur permet de créer un objet PageId avec les valeurs
	 * {@code fileIdx}
	 * et {@code pageIdx}.
	 * 
	 * @param fileIdx la valeur de fileIdx
	 * @param pageIdx la valeur de pageIdx
	 */
	public PageId(int fileIdx, int pageIdx) {
		this.fileIdx = fileIdx;
		this.pageIdx = pageIdx;
	}

	/**
	 * Retourne la valeur {@code fileIdx} de l'objet PageId.
	 * 
	 * @return la valeur de fileIdx
	 */
	public int getFileIdx() {
		return fileIdx;
	}

	/**
	 * Retourne la valeur {@code pageIdx} de l'objet PageId.
	 * 
	 * @return la valeur de pageIdx
	 */
	public int getPageIdx() {
		return pageIdx;
	}

	/**
	 * Définit la valeur {@code fileIdx} de l'objet PageId avec la valeur
	 * spécifiée.
	 * 
	 * @param fileIdx la nouvelle valeur de fileIdx
	 */
	public void setFileIdx(int fileIdx) {
		this.fileIdx = fileIdx;
	}

	/**
	 * Définit la valeur {@code pageIdx} de l'objet PageId avec la valeur
	 * spécifiée.
	 * 
	 * @param pageIdx la nouvelle valeur de pageIdx
	 */
	public void setPageIdx(int pageIdx) {
		this.pageIdx = pageIdx;
	}

	/**
	 * Définit les valeurs {@code fileIdx} et {@code pageIdx} de l'objet
	 * PageId avec les valeurs spécifiées.
	 * 
	 * @param fileIdx la nouvelle valeur de fileIdx
	 * @param pageIdx la nouvelle valeur de pageIdx
	 */
	public void set(int fileIdx, int pageIdx) {
		setFileIdx(fileIdx);
		setPageIdx(pageIdx);
	}

	/**
	 * Définit les valeurs {@code fileIdx} et {@code pageIdx} de l'objet
	 * PageId à 0 en appelant la méthode {@code set()} avec les arguments 0
	 * et 0.
	 * 
	 * @see up.mi.bdda.hcg.main.PageId#set(int, int)
	 */
	public void reset() {
		set(0, 0);
	}

	@Override
	protected PageId clone() {
		PageId pageId = null;

		try {
			pageId = (PageId) super.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}

		return pageId;
	}

	@Override
	public String toString() {
		return fileIdx + "." + pageIdx;
	}
}
