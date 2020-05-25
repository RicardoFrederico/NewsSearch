package googla.tools;

import java.util.LinkedList;

/**
 * 
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 * @param <T>
 */
public class MyBlockingQueue<T> {

    private LinkedList<T> list = new LinkedList<>();
    private int maxSize;

    //not used the blocking queue has all clients tasks
    public MyBlockingQueue(int maxSize) {
        this.maxSize = maxSize;
    }

   
    public MyBlockingQueue() {
        maxSize = Integer.MAX_VALUE;
    }

    // Offer
    public synchronized void offer(T object) throws InterruptedException {
        while(list.size() == maxSize) {
            wait();
        }
        list.add(object);
        notifyAll();
    }

    // Poll
    public synchronized T poll() throws InterruptedException {
        while(list.size() == 0) {
            wait();
        }
        T object = list.pollFirst();
        notifyAll();
        return object;
    }

    // Size - not used
    public int size() {
        return list.size();
    }

    // Clear - not used
    public synchronized void clear() {
        list.clear();
    }


}

