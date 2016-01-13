package ac.ncic.syssw.jni;

public final class JniUbmk {
	static {
		System.loadLibrary("jni-ubmk");
	}

	/**
	 * simply return ZER0 from native binaries; consider it as an empty JNI call.
	 */
	public static native int emptyJniCall();
	public static int emptyJvmMethod() {
		return 0;
	}

	/**
	 * do a three-level loop calculation.
	 */
	public static native double someCalcJni(int x, int y, int z);
	public static double someCalcJvm(int x, int y, int z) {
		double ret = 0.0;
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < z; k++) {
					ret += (double)((i + j * Math.sin(k))/* / (i * j * k)*/);
				}
			}
		}

		return ret;
	}
}
