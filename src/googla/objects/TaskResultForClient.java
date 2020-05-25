package googla.objects;

import java.util.ArrayList;

/**
 * Results class for client
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class TaskResultForClient extends Message{
	
	private static final long serialVersionUID = 1L;
	private ArrayList<TaskDoneToServer> listOfsearchedArticles; 
	private Article articleSearchResult;
	private ArrayList<Integer> positions;
	private MessageType type;
	
	
	/**
	 * Constructor of news search result list for client 
	 * @param listOfsearchedArticles list of results with occurrence bigger than 0 and ordered
	 * @param type identifies the type of result in this constructor it is a search request result
	 */
	public TaskResultForClient(ArrayList<TaskDoneToServer> listOfsearchedArticles, MessageType type) {
		this.listOfsearchedArticles = listOfsearchedArticles;		
		this.type = type;
	}

	/**
	 * Constructor for article search result
	 * @param articleSearchResult article to deliver to client
	 * @param positions array of word positions
	 * @param type identifies the type of result in this constructor it is a article request result
	 */
	public TaskResultForClient(Article articleSearchResult,ArrayList<Integer> positions, MessageType type) {
		this.articleSearchResult = articleSearchResult;
		this.positions=positions;
		this.type = type;
	}


	public ArrayList<TaskDoneToServer> getListOfsearchedArticles() {
		return listOfsearchedArticles;
	}


	public Article getArticleSearchResult() {
		return articleSearchResult;
	}


	public MessageType getType() {
		return type;
	}


	public ArrayList<Integer> getPositions() {
		return positions;
	}
	
	
	
}
