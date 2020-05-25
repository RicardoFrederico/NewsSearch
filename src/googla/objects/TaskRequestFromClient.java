package googla.objects;

/**
 * Client sends this Request Object to server
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class TaskRequestFromClient extends Message{
	
	private static final long serialVersionUID = 6074738015691102174L;
	private int newsId;
	private String stringToSearch;
	private MessageType type;
	
	/**
	 * Constructor for word search request to server
	 * @param stringToSearch string to search
	 * @param type defined by enum
	 */
	public TaskRequestFromClient(String stringToSearch, MessageType type) {				
		this.stringToSearch = stringToSearch;
		this.type = type;
	}
	
	/**
	 * Constructor for article id request for server
	 * @param newsId article id
	 * @param type enum for type
	 */
	public TaskRequestFromClient(int newsId, MessageType type) {		
		this.newsId = newsId;
		this.type = type;
	}

	public String getStringToSearch() {
		return stringToSearch;
	}

	public MessageType getType() {
		return type;
	}

	public int getNewsId() {
		return newsId;
	}	
	

}
