package ac.ncic.syssw.jni.ubmk;

import static ac.ncic.syssw.jni.ubmk.JniUbmk.*;

public class Main {
	private static int testJNI() {
		return zero();
	}

	private static int testJVM() {
		return 0;
	}

	public static void main(String[] args) {
		long then0 = System.currentTimeMillis();
		int resultJVM = testJVM();
		long now0 = System.currentTimeMillis();

		long then1 = System.currentTimeMillis();
		int resultJNI = testJNI();
		long now1 = System.currentTimeMillis();

		if (resultJNI == 0 && resultJNI == 0) {
			System.out.println("test correct.");
			System.out.println("elapsed time 0: " + (now0 - then0));
			System.out.println("elapsed time 1: " + (now1 - then1));
		}
	}
}
