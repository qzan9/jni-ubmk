#ifdef __GNUC__
#	define _SVID_SOURCE
#endif /* __GNUC__ */

#include <stdio.h>
#include <math.h>

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *, void *);
//JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *, void *);

JNIEXPORT int JNICALL emptyJniCall(JNIEnv *, jobject);
JNIEXPORT jdouble JNICALL someCalcJni(JNIEnv *, jobject, jint, jint, jint);

#ifdef __cplusplus
}
#endif

static const JNINativeMethod methods[] = {
	{ "emptyJniCall", "()I"   , (void *)emptyJniCall },
	{ "someCalcJni" , "(III)D", (void *)someCalcJni  },
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
	JNIEnv *env = NULL;

//	if (jvm->GetEnv((void **)&env, JNI_VERSION_1_6) != JNI_OK)
	if ((*jvm)->GetEnv(jvm, (void **)&env, JNI_VERSION_1_6) != JNI_OK) {
		return JNI_ERR;
	}

//	if (env->RegisterNatives(env->FindClass("Lac/ncic/syssw/jni/ubmk/JniUbmk;"),
	if ((*env)->RegisterNatives(env,
	                            (*env)->FindClass(env, "Lac/ncic/syssw/jni/ubmk/JniUbmk;"),
	                            methods,
	                            sizeof(methods) / sizeof(methods[0])
	                           ) < -1) {
		return JNI_ERR;
	}

	return JNI_VERSION_1_6;
}

//JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved)
//{
//}

JNIEXPORT int JNICALL emptyJniCall(JNIEnv *env, jobject this)
{
	return 0;
}

JNIEXPORT jdouble JNICALL someCalcJni(JNIEnv *env, jobject this, jint i, jint j, jint k)
{
	int l, m, n;
	double ret = 0.0;
	for (l = 0; l < i; l++) {
		for (m = 0; m < j; m++) {
			for (n = 0; n < k; n++) {
				ret += (double)((l + m * sin(n))/* / (l * m * n)*/);
			}
		}
	}

	return (jdouble)ret;
}
