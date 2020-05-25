package googla.objects;

import java.util.ArrayList;

/**
 * Search Result send from worker to server
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class TaskDoneToServer extends Message {
	
	private static final long serialVersionUID = 353684868522440591L;
	private int clientId;
	private int searchedArticleId;
	private String searchResult;
	private ArrayList<Integer> stringPositionsInText;
	
		
	/**
	 * After worker does it job it creats this object to server
	 * @param clientId Client ID
	 * @param searchedArticleId article ID
	 * @param searchResult a String with searched word in article frequency and the article title 
	 * @param stringPositionsInText arraylist of positions where the searched word can be found
	 */
	public TaskDoneToServer(int clientId, int searchedArticleId, String searchResult, ArrayList<Integer> stringPositionsInText) {
		this.clientId = clientId;
		this.searchedArticleId = searchedArticleId;
		this.searchResult = searchResult;
		this.stringPositionsInText = stringPositionsInText;
	}


	public int getClientId() {
		return clientId;
	}


	public int getSearchedArticleId() {
		return searchedArticleId;
	}
	

	public ArrayList<Integer> getStringPositionsInText() {
		return stringPositionsInText;
	}

	public int getPositionsCount() {		
		return stringPositionsInText.size();
	}


	@Override
	public String toString() {
		return searchResult;
	}
	
	
	

}
