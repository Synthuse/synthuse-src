/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
#include "StdAfx.h"
#include "org_synthuse_WpfBridge.h"
#include "WpfAutomation.h"
#include "Global.h"
#include <msclr/marshal.h> //using namespace msclr::interop;

using namespace System;
using namespace System::Windows::Automation;
using namespace msclr::interop;
using namespace Globals;

JNIEXPORT void JNICALL Java_org_synthuse_WpfBridge_setFrameworkId(JNIEnv *env, jobject obj, jstring jpropertyValue)
{
	const char *propertyValue = env->GetStringUTFChars(jpropertyValue, 0);//convert string
	Global::WPF_AUTO->setFrameworkId(marshal_as<String ^>(propertyValue));
	env->ReleaseStringUTFChars(jpropertyValue, propertyValue); //release string
}

/*
 * Class:     org_synthuse_WpfBridge
 * Method:    setTouchableOnly
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_org_synthuse_WpfBridge_setTouchableOnly(JNIEnv *env, jobject obj, jboolean jval)
{
    Global::WPF_AUTO->setTouchableOnly((bool)(jval == JNI_TRUE));
}

/*
 * Class:     org_synthuse_WpfBridge
 * Method:    CountDescendantWindows
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_synthuse_WpfBridge_countDescendantWindows__(JNIEnv *env, jobject obj)
{
	return Global::WPF_AUTO->countDescendantWindows();
}

/*
 * Class:     org_synthuse_WpfBridge
 * Method:    CountDescendantWindows
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_synthuse_WpfBridge_countDescendantWindows__Ljava_lang_String_2(JNIEnv *env, jobject obj, jstring jruntimeIdValue)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	jint result = Global::WPF_AUTO->countDescendantWindows(marshal_as<String ^>(runtimeIdValue));
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return result;
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    CountChildrenWindows
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_synthuse_WpfBridge_countChildrenWindows__(JNIEnv *env, jobject obj)
{
	return Global::WPF_AUTO->countChildrenWindows();
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    CountChildrenWindows
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_synthuse_WpfBridge_countChildrenWindows__Ljava_lang_String_2(JNIEnv *env, jobject obj, jstring jruntimeIdValue)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	jint result = Global::WPF_AUTO->countChildrenWindows(marshal_as<String ^>(runtimeIdValue));
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return result;
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    EnumChildrenWindowIds
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_synthuse_WpfBridge_enumChildrenWindowIds(JNIEnv *env, jobject obj, jstring jruntimeIdValue)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	array<System::String ^> ^mchildrenIds = Global::WPF_AUTO->enumChildrenWindowIds(marshal_as<String ^>(runtimeIdValue));
	if (mchildrenIds == nullptr)
		return NULL;
	//create result object array to the same size as the managed children Ids string array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray results = env->NewObjectArray(mchildrenIds->Length, stringClass, 0);
	marshal_context context; //lets you marshal managed classes to unmanaged types
	//char **childrenIds = new char *[mchildrenIds->Length];
	for(int i = 0 ; i < mchildrenIds->Length ; i++)
	{
		//childrenIds[i] = (char *)context.marshal_as<const char *>(mchildrenIds[i]);
		//env->SetObjectArrayElement(results, i, env->GetStringUTFChars(childrenIds[i], 0)
		env->SetObjectArrayElement(results, i, env->NewStringUTF(context.marshal_as<const char *>(mchildrenIds[i])));
	}
	//delete[] childrenIds;
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return results;
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    EnumDescendantWindowIds
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_synthuse_WpfBridge_enumDescendantWindowIds__Ljava_lang_String_2(JNIEnv *env, jobject obj, jstring jruntimeIdValue)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	array<System::String ^> ^mchildrenIds = Global::WPF_AUTO->enumDescendantWindowIds(marshal_as<String ^>(runtimeIdValue));
	if (mchildrenIds == nullptr)
		return NULL;
	//create result object array to the same size as the managed children Ids string array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray results = env->NewObjectArray(mchildrenIds->Length, stringClass, 0);
	marshal_context context; //lets you marshal managed classes to unmanaged types
	//char **childrenIds = new char *[mchildrenIds->Length];
	for(int i = 0 ; i < mchildrenIds->Length ; i++)
	{
		//childrenIds[i] = (char *)context.marshal_as<const char *>(mchildrenIds[i]);
		//env->SetObjectArrayElement(results, i, env->GetStringUTFChars(childrenIds[i], 0)
		env->SetObjectArrayElement(results, i, env->NewStringUTF(context.marshal_as<const char *>(mchildrenIds[i])));
	}
	//delete[] childrenIds;
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return results;
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    EnumDescendantWindowIds
 * Signature: (J)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_synthuse_WpfBridge_enumDescendantWindowIds__J(JNIEnv *env, jobject obj, jlong jprocessId)
{
	array<System::String ^> ^mchildrenIds = Global::WPF_AUTO->enumDescendantWindowIds((System::Int32)jprocessId);
	if (mchildrenIds == nullptr)
		return NULL;
	//create result object array to the same size as the managed children Ids string array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray results = env->NewObjectArray(mchildrenIds->Length, stringClass, 0);
	marshal_context context; //lets you marshal managed classes to unmanaged types
	for(int i = 0 ; i < mchildrenIds->Length ; i++)
	{
		env->SetObjectArrayElement(results, i, env->NewStringUTF(context.marshal_as<const char *>(mchildrenIds[i])));
	}
	return results;
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    getRuntimeIdFromHandle
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_synthuse_WpfBridge_getRuntimeIdFromHandle(JNIEnv *env, jobject obj, jlong jwindowHandle)
{
	System::String ^mrunId = Global::WPF_AUTO->getRuntimeIdFromHandle(System::IntPtr(jwindowHandle));
	if (mrunId == nullptr)
		return NULL;
	marshal_context context; //lets you marshal managed classes to unmanaged types
	jstring result = env->NewStringUTF(context.marshal_as<const char *>(mrunId));
	return result;
}

/*
 * Class:     org_synthuse_WpfBridge
 * Method:    enumDescendantWindowInfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_synthuse_WpfBridge_enumDescendantWindowInfo(JNIEnv *env, jobject obj, jstring jruntimeIdValue, jstring jproperties)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	const char *properties = env->GetStringUTFChars(jproperties, 0);//convert string
	array<System::String ^> ^mwinInfo = Global::WPF_AUTO->enumDescendantWindowInfo(marshal_as<String ^>(runtimeIdValue), marshal_as<String ^>(properties));
	if (mwinInfo == nullptr)
		return NULL;
	//create result object array to the same size as the managed window info string array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray results = env->NewObjectArray(mwinInfo->Length, stringClass, 0);
	marshal_context context; //lets you marshal managed classes to unmanaged types
	for(int i = 0 ; i < mwinInfo->Length ; i++)
	{
		env->SetObjectArrayElement(results, i, env->NewStringUTF(context.marshal_as<const char *>(mwinInfo[i])));
	}
	env->ReleaseStringUTFChars(jproperties, properties); //release string
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return results;
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    getRuntimeIdFromPoint
 * Signature: (II)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_synthuse_WpfBridge_getRuntimeIdFromPoint(JNIEnv *env, jobject obj, jint x, jint y)
{
	System::String ^mresult = Global::WPF_AUTO->getRuntimeIdFromPoint(x, y);
	if (mresult == nullptr)
		return NULL;
	marshal_context context; //lets you marshal managed classes to unmanaged types
	jstring result = env->NewStringUTF(context.marshal_as<const char *>(mresult));
	return result;
}

/*
 * Class:     org_synthuse_WpfBridge
 * Method:    getParentRuntimeId
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_synthuse_WpfBridge_getParentRuntimeId(JNIEnv *env, jobject obj, jstring jruntimeIdValue)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	System::String ^mresult = Global::WPF_AUTO->getParentRuntimeId(marshal_as<String ^>(runtimeIdValue));
	if (mresult == nullptr)
		return NULL;
	marshal_context context; //lets you marshal managed classes to unmanaged types
	jstring result = env->NewStringUTF(context.marshal_as<const char *>(mresult));
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return result;
}

/*
 * Class:     org_synthuse_WpfBridge
 * Method:    GetProperty
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_synthuse_WpfBridge_getProperty(JNIEnv *env, jobject obj, jstring jpropertyName, jstring jruntimeIdValue)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	const char *propertyName = env->GetStringUTFChars(jpropertyName, 0);//convert string
	System::String ^mresult = Global::WPF_AUTO->getProperty(marshal_as<String ^>(propertyName), marshal_as<String ^>(runtimeIdValue));
	if (mresult == nullptr)
		return NULL;
	marshal_context context; //lets you marshal managed classes to unmanaged types
	jstring result = env->NewStringUTF(context.marshal_as<const char *>(mresult));
	env->ReleaseStringUTFChars(jpropertyName, propertyName); //release string
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return result;
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    GetProperties
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_synthuse_WpfBridge_getProperties(JNIEnv *env, jobject obj, jstring jruntimeIdValue)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	array<System::String ^> ^mprops = Global::WPF_AUTO->getProperties(marshal_as<String ^>(runtimeIdValue));
	if (mprops == nullptr)
		return NULL;
	//create result object array to the same size as the managed children Ids string array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray results = env->NewObjectArray(mprops->Length, stringClass, 0);
	marshal_context context; //lets you marshal managed classes to unmanaged types
	for(int i = 0 ; i < mprops->Length ; i++)
	{
		env->SetObjectArrayElement(results, i, env->NewStringUTF(context.marshal_as<const char *>(mprops[i])));
	}
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return results;
}


/*
 * Class:     org_synthuse_WpfBridge
 * Method:    GetPropertiesAndValues
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_synthuse_WpfBridge_getPropertiesAndValues(JNIEnv *env, jobject obj, jstring jruntimeIdValue)
{
	const char *runtimeIdValue = env->GetStringUTFChars(jruntimeIdValue, 0);//convert string
	array<System::String ^> ^mprops = Global::WPF_AUTO->getPropertiesAndValues(marshal_as<String ^>(runtimeIdValue));
	if (mprops == nullptr)
		return NULL;
	//create result object array to the same size as the managed children Ids string array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray results = env->NewObjectArray(mprops->Length, stringClass, 0);
	marshal_context context; //lets you marshal managed classes to unmanaged types
	for(int i = 0 ; i < mprops->Length ; i++)
	{
		env->SetObjectArrayElement(results, i, env->NewStringUTF(context.marshal_as<const char *>(mprops[i])));
	}
	env->ReleaseStringUTFChars(jruntimeIdValue, runtimeIdValue); //release string
	return results;
}
