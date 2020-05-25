package googla.objects;

/**
 * Server creates this task object for worker
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class TaskJobToWorker extends Message{
	
	private static final long serialVersionUID = -5070636852975547099L;
	private int clientID;
	private String stringToSearch;
	private Article articleToBeSearched;

	
	/**
	 * Creates a task for worker to do
	 * 
	 * @param clientID - Client id number
	 * @param stringToSearch - string to search
	 * @param articleToBeSearched - text to search in
	 */
	public TaskJobToWorker(int clientID, String stringToSearch, Article articleToBeSearched) {		
		this.clientID = clientID;
		this.stringToSearch = stringToSearch;
		this.articleToBeSearched = articleToBeSearched;		
	}


	public int getClientID() {
		return clientID;
	}


	public String getStringToSearch() {
		return stringToSearch;
	}


	public Article getArticleToBeSearched() {
		return articleToBeSearched;
	}
		

}
