package thread.concurrency.countDownLatch;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
	static CountDownLatch latch = new CountDownLatch(2);

	public static void main(String[] args) {

		Runnable follower = () -> {
			
			try {
				latch.countDown();
				System.out.println("Follower: "+Thread.currentThread().getName());
			} catch (Exception e) {
				// TODO: handle exception
			}

		};
		Runnable leader = () -> {
			
			try {
				latch.await();
				System.out.println("Leader: "+Thread.currentThread().getName());
			} catch (Exception e) {
				// TODO: handle exception
			}

		};
		
		Thread followerThread1 = new Thread(follower);
		Thread followerThread2 = new Thread(follower);
		Thread followerThread3 = new Thread(follower);
		Thread leaderThread= new Thread(leader);
		
		followerThread1.start();
		followerThread2.start();
		followerThread3.start();
		
		leaderThread.start();

	}

}
