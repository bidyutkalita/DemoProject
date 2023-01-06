package com.bid.java.multithreading.thread.shutdownHook;

public class ShutDownHook {

	public static void main(String[] args) throws Exception {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("shoutdown hook executed");
			}
		});
		 System.out.println("Application Terminating ...");
	}
}
