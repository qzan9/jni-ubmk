package ac.ncic.syssw.jni;

public class RunBmk {

	public static final int DEFAULT_WARMUP_ITER = 10000;
	public static final int DEFAULT_BMK_ITER    = 1000;

	private RunBmk() { }

	private static RunBmk INSTANCE;
	public  static RunBmk getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new RunBmk();
		}
		return INSTANCE;
	}

	public void uBmkEmptyCall() {

		long startTime, elapsedTime;
		int sum;

		System.out.println("=== JNI invocation micro-benchmark ===");

		System.out.println("\nwarming up ...");
		for (int t = 0; t < DEFAULT_WARMUP_ITER; t++) {
			JniUbmk.emptyJniCall();
			System.out.printf("%d ", JniUbmk.emptyJniCallParam5(t, t + 1, t + 2, t + 3, t + 4));
		}
		System.out.println();

		System.out.println("\nstart benchmarking ...");
		startTime = System.nanoTime();
		for (int t = 0; t < DEFAULT_BMK_ITER; t++) {
			JniUbmk.emptyJniCall();
		}
		elapsedTime = System.nanoTime() - startTime;
		System.out.printf("average time of calling emptyJniCall is %.1f ns.\n", (double) elapsedTime / 1000);
		sum = 0;
		startTime = System.nanoTime();
		for (int t = 0; t < DEFAULT_BMK_ITER; t++) {
			sum += JniUbmk.emptyJniCallParam5(t, t + 1, t + 2, t + 3, t + 4);
		}
		elapsedTime = System.nanoTime() - startTime;
		System.out.println(sum);
		System.out.printf("average time of calling emptyJniCallParam5 is %.1f ns.\n", (double) elapsedTime / 1000);
	}

	public void uBmkSomeCalc(String[] args) {
		int x, y, z;
		long startTime, elapsedTime;
		double test;

		if (args.length != 3) {
			x = 100; y = 100; z = 10000;
		} else {
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			z = Integer.parseInt(args[2]);
		}

		System.out.println("=== calculation micro-benchmark ===");

		System.out.println("\nwarming up ... ignore the outputs ...");
//		for (int t = 0; t < DEFAULT_WARMUP_ITER; t++) {
		for (int t = 0; t < 10; t++) {
				System.out.println(t + " - " + JniUbmk.someCalcJni(x, y, z) + ", " + JniUbmk.someCalcJvm(x, y, z));
			}

		System.out.println("\nstart benchmarking ...");
//		test = 0.;
		startTime = System.nanoTime();
//		for (int t = 0; t < DEFAULT_BMK_ITER; t++) {
			test = JniUbmk.someCalcJni(x, y, z);
//		}
		elapsedTime = System.nanoTime() - startTime;
		System.out.printf("JNI result: %.6f, %.1f ms\n", test, (double) elapsedTime / 1000 / 1000);
//		test = 0.;
		startTime = System.nanoTime();
//		for (int t = 0; t < DEFAULT_BMK_ITER; t++) {
			test = JniUbmk.someCalcJvm(x, y, z);
//		}
		elapsedTime = System.nanoTime() - startTime;
		System.out.printf("JVM result: %.6f, %.1f ms\n", test, (double) elapsedTime / 1000 / 1000);
	}
}
