package googla.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import googla.objects.TaskDoneToServer;
import googla.objects.TaskJobToWorker;
import googla.tools.TextUtilities;

/**
 * Worker main app
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class WorkerGooglaMain {
	
	private final int MAX_RECONN_TRY = 5;
	private final int SERVER_PORT=8080;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private boolean isWorkerOnline;
	private String serveAddress;
	
	/**
	 * Class constructor creates the connection/communication streams to server
	 * @param server
	 */
	private WorkerGooglaMain(String server) {
		this.serveAddress=server;
		doConnections(server);	
	}
	
	/**
	 * Creates the connection / streams to server if the server isn't up it ends the program
	 * @param serverAddr server address to connect
	 */
	private void doConnections(String serverAddr) {
        try {
            InetAddress address = InetAddress.getByName(serverAddr);
            socket = new Socket(address, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isWorkerOnline = true;
            out.writeObject("WORKER");
			System.out.println("WorkerGooglaMain -> Worker connected to Server "+ serverAddr);
			init();
        } catch (IOException e ) {
        	System.out.println("WorkerGooglaMain -> Worker failed to connect to server. Please try again later");        	
        } 
    }
	
			
	/**
	 * Starts worker thread working if the server disconnects it tries to reconnect to the server
	 */
	private void init() {
		try {
			while(true) {
				TaskJobToWorker taskToDo = (TaskJobToWorker) in.readObject();
				out.writeObject(createTaskDoneToServer(taskToDo)); // recebe a tasktodo e transforma em taskdone e envia para o server
			}			
		}catch (Exception e) {
			isWorkerOnline = false;
			System.out.println("WorkerGooglaMain -> Worker Exiting due to server disconnection");
			reconnectWorker(serveAddress);
		}       
		
	}
	
	/**
	 * Method to search word in text and return an object with taskdone
	 * @param taskToDo TaskJobToWorker object whit the article and word for search request
	 * @return TaskDonetoServer object with single article word search result
	 */
	private TaskDoneToServer createTaskDoneToServer ( TaskJobToWorker taskToDo ) {
		ArrayList<Integer> positions;
		String toSearch = taskToDo.getArticleToBeSearched().toString(); 
		positions = TextUtilities.searchWordMethod(toSearch, taskToDo.getStringToSearch());
		String searchResult = positions.size() + " - " + taskToDo.getArticleToBeSearched().getTitulo();
		return new TaskDoneToServer(taskToDo.getClientID(), taskToDo.getArticleToBeSearched().getId(), searchResult,positions);
	}
	
	/**
	 * Method to try to reconnect worker to server after server disconnection
	 * @param serverAddr
	 */
	private void reconnectWorker(String serverAddr) {
		int counter=0;
		while(!isWorkerOnline) {			
			if (counter < MAX_RECONN_TRY) {
				try {					
					InetAddress address = InetAddress.getByName(serverAddr);
		            socket = new Socket(address, SERVER_PORT);
		            out = new ObjectOutputStream(socket.getOutputStream());
		            in = new ObjectInputStream(socket.getInputStream());
		            isWorkerOnline = true;
		            out.writeObject("WORKER");
					System.out.println("WorkerGooglaMain -> Worker connected to Server "+ serverAddr);
					init();				
					break;
				}catch (Exception e) {
					try {						
						System.out.println("WorkerGooglaMain -> sleeping 5 sec before trying again ");
						Thread.sleep(5000);
						counter++;						
					} catch (InterruptedException e1) {						
						System.out.println("WorkerGooglaMain -> missed sleep");
					}				
				}
				
			}else {
				System.out.println("WorkerGooglaMain -> The server isn't getting up so maybe try again later");
				break;
			}
			
			
		}
		
		
	}
	
	
	public static void main(String[] args) {
		String serverAddress;		
		if (args.length < 1) {
			System.out.println("WorkerGooglaMain -> A server address was not assigned in args. Using localhost");
			serverAddress = "127.0.0.1";
		} else {
			serverAddress = args[0];
		}
		
		new WorkerGooglaMain(serverAddress);
		
	}

}