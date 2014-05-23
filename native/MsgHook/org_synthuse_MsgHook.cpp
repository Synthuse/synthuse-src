/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/

#include "stdafx.h"
#include "org_synthuse_MsgHook.h"

/*
 * Class:     org_synthuse_MsgHook
 * Method:    setMessageHook
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean JNICALL Java_org_synthuse_MsgHook_setMessageHook(JNIEnv *env, jobject obj, jlong jhWnd, jlong jthreadId)
{
	return SetMsgHook((HWND)jhWnd, (DWORD)jthreadId);
}

/*
 * Class:     org_synthuse_MsgHook
 * Method:    removeMessageHook
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_synthuse_MsgHook_removeMessageHook(JNIEnv *env, jobject obj)
{
	return RemoveHook();
}