package ac.ncic.syssw.jni.ubmk;

import static ac.ncic.syssw.jni.ubmk.JniUbmk.*;

public class RunBmk {
	private RunBmk() { }

	private static RunBmk INSTANCE;
	public  static RunBmk getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new RunBmk();
		}
		return INSTANCE;
	}

	public void uBmkEmptyCall() {
		/* warm up, guys. */
		emptyJvmMethod();
		emptyJniCall();
//		emptyJvmMethodHere();

		/* run the tests and measure the time. */
		long then0 = System.nanoTime();
		int  test0 = emptyJvmMethod();
		long  now0 = System.nanoTime();

		long then1 = System.nanoTime();
		int  test1 = emptyJniCall();
		long  now1 = System.nanoTime();

		long then2 = System.nanoTime();
		int  test2 = 0;//emptyJvmMethodHere();
		long  now2 = System.nanoTime();

		/* print the results. */
		if (test0 == 0 && test1 == 0 && test2 == 0) {
			System.out.println("test correct.");
			System.out.println("elapsed time 0: " + (now0 - then0));
			System.out.println("elapsed time 1: " + (now1 - then1));
			System.out.println("elapsed time 2: " + (now2 - then2));
		}
	}

	public void uBmkSomeCalc() {
		int i = 100, j = 100, k = 10000;

		System.out.println("=== calculation benchmark ===");

		/* warm up. */
		System.out.println("warming up ... ignore the outputs ...");
		for (int t = 0; t < 10; t++) {
			System.out.println(someCalcJvm(i, j, k));
//			System.out.println(someCalcJni(i, j, k));
		}

		/* JIT may be done; start real benchmarking ... */
		System.out.println();
		System.out.println("start timing ...");
		long then0 = System.currentTimeMillis();
		double test0 = someCalcJni(i, j, k);
		long  now0 = System.currentTimeMillis();

		long then1 = System.currentTimeMillis();
		double test1 = someCalcJvm(i, j, k);
		long  now1 = System.currentTimeMillis();

		System.out.printf("JNI result: %.6f, %d ms.\n", test0, (now0 - then0));
		System.out.printf("JVM result: %.6f, %d ms.\n", test1, (now1 - then1));

		System.out.println("======");
	}
}
