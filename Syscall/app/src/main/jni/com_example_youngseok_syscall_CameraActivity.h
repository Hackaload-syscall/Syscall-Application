/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_youngseok_syscall_CameraActivity */

#ifndef _Included_com_example_youngseok_syscall_CameraActivity
#define _Included_com_example_youngseok_syscall_CameraActivity
#ifdef __cplusplus
extern "C" {
#endif
#undef com_example_youngseok_syscall_CameraActivity_PERMISSIONS_REQUEST_CODE
#define com_example_youngseok_syscall_CameraActivity_PERMISSIONS_REQUEST_CODE 1000L
/*
 * Class:     com_example_youngseok_syscall_CameraActivity
 * Method:    ConvertRGBtoGray
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_example_youngseok_syscall_CameraActivity_ConvertRGBtoRGBA
  (JNIEnv *, jobject, jlong, jlong);
JNIEXPORT jlong JNICALL
Java_com_example_youngseok_syscall_CameraActivity_loadCascade(JNIEnv *env, jclass type,
                                                                  jstring cascadeFileName_);

JNIEXPORT void JNICALL
Java_com_example_youngseok_syscall_CameraActivity_detect(JNIEnv *env, jclass type,
                                                             jlong cascadeClassifier_face,
                                                             jlong cascadeClassifier_eye,
                                                             jlong matAddrInput,
                                                             jlong matAddrResult);
#ifdef __cplusplus
}
#endif
#endif
