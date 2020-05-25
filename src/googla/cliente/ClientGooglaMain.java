package googla.cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import googla.objects.MessageType;
import googla.objects.TaskRequestFromClient;
import googla.objects.TaskResultForClient;

/**
 * Client Side App
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class ClientGooglaMain {
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private final int SERVER_PORT=8080;
	private GooglaGui gui;
	
	/**
	 * Client Construtor connections maker and gui creator
	 * @param s server address to connect
	 */
	private ClientGooglaMain(String s) {
		this.gui = new GooglaGui(this);		
		doConnections(s);	
	}
	
	/**
	 * Creates a connection to a given server with known port 8080 and streams for communication
	 * @param serverAddr Server Address
	 */
	private void doConnections(String serverAddr) {
        try {
            InetAddress address = InetAddress.getByName(serverAddr);
            socket = new Socket(address, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());            
            out.writeObject("CLIENT");
			System.out.println("ClientGooglaMain -> Client connected to Server "+ serverAddr);
			init();			
        } catch (IOException e) {
        	System.out.println("ClientGooglaMain -> Client failed to connect to server.");
        	gui.serverConnectionLost(0);
			//e.printStackTrace();
		}
    }
	
	
	
	
	
	/**
	 * Creates a while loop waiting for messages from server
	 */
	private void init() {
		try {			
			while(true) {
				TaskResultForClient message = (TaskResultForClient) in.readObject();
				MessageType type = message.getType();
				switch(type) {				
				case NEWS_RESULT:
					gui.doShowArticleText(message.getArticleSearchResult().toString(),message.getPositions());
					break;
				case SEARCH_RESULT:
					gui.doShowListResults(message.getListOfsearchedArticles());
					gui.enableSearchButton();
					break;
				default:
					break;							
				}
				 
			}			
		}catch (Exception e) {
			System.out.println("ClientGooglaMain -> connection to server lost");
			//e.printStackTrace();
			gui.serverConnectionLost(1);
		}       
		
	}
	
	/**
	 * Method for sending a request for a word search in the news folder
	 * @param s word to search
	 */
	private void sendSearchMessage(String s) { //from gui
		TaskRequestFromClient taskRequested = new TaskRequestFromClient(s,MessageType.SEARCH_REQUEST);
		try {			
			out.writeObject(taskRequested);
		} catch (IOException e) {
			System.out.println("ClientGooglaMain -> error in sending search request");
			//e.printStackTrace();
		}
	}
	
	/**
	 * This method is the public interface for the previous method
	 * @param s word/string to search in article
	 */
	void doSendSearchMessage(String s) { //method package private
		sendSearchMessage(s);
	}
	
	/**
	 * Method for requesting a article from the server 
	 * @param n article id
	 */
	private void sendArticleRequest(int n) { //from gui
		TaskRequestFromClient articleRequested = new TaskRequestFromClient(n,MessageType.NEWS_REQUEST);
		try {
			out.writeObject(articleRequested);			
		} catch (IOException e) {
			System.out.println("ClientGooglaMain -> error in sending news request");
			e.printStackTrace();
		}		
	}
	
	/**
	 * This method is the public interface for the previous method
	 * @param n
	 */
	void doSendArticleRequest(int n) { //method package private
		sendArticleRequest(n);
	}
	
	public static void main(String[] args){
		String serverAddress;		
		if (args.length < 1) {
			System.out.println("ClientGooglaMain -> A server address was not assigned in args. Using localhost");
			serverAddress = "127.0.0.1";
		} else {
			serverAddress = args[0];
		}
	
		new ClientGooglaMain(serverAddress);
		
		
	}

}
