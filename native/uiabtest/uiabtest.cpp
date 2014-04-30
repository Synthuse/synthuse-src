/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
// uiabtest.cpp : main project file.

#include "stdafx.h"

using namespace System;
using namespace System::Windows::Automation;
using namespace uiabridge;

void outputResults(array<System::String ^> ^winInfo)
{
	System::IO::StreamWriter ^file = gcnew System::IO::StreamWriter("c:\\temp.txt");
	for each(System::String ^prop in winInfo)
	{
		Console::WriteLine(prop);
		file->WriteLine(prop);
	}
	file->Flush();
	file->Close();

}

int main(array<System::String ^> ^args)
{
    Console::WriteLine(L"UI Automation Bridge Test");
	AutomationBridge ^ab = gcnew AutomationBridge();
	System::String ^propList = L"RuntimeIdProperty,ParentRuntimeIdProperty,NativeWindowHandleProperty,ProcessIdProperty,FrameworkIdProperty,LocalizedControlTypeProperty,ClassNameProperty,NameProperty";
	//System::String ^propList = L"RuntimeIdProperty,BoundingRectangleProperty";
	Console::WriteLine(propList);
	//System::String ^winInfo1 = ab->getWindowInfo(System::IntPtr(3409618), propList);

	System::DateTime start = System::DateTime::Now;
	//ab->addEnumFilter("Parent/ClassNameProperty", "Notepad");
	//ab->addEnumFilter("First/RuntimeIdProperty", "42-4784952");
	//ab->addEnumFilter("ClassNameProperty", "Notepad");
	//ab->addEnumFilter("All/ClassNameProperty", "Edit");
	//ab->addEnumFilter("All/LocalizedControlTypeProperty", "menu item");
	ab->addEnumFilter("Parent/FrameworkIdProperty", "WinForm");
	//ab->addEnumFilter("Parent/FrameworkIdProperty", "WPF");
	//ab->addEnumFilter("Parent/ClassNameProperty", "WindowsForms10.Window.8.app.0.2bf8098_r13_ad1");
	array<System::String ^> ^winInfo = ab->enumWindowInfo(propList); //L"*"
	//ab->clearEnumFilters();
	//ab->addEnumFilter("Parent/FrameworkIdProperty", "WinForm");
	//array<System::String ^> ^winInfo = ab->enumWindowInfo(System::IntPtr(3409618), propList); //L"*"
	//array<System::String ^> ^winInfo = ab->enumWindowInfo(System::IntPtr(12977932), propList); //L"*"
	//Console::WriteLine("enumWindowInfo x,y: {0}", ab->getWindowInfo(100,100, propList); //L"*"

	outputResults(winInfo);
	//Globals::Global::AUTO_BRIDGE->clearEnumFilters();
	//winInfo = nullptr;
	//winInfo = Globals::Global::AUTO_BRIDGE->enumWindowInfo(System::IntPtr(7603636), propList);
	//Console::WriteLine("winInfo length: {0}", winInfo->Length);
	//winInfo = Globals::Global::AUTO_BRIDGE->enumWindowInfo(System::IntPtr(7603636), propList);
	//Console::WriteLine("winInfo length: {0}", winInfo->Length);

	//Console::WriteLine("getWindowInfo RuntimeIdProperty: {0}", ab->getWindowInfo("42-4784952", propList)); 

	//System::Threading::Thread::Sleep(10000); //10 seconds sleep
	//array<System::String ^> ^winInfo2 = ab->enumWindowInfo(propList); //L"*"
	//outputResults(winInfo2);

	System::Double seconds = System::Math::Round(System::DateTime::Now.Subtract(start).TotalSeconds, 4);
	Console::WriteLine(L"Total Elements: {0} in {1} seconds", winInfo->Length, seconds);
	getch();//wait for user input
    return 0;
}
