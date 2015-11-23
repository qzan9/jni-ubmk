package ac.ncic.syssw.jni.ubmk;

public final class JniUbmk {
	static {
		System.loadLibrary("jni-ubmk");
	}

	/**
	 * simply return ZER0; consider it as an empty JNI call.
	 */
	public static native int emptyJniCall();

	public static int emptyJvmMethod() {
		return 0;
	}
}
