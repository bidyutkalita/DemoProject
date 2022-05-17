package thread.staticSynchronization;

class A {
	static int a=0;
	 synchronized public static void dispay(String threadName, int n) {
		for (int i = 0; i < n; i++) {
//			System.out.println(threadName + " : " + i);
//			System.out.println(threadName + " : " + a++);
			System.out.println(a++);
			
		}
	}
}

public class StaticSynchronizationTest {
	public static void main(String[] args) {

		new Thread(() -> A.dispay("First",10)).start();
		new Thread(() -> A.dispay("Seconed",10)).start();
		new Thread(() -> A.dispay("Third",10)).start();
		new Thread(() -> A.dispay("Forth",10)).start();

	}

}
