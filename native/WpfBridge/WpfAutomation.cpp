/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
#include "StdAfx.h"
#include "WpfAutomation.h"

using namespace System;
using namespace System::Windows::Automation;

WpfAutomation::WpfAutomation(void)
{
	this->frameworkId = WpfAutomation::DEFAULT_FRAMEWORK;
}

void WpfAutomation::setFrameworkId(System::String ^propertyValue)
{
	this->frameworkId = propertyValue;
}

array<System::Int32> ^ WpfAutomation::convertRuntimeIdString(System::String ^runtimeIdValue)
{
	System::String ^delim = L"-";
    array<System::String ^> ^idStrArray = runtimeIdValue->Split(delim->ToCharArray());
    array<System::Int32> ^idArray = gcnew array<System::Int32>(idStrArray->Length);
    for(System::Int32 i = 0 ; i < idStrArray->Length ; i++)
    {
        idArray[i] = System::Int32::Parse(idStrArray[i]);
    }
	return idArray;
}

AutomationElement ^ WpfAutomation::findAutomationElementById(System::String ^runtimeIdValue)
{
	array<System::Int32> ^idArray = this->convertRuntimeIdString(runtimeIdValue);
	Condition ^pcFramework = gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId);
	Condition ^pcRunId = gcnew PropertyCondition(AutomationElement::RuntimeIdProperty, idArray);
	Condition ^frameworkAndRuntimeId = gcnew AndCondition(pcFramework, pcRunId);
	return AutomationElement::RootElement->FindFirst(TreeScope::Descendants, frameworkAndRuntimeId);
}

array<System::String ^> ^ WpfAutomation::getRuntimeIdsFromCollection(System::Windows::Automation::AutomationElementCollection ^collection)
{
	array<System::String ^> ^idStrArray = gcnew array<System::String ^>(collection->Count);
	System::Int32 count = 0;
	for each(AutomationElement ^child in collection) 
	{
		System::Object ^currentVal = child->GetCurrentPropertyValue(AutomationElement::RuntimeIdProperty);
		if (currentVal != nullptr)
		{
			array<System::Int32> ^idArray = (array<System::Int32> ^)currentVal;
			for each(System::Int32 val in idArray)
			{
				idStrArray[count] += System::Convert::ToString(val) + L"-";
				//System::String->Concat(idStrArray[count], System::String->Concat(val->ToString(), L"-"));
			}
			idStrArray[count] = idStrArray[count]->TrimEnd('-');
			//System::Console::WriteLine("id: {0}", idStrArray[count]);
		}
		++count;
	}
	return idStrArray;
}
	
//Descendants will walk the full tree of windows, NOT just one level of children
System::Int32 WpfAutomation::countDescendantWindows()
{
	//AutomationElementCollection ^aec = rootElem->FindAll(TreeScope::Children, Condition::TrueCondition);
	AutomationElementCollection ^aec = AutomationElement::RootElement->FindAll(TreeScope::Descendants, gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId));
	System::Int32 result = aec->Count;
	//delete aec;
	return result;
}

System::Int32 WpfAutomation::countDescendantWindows(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Descendants, gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId));
	System::Int32 result = aec->Count;
	//delete aec;
	//delete frameworkAndRuntimeId;
	return result;
}
	
System::Int32 WpfAutomation::countChildrenWindows()
{
	//AutomationElementCollection ^aec = rootElem->FindAll(TreeScope::Children, Condition::TrueCondition);
	AutomationElementCollection ^aec = AutomationElement::RootElement->FindAll(TreeScope::Children, gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId));
	System::Int32 result = aec->Count;
	//delete aec;
	return result;
}

System::Int32 WpfAutomation::countChildrenWindows(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Children, gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId));
	System::Int32 result = aec->Count;
	//delete aec;
	//delete frameworkAndRuntimeId;
	return result;
}
	
array<System::String ^> ^ WpfAutomation::enumChildrenWindowIds(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Children, gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId));
	return getRuntimeIdsFromCollection(aec);
}

array<System::String ^> ^ WpfAutomation::enumDescendantWindowIds(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Descendants, gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId));
	return getRuntimeIdsFromCollection(aec);
}

array<System::String ^> ^ WpfAutomation::enumDescendantWindowIds(System::Int32 processId)
{
	Condition ^frameworkAndProcessId = gcnew AndCondition(
		gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId),
		gcnew PropertyCondition(AutomationElement::ProcessIdProperty, processId));
	AutomationElement ^parent = AutomationElement::RootElement->FindFirst(TreeScope::Descendants, frameworkAndProcessId);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Descendants, gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId));
	return getRuntimeIdsFromCollection(aec);
}

array<System::String ^> ^ WpfAutomation::EnumDescendantWindowIdsFromHandle(System::IntPtr windowHandle)
{
	//AutomationElement test = AutomationElement.FromHandle(new System.IntPtr(123123));
	AutomationElement ^parent = AutomationElement::FromHandle(windowHandle);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Descendants, gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId));
	return getRuntimeIdsFromCollection(aec);
}

System::String ^ WpfAutomation::getProperty(System::String ^propertyName, System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue);
	//System::Object ^currentVal = parent->GetCurrentPropertyValue(AutomationElement::RuntimeIdProperty);
	array<AutomationProperty^> ^aps = parent->GetSupportedProperties();
	for each(AutomationProperty ^ap in aps)
	{
		//System::Console::WriteLine("property: {0}", ap->ProgrammaticName);
		if (ap->ProgrammaticName->Contains(L"." + propertyName) || ap->ProgrammaticName->Equals(propertyName))
		{
			System::Object ^currentVal = parent->GetCurrentPropertyValue(ap);
			return currentVal->ToString();
		}
	}
	return nullptr;
}

array<System::String ^> ^ WpfAutomation::getProperties(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue);
	array<AutomationProperty^> ^aps = parent->GetSupportedProperties();
	array<System::String ^> ^propStrArray = gcnew array<System::String ^>(aps->Length);
	System::Int32 count = 0;
	for each(AutomationProperty ^ap in aps)
	{
		System::Object ^currentVal = parent->GetCurrentPropertyValue(ap);
		if (currentVal == nullptr)
			continue;
		propStrArray[count] = ap->ProgrammaticName;
		++count;
	}
	return propStrArray;
}

array<System::String ^> ^ WpfAutomation::getPropertiesAndValues(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue);
	array<AutomationProperty^> ^aps = parent->GetSupportedProperties();
	array<System::String ^> ^propStrArray = gcnew array<System::String ^>(aps->Length);
	System::Int32 count = 0;
	for each(AutomationProperty ^ap in aps)
	{
		System::Object ^currentVal = parent->GetCurrentPropertyValue(ap);
		if (currentVal == nullptr)
			continue;
		propStrArray[count] = ap->ProgrammaticName + ":" + currentVal->ToString();
		++count;
	}
	return propStrArray;
}
