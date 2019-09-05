/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_intel_hpnl_core_RdmService */

#ifndef _Included_com_intel_hpnl_core_RdmService
#define _Included_com_intel_hpnl_core_RdmService
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_intel_hpnl_core_RdmService
 * Method:    init
 * Signature: (IIIZLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_intel_hpnl_core_RdmService_init
  (JNIEnv *, jobject, jint, jint, jint, jboolean, jstring);

/*
 * Class:     com_intel_hpnl_core_RdmService
 * Method:    listen
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_hpnl_core_RdmService_listen
  (JNIEnv *, jobject, jstring, jstring, jlong);

/*
 * Class:     com_intel_hpnl_core_RdmService
 * Method:    get_con
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_hpnl_core_RdmService_get_1con
  (JNIEnv *, jobject, jstring, jstring, jlong);

/*
 * Class:     com_intel_hpnl_core_RdmService
 * Method:    wait_event
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_intel_hpnl_core_RdmService_wait_1event
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_intel_hpnl_core_RdmService
 * Method:    wait_event
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_intel_hpnl_core_RdmService_remove_1connection
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     com_intel_hpnl_core_RdmService
 * Method:    set_recv_buffer
 * Signature: (Ljava/nio/ByteBuffer;JIJ)V
 */
JNIEXPORT void JNICALL Java_com_intel_hpnl_core_RdmService_set_1recv_1buffer
  (JNIEnv *, jobject, jobject, jlong, jint, jlong);

/*
 * Class:     com_intel_hpnl_core_RdmService
 * Method:    set_send_buffer
 * Signature: (Ljava/nio/ByteBuffer;JIJ)V
 */
JNIEXPORT void JNICALL Java_com_intel_hpnl_core_RdmService_set_1send_1buffer
  (JNIEnv *, jobject, jobject, jlong, jint, jlong);

/*
 * Class:     com_intel_hpnl_core_RdmService
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_intel_hpnl_core_RdmService_free
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif
