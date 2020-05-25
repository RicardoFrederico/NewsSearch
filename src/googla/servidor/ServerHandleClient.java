package googla.servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import googla.objects.Article;
import googla.objects.MessageType;
import googla.objects.TaskDoneToServer;
import googla.objects.TaskJobToWorker;
import googla.objects.TaskRequestFromClient;
import googla.objects.TaskResultForClient;
import googla.tools.ObjectBarrier;

/**
 * Server Thread for handling Client communication
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class ServerHandleClient extends Thread{
	
	private ServidorGooglaMain server;    
    private Socket socket;    
    private ObjectInputStream in; 
    private ObjectOutputStream out;  
    private int clientID;
    private ObjectBarrier<TaskDoneToServer> doneSearchBarrier;
    private ArrayList<TaskDoneToServer> tasksDone;
    //private ArrayList<TaskDoneToServer> copyOftasksDone;
    
    /**
     * Thread constructor for server - client communication 
     * @param server server instance
     * @param socket socket 
     * @param in object input stream
     * @param out object output stream
     * @param clientID id for client assigned by server
     */
	ServerHandleClient(ServidorGooglaMain server, Socket socket, ObjectInputStream in, ObjectOutputStream out, int clientID) {
		this.server = server;
		this.socket = socket;
		this.in = in;
		this.out = out;
		this.clientID = clientID;
		doneSearchBarrier = new ObjectBarrier<>(clientID, server.getJornal().size());
	}
	
	
	/**
	 * Thread runs in a while loop waiting for Tasks Requests 
	 */
	@Override
	public void run() {
		System.out.println("ServerHandleClient -> The SHC thread has started");		
		try {
			while(true) {				
				TaskRequestFromClient message = (TaskRequestFromClient) in.readObject();
				MessageType type = message.getType();
				switch(type) {						
				case NEWS_REQUEST:					
					sendArticleForRequest(message.getNewsId());
					break;
				case SEARCH_REQUEST:					
					creatTaskJobs(message.getStringToSearch()); // cria as tarefas para o worker
					sendArrayOfResultstoClient(doneSearchBarrier.waitResults()); // aguarda que a barreira de o resultado trata e envia para o cliente
					break;
				default:
					break;
				}				
			}
			
		}catch (Exception e) {			
			server.doRemoveHandleClientFromArray(this);						
		}finally {
			try {
				socket.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}		
	}
	
	
	/**
	 * When the client requests a word search to the server it creates tasks for workers
	 * @param wordToSearch string for search
	 * @throws InterruptedException throws if tasks can't be created
	 */
	private synchronized void creatTaskJobs(String wordToSearch) throws InterruptedException{
		ArrayList<Article> journal = server.getJornal();
		for(Article articleToSearch : journal)
			server.getTaskReceiver().offer(new TaskJobToWorker(clientID,wordToSearch,articleToSearch));		
	}
	
	/**
	 * When the object barrier releases the results this method sends the array result to client
	 * @param allTaskFromClientDone array with results for client
	 */
	private void sendArrayOfResultstoClient(ArrayList<TaskDoneToServer> allTaskFromClientDone) {		
		tasksDone = new ArrayList<>();
		for(TaskDoneToServer taskFinished : allTaskFromClientDone) {			
			if(taskFinished.getStringPositionsInText().size()>0) {
				tasksDone.add(taskFinished);				
			}
		}
		tasksDone.sort((o1, o2) -> o2.getPositionsCount() - o1.getPositionsCount());		
		try {			
			TaskResultForClient result = new TaskResultForClient(tasksDone, MessageType.SEARCH_RESULT);
			out.writeObject(result);			
		} catch (IOException e) {
			System.out.println("ServerHandleClient -> Client isn't online, couldn't send the results");			
		}
		
	}
	
	/**
	 * This method receives an article id and searchs in arraylist of tasksDone to send to client
	 * @param articleId article id for search in results arraylist
	 */
	private void sendArticleForRequest(int articleId) {		
		TaskResultForClient result;
		for(TaskDoneToServer t : tasksDone) {
			if(t.getSearchedArticleId() ==articleId) {
				result = new TaskResultForClient(server.doGetArticleById(articleId),t.getStringPositionsInText(), MessageType.NEWS_RESULT);
				try {
					out.writeObject(result);
				} catch (IOException e) {
					System.out.println("ServerHandleClient -> Client isn't online, couldn't send the article");
					//e.printStackTrace();
				}
				return;
			}	
			
		}
		
	}
	
	int getClientID() {
		return clientID;
	}


	ObjectBarrier<TaskDoneToServer> getDoneSearchBarrier() {
		return doneSearchBarrier;
	}
	
	

}
