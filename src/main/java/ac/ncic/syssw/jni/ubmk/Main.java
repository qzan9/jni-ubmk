package ac.ncic.syssw.jni.ubmk;

import static ac.ncic.syssw.jni.ubmk.JniUbmk.*;

public class Main {
	private static int emptyJvmMethodHere() {
		return 0;
	}

	public static void main(String[] args) {

		emptyJvmMethod();
		emptyJniCall();
		emptyJvmMethodHere();

		long then0 = System.nanoTime();
		int  test0 = emptyJvmMethod();
		long  now0 = System.nanoTime();

		long then1 = System.nanoTime();
		int  test1 = emptyJniCall();
		long  now1 = System.nanoTime();

		long then2 = System.nanoTime();
		int  test2 = emptyJvmMethodHere();
		long  now2 = System.nanoTime();

		if (test0 == 0 && test1 == 0 && test2 == 0) {
			System.out.println("test correct.");
			System.out.println("elapsed time 0: " + (now0 - then0));
			System.out.println("elapsed time 1: " + (now1 - then1));
			System.out.println("elapsed time 2: " + (now2 - then2));
		}
	}
}
