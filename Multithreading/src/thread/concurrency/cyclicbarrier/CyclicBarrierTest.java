package thread.concurrency.cyclicbarrier;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
	static CyclicBarrier barrier = new CyclicBarrier(3);
	public static void main(String[] args) {
		Runnable runnable=()->{
			for(int i=0;i<100;i++)
			{
				if(i==50)
				{
					System.out.println("Started "+Thread.currentThread().getName());
					
					try
					{
						CyclicBarrierTest.barrier.await();
					}
					catch (Exception e) {
						// TODO: handle exception
					}
					
					System.out.println("Finished");
				}
				
			}
		};
		
		
		Thread t1=new Thread(runnable);
		Thread t2=new Thread(runnable);
		Thread t3=new Thread(runnable);
		Thread t4=new Thread(runnable);
		Thread t5=new Thread(runnable);
		Thread t6=new Thread(runnable);
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
	}

}
