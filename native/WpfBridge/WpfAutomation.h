/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
#pragma once

public ref class WpfAutomation
{
public:
	WpfAutomation(void);
	void setFrameworkId(System::String ^propertyValue); //default is WPF, but also accepts Silverlight, Win32	
	void setTouchableOnly(System::Boolean val); //default is true
	
	//Descendants will walk the full tree of windows, NOT just one level of children
	System::Int32 countDescendantWindows();
	System::Int32 countDescendantWindows(System::String ^runtimeIdValue);
	
	System::Int32 countChildrenWindows();
	System::Int32 countChildrenWindows(System::String ^runtimeIdValue);
	
	array<System::String ^> ^ enumChildrenWindowIds(System::String ^runtimeIdValue); //if runtimeIdValue is null will start at desktop
	array<System::String ^> ^ enumDescendantWindowIds(System::String ^runtimeIdValue); //if runtimeIdValue is null will start at desktop
	array<System::String ^> ^ enumDescendantWindowIds(System::Int32 processId);
	//In all the above Enumerate methods will return a list of Runtime Ids for all related windows.
	array<System::String ^> ^ enumDescendantWindowInfo(System::String ^runtimeIdValue, System::String ^properties);

	System::String ^ getRuntimeIdFromHandle(System::IntPtr windowHandle);
	System::String ^ getRuntimeIdFromPoint(System::Int32 x, System::Int32 y);
	System::String ^ getParentRuntimeId(System::String ^runtimeIdValue);
	System::String ^ getProperty(System::String ^propertyName, System::String ^runtimeIdValue);
	array<System::String ^> ^ getProperties(System::String ^runtimeIdValue);
	array<System::String ^> ^ getPropertiesAndValues(System::String ^runtimeIdValue);
private:
	array<System::Int32> ^ convertRuntimeIdString(System::String ^runtimeIdValue);
	System::Windows::Automation::AutomationElement ^ findAutomationElementById(System::String ^runtimeIdValue);
	System::String ^ getRuntimeIdFromElement(System::Windows::Automation::AutomationElement ^element);
	array<System::String ^> ^ getRuntimeIdsFromCollection(System::Windows::Automation::AutomationElementCollection ^collection);
	System::Windows::Automation::Condition ^ getSearchConditions();

	static System::String ^DEFAULT_FRAMEWORK = L"WPF";
	static System::Boolean ^DEFAULT_TOUCHABLE = true;
	System::String ^frameworkId;
	System::Boolean ^touchableOnly;
};

