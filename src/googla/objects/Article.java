package googla.objects;

/**
 * Article Object created by server 
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 */
public class Article extends Message{
	
	private static final long serialVersionUID = 91657099698418736L;
	private int id;	
	private String titulo;
	private String corpo;	
	
	/**
	 * Object constructor by server after reading txt file	
	 * @param id assigns an id to the article
	 * @param titulo the first line of the txt file is considered the article title
	 * @param corpo the next lines are considered the body of the article
	 */
	public Article(int id, String titulo, String corpo) {		
		this.titulo = titulo;
		this.corpo = corpo;
		this.id = id;
	}
	
	public String getTitulo() {	return titulo; }	

	public String getCorpo() { return corpo; }

	public void setCorpo(String corpo) { this.corpo = corpo; }

	public int getId() { return id;	}	

	@Override
	public String toString() {
		return  titulo + "\n" + "\n" + corpo;
	}		

}
