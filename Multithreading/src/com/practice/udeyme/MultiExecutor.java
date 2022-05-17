package com.practice.udeyme;

import java.util.ArrayList;
import java.util.List;

public class MultiExecutor {

    // Add any necessary member variables here
	List<Thread> threadList = new ArrayList<Thread>();

    /* 
     * @param tasks to executed concurrently
     */
    public MultiExecutor(List<Runnable> tasks) {
        // Complete your code here
    	
    	for (Runnable runnable : tasks) {
			Thread thread = new Thread(runnable);
			threadList.add(thread);
					
		}
    }

    /**
     * Starts and executes all the tasks concurrently
     */
    public void executeAll() {
        for (Thread thread : threadList) {
			thread.start();
		}
    }
}