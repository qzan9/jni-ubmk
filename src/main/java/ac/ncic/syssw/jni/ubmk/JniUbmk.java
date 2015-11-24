package ac.ncic.syssw.jni.ubmk;

public final class JniUbmk {
	static {
		System.loadLibrary("jni-ubmk");
	}

	/**
	 * simply return ZER0 from native binaries; consider it as an empty JNI call.
	 */
	public static native int emptyJniCall();

	/**
	 * return ZER0; this could be optimized by the JVM compiler.
	 */
	public static int emptyJvmMethod() {
		return 0;
	}

	/**
	 * do a three-level loop calculation.
	 */
	public static native double someCalcJni(int i, int j, int k);

	public static double someCalcJvm(int i, int j, int k) {
		double ret = 0.0;
		for (int l = 0; l < i; l++) {
			for (int m = 0; m < j; m++) {
				for (int n = 0; n < k; n++) {
					ret += (double)((l + m * Math.sin(n))/* / (l * m * n)*/);
				}
			}
		}

		return ret;
	}
}
