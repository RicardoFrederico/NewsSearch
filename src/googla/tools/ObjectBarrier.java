package googla.tools;

import java.util.ArrayList;
import java.util.List;



/**
 * 
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 * @param <Item>
 */
public class ObjectBarrier <Item> {
	
	private int clientID;
	private int max_objects;
	private List<Item> doneTasksList;
	
	/**
	 * @param clientID
	 * @param max_objects
	 */
	public ObjectBarrier(int clientID, int max_objects) {
		if(max_objects <= 0) throw new IllegalArgumentException("Object List must be greater than 0");
		this.clientID = clientID;
		this.max_objects = max_objects;
		doneTasksList = new ArrayList<>();
	}
	
	/**
	 * This Method waits for results, when the results are equal to the size of the list it will create an copy of the array of results
	 * cleans the original results  and notifies all threads that is available to receive results
	 * @return the results in an ArrayList
	 * @throws InterruptedException
	 */
	public synchronized ArrayList<Item> waitResults() throws InterruptedException {
		while(doneTasksList.size() < max_objects)
			wait();		
		ArrayList<Item> copyArray = new ArrayList<>(doneTasksList);
		doneTasksList.clear();
		notifyAll(); // notifica todos os trabalhadores que a lista está vazia 
		return copyArray;		
	}
	
	
	/**
	 * Adds an object to the list if there is room in the list, and notifies all threads that it has put a result for checking of waitResults()
	 * @param itm
	 * @throws InterruptedException
	 */
	public synchronized void addResult(Item itm) throws InterruptedException {
		//while(doneTasksList.size() == max_objects) // not necessary when condition is met there is not any more objects
			//wait();
		doneTasksList.add(itm);
		notify(); //synchronized guarantees only one thread is using this method 
	}
	
	public int getClientID() {
		return clientID;
	}
	public int getMax_Objects() {
		return max_objects;
	}
	
}
