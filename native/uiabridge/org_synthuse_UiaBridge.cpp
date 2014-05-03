/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
#include "stdafx.h"
#include <msclr/marshal.h> //using namespace msclr::interop;
#include "org_synthuse_UiaBridge.h"
#include "uiabridge.h"
#include "Global.h"

using namespace System;
using namespace System::Windows::Automation;
using namespace msclr::interop;
using namespace Globals;
using namespace uiabridge;


/*
 * Class:     org_synthuse_UiaBridge
 * Method:    initialize
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_synthuse_UiaBridge_initialize(JNIEnv *env, jobject obj, jstring jproperties)
{
	const char *properties = env->GetStringUTFChars(jproperties, 0);//convert string
	Global::AUTO_BRIDGE = gcnew AutomationBridge(marshal_as<String ^>(properties));
}

/*
 * Class:     org_synthuse_UiaBridge
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_synthuse_UiaBridge_shutdown(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     org_synthuse_UiaBridge
 * Method:    addEnumFilter
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_synthuse_UiaBridge_addEnumFilter(JNIEnv *env, jobject obj, jstring jpropertyName, jstring jpropertyValue)
{
	const char *propertyName = env->GetStringUTFChars(jpropertyValue, 0);//convert string
	const char *propertyValue = env->GetStringUTFChars(jpropertyValue, 0);//convert string
	return (jint)Global::AUTO_BRIDGE->addEnumFilter(marshal_as<String ^>(propertyName), marshal_as<String ^>(propertyValue));
}

/*
 * Class:     org_synthuse_UiaBridge
 * Method:    clearEnumFilters
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_synthuse_UiaBridge_clearEnumFilters(JNIEnv *env, jobject obj)
{
	Global::AUTO_BRIDGE->clearEnumFilters();
}

/*
 * Class:     org_synthuse_UiaBridge
 * Method:    enumWindowInfo
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_synthuse_UiaBridge_enumWindowInfo__Ljava_lang_String_2(JNIEnv *env, jobject obj, jstring jproperties)
{
	const char *properties = env->GetStringUTFChars(jproperties, 0);//convert string
	array<System::String ^> ^mwinInfo = Global::AUTO_BRIDGE->enumWindowInfo(marshal_as<String ^>(properties));
	if (mwinInfo == nullptr)
		return NULL;
	//create result object array to the same size as the managed children Ids string array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray results = env->NewObjectArray(mwinInfo->Length, stringClass, 0);
	marshal_context context; //lets you marshal managed classes to unmanaged types
	//char **childrenIds = new char *[mchildrenIds->Length];
	for(int i = 0 ; i < mwinInfo->Length ; i++)
	{
		//childrenIds[i] = (char *)context.marshal_as<const char *>(mchildrenIds[i]);
		//env->SetObjectArrayElement(results, i, env->GetStringUTFChars(childrenIds[i], 0)
		env->SetObjectArrayElement(results, i, env->NewStringUTF(context.marshal_as<const char *>(mwinInfo[i])));
	}
	//delete[] childrenIds;
	env->ReleaseStringUTFChars(jproperties, properties); //release string
	return results;
}

/*
 * Class:     org_synthuse_UiaBridge
 * Method:    enumWindowInfo
 * Signature: (ILjava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_synthuse_UiaBridge_enumWindowInfo__ILjava_lang_String_2(JNIEnv *env, jobject obj, jint jwindowHandle, jstring jproperties)
{
	const char *properties = env->GetStringUTFChars(jproperties, 0);//convert string
	array<System::String ^> ^mwinInfo = Global::AUTO_BRIDGE->enumWindowInfo(System::IntPtr(jwindowHandle), marshal_as<String ^>(properties));
	if (mwinInfo == nullptr)
		return NULL;
	//create result object array to the same size as the managed children Ids string array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray results = env->NewObjectArray(mwinInfo->Length, stringClass, 0);
	marshal_context context; //lets you marshal managed classes to unmanaged types
	//char **childrenIds = new char *[mchildrenIds->Length];
	for(int i = 0 ; i < mwinInfo->Length ; i++)
	{
		//childrenIds[i] = (char *)context.marshal_as<const char *>(mchildrenIds[i]);
		//env->SetObjectArrayElement(results, i, env->GetStringUTFChars(childrenIds[i], 0)
		env->SetObjectArrayElement(results, i, env->NewStringUTF(context.marshal_as<const char *>(mwinInfo[i])));
	}
	//delete[] childrenIds;
	env->ReleaseStringUTFChars(jproperties, properties); //release string
	return results;
}

/*
 * Class:     org_synthuse_UiaBridge
 * Method:    getWindowInfo
 * Signature: (IILjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_synthuse_UiaBridge_getWindowInfo__IILjava_lang_String_2(JNIEnv *env, jobject obj, jint jx, jint jy, jstring jproperties)
{
	const char *properties = env->GetStringUTFChars(jproperties, 0);//convert string
	System::String ^mwinInfo = Global::AUTO_BRIDGE->getWindowInfo(jx, jy, marshal_as<String ^>(properties));
	env->ReleaseStringUTFChars(jproperties, properties); //release string
	marshal_context context;
	return env->NewStringUTF(context.marshal_as<const char *>(mwinInfo));
}

/*
 * Class:     org_synthuse_UiaBridge
 * Method:    getWindowInfo
 * Signature: (ILjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_synthuse_UiaBridge_getWindowInfo__ILjava_lang_String_2(JNIEnv *env, jobject obj, jint jwindowHandle, jstring jproperties)
{
	const char *properties = env->GetStringUTFChars(jproperties, 0);//convert string
	System::String ^mwinInfo = Global::AUTO_BRIDGE->getWindowInfo(System::IntPtr(jwindowHandle), marshal_as<String ^>(properties));
	env->ReleaseStringUTFChars(jproperties, properties); //release string
	marshal_context context;
	return env->NewStringUTF(context.marshal_as<const char *>(mwinInfo));
}

/*
 * Class:     org_synthuse_UiaBridge
 * Method:    getWindowInfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_synthuse_UiaBridge_getWindowInfo__Ljava_lang_String_2Ljava_lang_String_2(JNIEnv *env, jobject obj, jstring jruntimeIdStr, jstring jproperties)
{
	const char *properties = env->GetStringUTFChars(jproperties, 0);//convert string
	const char *runtimeIdStr = env->GetStringUTFChars(jruntimeIdStr, 0);//convert string
	System::String ^mwinInfo = Global::AUTO_BRIDGE->getWindowInfo(marshal_as<String ^>(runtimeIdStr), marshal_as<String ^>(properties));
	env->ReleaseStringUTFChars(jruntimeIdStr, runtimeIdStr); //release string
	env->ReleaseStringUTFChars(jproperties, properties); //release string
	marshal_context context;
	return env->NewStringUTF(context.marshal_as<const char *>(mwinInfo));
}
