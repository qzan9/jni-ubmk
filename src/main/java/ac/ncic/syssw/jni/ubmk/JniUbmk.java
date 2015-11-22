package ac.ncic.syssw.jni.ubmk;

public final class JniUbmk {
	static {
		System.loadLibrary("jni-ubmk");
	}

	public static native int zero();
}
