package googla.servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import googla.objects.TaskDoneToServer;
import googla.objects.TaskJobToWorker;
import googla.tools.MyBlockingQueue;

/**
 * Server Thread for handling worker communication
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class ServerHandleWorker extends Thread {
	
	private ServidorGooglaMain server;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private MyBlockingQueue<TaskJobToWorker> taskReceiver;
	private TaskJobToWorker lastTask;
	
	/**
	 * Thread constructor for server - worker communication 
	 * @param server server instance
	 * @param socket socket
	 * @param in object input stream
	 * @param out object output stream
	 * @param taskReceiver shared blocking queue for all workers
	 */
	ServerHandleWorker(ServidorGooglaMain server, Socket socket, ObjectInputStream in, ObjectOutputStream out,
			MyBlockingQueue<TaskJobToWorker> taskReceiver) {		
		this.server = server;
		this.socket = socket;
		this.in = in;
		this.out = out;
		this.taskReceiver = taskReceiver;
	}
	
	/**
	 * Thread waits in while loop fetching tasks from blocking queue and adding tasks done to the client object barrier of done tasks
	 */
	@Override
	public void run(){		
		System.out.println("ServerHandleWorker -> The SHW thread has started");		
		try {
			while(true){
				
				TaskJobToWorker taskToDo = taskReceiver.poll(); // retira uma tarefa da fila
				lastTask = taskToDo; // para nao perder a tarefa na disconecao
				out.writeObject(taskToDo); // envia para o trabalhador
								
				TaskDoneToServer taskDone = (TaskDoneToServer)in.readObject(); //recebe tarefa feita do worker
				addToClientObjectBarrier(taskDone); // envia para o object barrier do cliente no server client handler
			}
			
		} catch (IOException | ClassNotFoundException | InterruptedException e) {			
			try {
				taskReceiver.offer(lastTask);
			} catch (InterruptedException e1) {
				System.out.println("ServerHandleWorker -> Couldn't return the last task to the bloquing queue the client will block");
			}		
			System.out.println("ServerHandleWorker -> Message: worker exit");
		} finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * Method for adding tasks done to the correct client object barrier
	 * @param articleSearchResult task done
	 */
	private synchronized void addToClientObjectBarrier(TaskDoneToServer articleSearchResult) {
		for(ServerHandleClient clientHandler : server.getHandleClientes())
			if( articleSearchResult.getClientId()== clientHandler.getClientID())				
				try {					
					clientHandler.getDoneSearchBarrier().addResult(articleSearchResult);
					return;
				} catch (InterruptedException e) {
					System.out.println("ServerHandleWorker -> Couldn't add the TaskResult to the client ObjectBarrier");
					e.printStackTrace();
				}
	}
}
