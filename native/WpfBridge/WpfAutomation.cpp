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
    this->touchableOnly = true;
}

void WpfAutomation::setFrameworkId(System::String ^propertyValue)
{
	this->frameworkId = propertyValue;
}

void WpfAutomation::setTouchableOnly(System::Boolean val)
{
	this->touchableOnly = val;
}

System::Windows::Automation::Condition ^ WpfAutomation::getSearchConditions()
{
	array<System::Windows::Automation::Condition ^> ^cons = nullptr;
	if (this->touchableOnly) 
	{
		cons = gcnew array<System::Windows::Automation::Condition ^>(3);
		cons[0] = gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId);//is WPF framework
		cons[1] = gcnew PropertyCondition(AutomationElement::IsEnabledProperty, true);//is enabled
		cons[2] = gcnew PropertyCondition(AutomationElement::IsOffscreenProperty, false);// is off screen
	}
	else
	{
		cons = gcnew array<System::Windows::Automation::Condition ^>(1);
		cons[0] = gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId);//is WPF framework
	}
	System::Windows::Automation::Condition ^result = gcnew System::Windows::Automation::AndCondition(cons);
	return result;
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

AutomationElement ^ WpfAutomation::findAutomationElementById(System::String ^runtimeIdValue, System::Boolean unfiltered)
{
	if (runtimeIdValue == nullptr || runtimeIdValue->Equals(L""))
		return AutomationElement::RootElement;
	array<System::Int32> ^idArray = this->convertRuntimeIdString(runtimeIdValue);
    if (unfiltered)
        return AutomationElement::RootElement->FindFirst(TreeScope::Descendants, gcnew PropertyCondition(AutomationElement::RuntimeIdProperty, idArray));
	//Condition ^pcFramework = gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId);
	Condition ^pcRunId = gcnew PropertyCondition(AutomationElement::RuntimeIdProperty, idArray);
	Condition ^frameworkAndRuntimeId = gcnew AndCondition(getSearchConditions(), pcRunId);
	return AutomationElement::RootElement->FindFirst(TreeScope::Descendants, frameworkAndRuntimeId);
}

array<System::String ^> ^ WpfAutomation::getRuntimeIdsFromCollection(System::Windows::Automation::AutomationElementCollection ^collection)
{
	array<System::String ^> ^idStrArray = gcnew array<System::String ^>(collection->Count);
	System::Int32 count = 0;
	for each(AutomationElement ^child in collection) 
	{
		idStrArray[count] = getRuntimeIdFromElement(child);
		++count;
	}
	return idStrArray;
}

System::String ^ WpfAutomation::getRuntimeIdFromElement(System::Windows::Automation::AutomationElement ^element)
{
	System::String ^result = L"";
	System::Object ^currentVal = element->GetCurrentPropertyValue(AutomationElement::RuntimeIdProperty);
	if (currentVal != nullptr)
	{
		array<System::Int32> ^idArray = (array<System::Int32> ^)currentVal;
		for each(System::Int32 val in idArray)
		{
			result += System::Convert::ToString(val) + L"-";
		}
		result = result->TrimEnd('-');
		//System::Console::WriteLine("id: {0}", result);
	}
	return result;
}
	
//Descendants will walk the full tree of windows, NOT just one level of children
System::Int32 WpfAutomation::countDescendantWindows()
{
	//AutomationElementCollection ^aec = rootElem->FindAll(TreeScope::Children, Condition::TrueCondition);
	AutomationElementCollection ^aec = AutomationElement::RootElement->FindAll(TreeScope::Descendants, getSearchConditions());
	if (aec == nullptr)
		return 0;
	System::Int32 result = aec->Count;
	//delete aec;
	return result;
}

System::Int32 WpfAutomation::countDescendantWindows(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue, true);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Descendants, getSearchConditions());
	if (aec == nullptr)
		return 0;
	System::Int32 result = aec->Count;
	//delete aec;
	//delete frameworkAndRuntimeId;
	return result;
}
	
System::Int32 WpfAutomation::countChildrenWindows()
{
	//AutomationElementCollection ^aec = rootElem->FindAll(TreeScope::Children, Condition::TrueCondition);
	AutomationElementCollection ^aec = AutomationElement::RootElement->FindAll(TreeScope::Children, getSearchConditions());
	if (aec == nullptr)
		return 0;
	System::Int32 result = aec->Count;
	//delete aec;
	return result;
}

System::Int32 WpfAutomation::countChildrenWindows(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue, true);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Children, getSearchConditions());
	if (aec == nullptr)
		return 0;
	System::Int32 result = aec->Count;
	//delete aec;
	//delete frameworkAndRuntimeId;
	return result;
}
	
array<System::String ^> ^ WpfAutomation::enumChildrenWindowIds(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue, true);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Children, getSearchConditions());
	if (aec == nullptr)
		return nullptr;
	return getRuntimeIdsFromCollection(aec);
}

array<System::String ^> ^ WpfAutomation::enumDescendantWindowIds(System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue, true);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Descendants, getSearchConditions());
	if (aec == nullptr)
		return nullptr;
	return getRuntimeIdsFromCollection(aec);
}

array<System::String ^> ^ WpfAutomation::enumDescendantWindowIds(System::Int32 processId)
{
	Condition ^frameworkAndProcessId = gcnew AndCondition(
		gcnew PropertyCondition(AutomationElement::FrameworkIdProperty, this->frameworkId),
		gcnew PropertyCondition(AutomationElement::ProcessIdProperty, processId));
	AutomationElement ^parent = AutomationElement::RootElement->FindFirst(TreeScope::Descendants, frameworkAndProcessId);
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Descendants, getSearchConditions());
	if (aec == nullptr)
		return nullptr;
	return getRuntimeIdsFromCollection(aec);
}

System::String ^ WpfAutomation::getRuntimeIdFromHandle(System::IntPtr windowHandle)
{
	//AutomationElement test = AutomationElement.FromHandle(new System.IntPtr(123123));
	AutomationElement ^target = AutomationElement::FromHandle(windowHandle);
	if (target == nullptr)
		return nullptr;
	return getRuntimeIdFromElement(target);
}

array<System::String ^> ^ WpfAutomation::enumDescendantWindowInfo(System::String ^runtimeIdValue, System::String ^properties)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue, true);
    if (parent == nullptr)
        return nullptr;
	AutomationElementCollection ^aec = parent->FindAll(TreeScope::Descendants, getSearchConditions());
    
    //create array for keeping order of properties
	System::String ^delim = L",";
	array<System::String ^> ^propSpltArray = properties->Split(delim->ToCharArray());
	TreeWalker ^tw = TreeWalker::ControlViewWalker;

	array<System::String ^> ^winInfoList = gcnew array<System::String ^>(aec->Count);
	System::Int32 count = 0;
	for each(AutomationElement ^child in aec) //loop through all descendants
	{
		array<AutomationProperty^> ^aps = child->GetSupportedProperties();
		array<System::String ^> ^propValues = gcnew array<System::String ^>(propSpltArray->Length);//keep order
		for(int i=0 ; i < propValues->Length ; i++)
		{
			propValues[i] = L"";
			if (propSpltArray[i]->Equals("ParentRuntimeIdProperty"))//custom property for getting parent
			{
				propValues[i] = getRuntimeIdFromElement(tw->GetParent(child));
			}
		}
		for each(AutomationProperty ^ap in aps) //loop through all supported Properties for a child
		{
			System::String ^currentPropertyStr = L""; //current property values
			//System::Console::WriteLine("property: {0}", ap->ProgrammaticName);
			System::String ^shortPropName = L" null ";
			if (ap->ProgrammaticName->Contains(L"."))
				shortPropName = ap->ProgrammaticName->Substring(ap->ProgrammaticName->IndexOf(L".") + 1);
			if (properties->Contains(shortPropName) || properties->Contains(ap->ProgrammaticName) || ap->ProgrammaticName->Equals(properties))
			{
				//System::Console::WriteLine("shortPropName: {0}", shortPropName);
				System::Object ^currentVal = child->GetCurrentPropertyValue(ap);
				if (currentVal == nullptr)
					continue;
				if (ap->ProgrammaticName->Equals(L"AutomationElementIdentifiers.RuntimeIdProperty"))
				{
					array<System::Int32> ^idArray = (array<System::Int32> ^)currentVal;
					for each(System::Int32 val in idArray)
					{
						currentPropertyStr += System::Convert::ToString(val) + L"-";
					}
					currentPropertyStr = currentPropertyStr->TrimEnd('-');
					//System::Console::WriteLine("id: {0}", result);
				}
				else//not runtimeId which is an Int32[]
				{
					currentPropertyStr = currentVal->ToString();
                    currentPropertyStr = currentPropertyStr->Replace(",","&#44;");
				}
			}
			if (currentPropertyStr->Equals(L"")) //if there isn't a value skip
			    continue;
			//System::Console::WriteLine("currentPropertyStr: {0}", currentPropertyStr);
			//find the correct order to return this property
			for(int i=0 ; i < propSpltArray->Length ; i++)
			{
			    if (propSpltArray[i]->Equals(shortPropName) || propSpltArray[i]->Equals(ap->ProgrammaticName))
			        propValues[i] = currentPropertyStr;
			}
		}
		//output properties in the correct order
		for(int i=0 ; i < propSpltArray->Length ; i++)
			winInfoList[count] += propValues[i] + L",";
		++count;
	}
	return winInfoList;
}

System::String ^ WpfAutomation::getRuntimeIdFromPoint(System::Int32 x, System::Int32 y)
{
	AutomationElement ^target = AutomationElement::FromPoint(System::Windows::Point(x,y));
	if (target == nullptr)
		return nullptr;
	return getRuntimeIdFromElement(target);
}

System::String ^ WpfAutomation::getParentRuntimeId(System::String ^runtimeIdValue)
{
	AutomationElement ^target = findAutomationElementById(runtimeIdValue, true);
	if (target == nullptr)
		return nullptr;
	TreeWalker ^tw = TreeWalker::ControlViewWalker;
	AutomationElement ^parent = tw->GetParent(target);
	return getRuntimeIdFromElement(parent);
}

System::String ^ WpfAutomation::getProperty(System::String ^propertyName, System::String ^runtimeIdValue)
{
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue, true);
	if (parent == nullptr)
		return nullptr;
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
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue, true);
	if (parent == nullptr)
		return nullptr;
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
	AutomationElement ^parent = findAutomationElementById(runtimeIdValue, true);
	if (parent == nullptr)
		return nullptr;
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
