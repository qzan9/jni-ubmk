package ac.ncic.syssw.jni;

import java.nio.ByteBuffer;
import java.util.Random;

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
			System.out.printf("%d ", JniUbmk.emptyJniCallParam5((long) t, t + 1, t + 2, t + 3, t + 4));
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
			sum += JniUbmk.emptyJniCallParam5(startTime, t + 1, t + 2, t + 3, t + 4);
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

	public void uBmkNewByteArray(String[] args) {
		int sizek, size, iter;
		int i;
		long startTime, elapsedTime;
		byte[] byteArray;
//		byte[] byteArray, byteArrayForBuffer;
		ByteBuffer buffer;
		Random random;

		try {
			if (args.length != 2) {
				sizek = 512;
				iter  = 1024;
			} else {
				sizek = Integer.parseInt(args[0]);
				iter  = Integer.parseInt(args[1]);
			}
		} catch (NumberFormatException e) {
			System.out.println("options: [SIZE_IN_KILO] [ITERATION_NUMBER]");
			System.out.println("using the defaults ...");
			sizek = 512;
			iter  = 1024;
		}

		size = sizek * 1024;
		random = new Random();

		System.out.printf("size: %dKB, iter: %d", sizek, iter);
		System.out.println();

		elapsedTime = 0;
		for (i = 0; i < iter; i++) {
			startTime = System.nanoTime();
			byteArray = new byte[size];
			elapsedTime += System.nanoTime() - startTime;
			random.nextBytes(byteArray);
		}
		System.out.printf("byte array: %.1f ns.", (double) elapsedTime / iter);
		System.out.println();

		elapsedTime = 0;
		byteArray = new byte[size];
//		byteArrayForBuffer = new byte[size*2];
		buffer = ByteBuffer.allocate(size);
//		buffer = ByteBuffer.allocateDirect(size);
//		buffer = ByteBuffer.wrap(byteArrayForBuffer);
		for (i = 0; i < iter; i++) {
			random.nextBytes(byteArray);
			buffer.put(byteArray);
//			random.nextBytes(byteArray);
//			buffer.put(byteArray);
			buffer.rewind();
			startTime = System.nanoTime();
//			buffer.limit(size);
//			byteArray = buffer.array();    // BufferOverflow
			buffer.get(byteArray);
			elapsedTime += System.nanoTime() - startTime;
			buffer.clear();
		}
		System.out.printf("buffer array: %.1f ns.", (double) elapsedTime / iter);
		System.out.println();
	}
}
