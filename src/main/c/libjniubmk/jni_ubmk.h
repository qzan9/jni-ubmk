/*
 * micro-benchmarking JNI characteristics.
 *
 * Author(s):
 *     azq    @qzan9    anzhongqi@ncic.ac.cn
 */

#ifndef _JNI_UBMK_H_
#define _JNI_UBMK_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *, void *);
//JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *, void *);

JNIEXPORT void JNICALL emptyJniCall(JNIEnv *, jobject);
JNIEXPORT jint JNICALL emptyJniCallParam5(JNIEnv *, jobject, jint, jint, jint, jint, jint);

JNIEXPORT jdouble JNICALL someCalcJni(JNIEnv *, jobject, jint, jint, jint);

#ifdef __cplusplus
}
#endif

#endif  /* _JNI_UBMK_H_ */

