package ac.ncic.syssw.jni;

public final class JniUbmk {
	static {
		System.loadLibrary("jniubmk");
	}

	/**
	 * simply return ZER0 from native binaries; consider it as an empty JNI call.
	 */
	public static native void emptyJniCall();
	public static native int  emptyJniCallParam5(int param0, int param1, int param2, int param3, int param4);

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
