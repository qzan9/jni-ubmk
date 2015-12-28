#ifndef _JNI_UBMK_H_
#define _JNI_UBMK_H_

#ifdef __GNUC__
#	define _SVID_SOURCE
#endif /* __GNUC__ */

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *, void *);
//JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *, void *);

JNIEXPORT jint JNICALL emptyJniCall(JNIEnv *, jobject);
JNIEXPORT jdouble JNICALL someCalcJni(JNIEnv *, jobject, jint, jint, jint);

#ifdef __cplusplus
}
#endif

#endif  /* _JNI_UBMK_H_ */

