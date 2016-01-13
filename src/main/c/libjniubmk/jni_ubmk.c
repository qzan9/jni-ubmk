/*
 * micro-benchmarking JNI characteristics.
 *
 * Author(s):
 *     azq    @qzan9    anzhongqi@ncic.ac.cn
 */

#ifdef __GNUC__
#	define _SVID_SOURCE
#endif /* __GNUC__ */

#include <stdio.h>
#include <math.h>

#include "jni_ubmk.h"

static const JNINativeMethod methods[] = {
	{       "emptyJniCall", "()V"     , (void *)emptyJniCall       },
	{ "emptyJniCallParam5", "(JIIII)I", (void *)emptyJniCallParam5 },
	{        "someCalcJni", "(III)D"  , (void *)someCalcJni        },
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
	JNIEnv *env = NULL;

//	if (jvm->GetEnv((void **)&env, JNI_VERSION_1_6) != JNI_OK)
	if ((*jvm)->GetEnv(jvm, (void **)&env, JNI_VERSION_1_6) != JNI_OK) {
		return JNI_ERR;
	}

//	if (env->RegisterNatives(env->FindClass("Lac/ncic/syssw/jni/JniUbmk;"),
	if ((*env)->RegisterNatives(env,
	                            (*env)->FindClass(env, "Lac/ncic/syssw/jni/JniUbmk;"),
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

JNIEXPORT void JNICALL emptyJniCall(JNIEnv *env, jobject thisObj)
{
	return;
}

JNIEXPORT jint JNICALL emptyJniCallParam5(JNIEnv *env, jobject thisObj,
                                          jlong param0, jint param1, jint param2, jint param3, jint param4)
{
	return (jint)0;
}

JNIEXPORT jdouble JNICALL someCalcJni(JNIEnv *env, jobject thisObj, jint x, jint y, jint z)
{
	int i, j, k;
	double ret = 0.0;
	for (i = 0; i < x; i++) {
		for (j = 0; j < y; j++) {
			for (k = 0; k < z; k++) {
				ret += (double)((i + j * sin(k))/* / (i * j * k)*/);
			}
		}
	}

	return (jdouble)ret;
}
