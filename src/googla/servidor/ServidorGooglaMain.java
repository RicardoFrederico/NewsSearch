package googla.servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


import googla.objects.TaskJobToWorker;
import googla.objects.Article;
import googla.tools.MyBlockingQueue;
import googla.tools.TextUtilities;

/**
 * Instrucoes para limpar ligacoes residuais no port 8080
 * netstat -ano | findstr 8080
 * taskkill /F /pid <número do pid ativo>
 * 
 * Server Main App
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class ServidorGooglaMain {
	
	private static final int PORT = 8080; // consider making it local
	private ServerSocket serverSocket;
	private Socket socket;	
	private MyBlockingQueue<TaskJobToWorker> taskReceiver;
	private int clientID = 0;
	private ArrayList<Article> jornal;
	private ArrayList<ServerHandleClient> clientes = new ArrayList<>();
	//private ArrayList<ServerHandleWorker> workers = new ArrayList<>(); //maybe useful for a future implementation strategy?
	
	/**
	 * Class constructor loads the files from the folder into an array after converting to article object
	 * @param s path to the folder
	 */
	private ServidorGooglaMain(String s) {
		jornal = TextUtilities.carregarNoticias(s);
		taskReceiver = new MyBlockingQueue<>();			
	}
	
	/**
	 * Creates the connection / communication streams in a while loop it creates and starts thread for client and worker handlers
	 * @throws IOException method interruption
	 */
	private void doConnections() throws IOException {
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("ServidorGooglaMain -> Created ServerSocket: " + serverSocket);
		} catch (IOException e) {
			System.out.println("ServidorGooglaMain -> Couldn't create serversocket");
			e.printStackTrace();
		}		
		try {
			while(true) {
				socket = serverSocket.accept();
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				String message = (String) in.readObject();				
				if(message.equals("WORKER")){
					ServerHandleWorker worker = new ServerHandleWorker(this, socket, in, out, taskReceiver);
					//workers.add(worker);
					worker.start();					
				}
				if(message.equals("CLIENT")){
					ServerHandleClient cli = new ServerHandleClient(this, socket,in, out,clientID++);
					clientes.add(cli);
					cli.start();
				}																
				System.out.println("ServidorGooglaMain -> Server accepted connection from " + socket.getInetAddress());
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("ServidorGooglaMain -> Couldn't connect streams to handlers");
			e.printStackTrace();
		} finally {
			serverSocket.close();
		}
	}	
	
	/**
	 * private method when a client disconnects this method removes the client handler thread from the clientes array 
	 * @param clientHandlertoKill client thread to remove
	 */
	private synchronized void removeHandleClientFromArray(ServerHandleClient clientHandlertoKill) {			
		clientes.remove(clientHandlertoKill);
		System.out.println("ServidorGooglaMain -> Removi o SHC com o id: " + clientHandlertoKill.getClientID());
	}
	
	/**
	 * public method that calls the corresponding private method
	 * @param clientHandler client thread to remove
	 */
	void doRemoveHandleClientFromArray(ServerHandleClient clientHandler) {		
		removeHandleClientFromArray(clientHandler);		
	}
	
	/**
	 * private method to get an article object from the array using the article id
	 * @param articleID article id
	 * @return searched article
	 */
	private synchronized Article getArticleById(int articleID) {
        Article toReturn = null;
        for (Article search : jornal)
            if (search.getId() == articleID)
                toReturn = search;
        return toReturn;
    }
	
	/**
	 * public method that calls the corresponding private method
	 * @param articleId article id
	 * @return searched article
	 */
	Article doGetArticleById(int articleId) {
		return getArticleById(articleId);
	}
	
	MyBlockingQueue<TaskJobToWorker> getTaskReceiver() { return taskReceiver; }

	ArrayList<ServerHandleClient> getHandleClientes() { return clientes;	}

	public ArrayList<Article> getJornal() {	return jornal; }
	
	/* auxiliary methods for alternative implementations to use if needed
    private int getArrayIndexByArticleId(int articleID) {
        int toReturn = 0;
        for (Article search : jornal)
            if (search.getId() == articleID)
                toReturn = jornal.indexOf(search);
        return toReturn;
    }

    private Article getArticleByArrayIndex(int index) {
        return jornal.get(index);
    }   

    public void doGetArrayIndexByArticleId(int articleId) {
        getArrayIndexByArticleId(articleId);
    }

    public void doGetArticleByArrayIndex(int index) {
        getArticleByArrayIndex(index);
    } */
	
	public static void main(String[] args) throws IOException {		
		String folder;		
		if (args.length < 1) {
			System.out.println("ServidorGooglaMain -> A folder was not assigned in args. Using local folder: news");
			folder = "news";
		} else {
			folder = args[0];
		}
		new ServidorGooglaMain(folder).doConnections();
		
		
	}

}
