/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
// This is the main DLL file.

#include "stdafx.h"
#include "uiabridge.h"

using namespace System;
using namespace System::Collections::Generic;
using namespace System::Windows::Automation;
using namespace uiabridge;

AutomationBridge::AutomationBridge()
{
	enumFilters = gcnew Dictionary<System::String ^, System::String ^>();
	cacheRequest = nullptr;
	initializeCache("");
}

AutomationBridge::AutomationBridge(System::String ^cachedProperties)
{
	enumFilters = gcnew Dictionary<System::String ^, System::String ^>();
	cacheRequest = nullptr;
	initializeCache(cachedProperties);
}

AutomationBridge::~AutomationBridge()
{
	enumFilters->Clear();
	if (cacheRequest != nullptr)
		cacheRequest->Pop(); //disable UI Automation Cache
	//Console::WriteLine("disposing of AutomationBridge");
}

void AutomationBridge::initializeCache(System::String ^cachedProperties)
{
	cacheRequest = gcnew CacheRequest();
	//cacheRequest->AutomationElementMode = AutomationElementMode::Full;
	cacheRequest->TreeFilter = Automation::RawViewCondition;
	cacheRequest->TreeScope = TreeScope::Element;// | TreeScope::Children;
	/*
	cacheRequest->Add(AutomationElement::RuntimeIdProperty);
	cacheRequest->Add(AutomationElement::ProcessIdProperty);
	cacheRequest->Add(AutomationElement::FrameworkIdProperty);
	cacheRequest->Add(AutomationElement::LocalizedControlTypeProperty);
	cacheRequest->Add(AutomationElement::ControlTypeProperty);
	cacheRequest->Add(AutomationElement::ClassNameProperty);
	cacheRequest->Add(AutomationElement::NameProperty);
	cacheRequest->Add(AutomationElement::BoundingRectangleProperty);
	*/
	System::String ^cachedPropStr = cachedProperties;
	if (cachedPropStr == nullptr) // check if blank/null we need to use DEFAULT_CACHED_PROPS;
		cachedPropStr = DEFAULT_CACHED_PROPS;
	else if (cachedPropStr->Equals(""))
		cachedPropStr = DEFAULT_CACHED_PROPS;
	array<AutomationProperty^> ^rootProperties = AutomationElement::RootElement->GetSupportedProperties();
	List<AutomationProperty^> ^cacheList = gcnew List<AutomationProperty^>();
	if (cachedPropStr->Contains(L"NativeWindowHandleProperty")) //special property not in the root property list
	{
		cacheList->Add(AutomationElement::NativeWindowHandleProperty);
		cacheRequest->Add(AutomationElement::NativeWindowHandleProperty);
	}
	if (cachedPropStr->Contains(L"ValueProperty")) //special property not in the root property list
	{
		cacheList->Add(ValuePattern::ValueProperty);
		cacheRequest->Add(AutomationElement::IsValuePatternAvailableProperty);
		cacheRequest->Add(ValuePattern::ValueProperty);
		cacheRequest->Add(ValuePattern::Pattern);
	}
	for each(AutomationProperty ^ap in rootProperties) //loop through all supported Properties for a child
	{
		System::String ^currentPropertyStr = L""; //current property values
		System::String ^shortPropName = L" null ";
		if (ap->ProgrammaticName->Contains(L".")) //get short Property name
			shortPropName = ap->ProgrammaticName->Substring(ap->ProgrammaticName->IndexOf(L".") + 1);
		if (cachedPropStr->Contains(shortPropName) || cachedPropStr->Contains(ap->ProgrammaticName))
		{
			cacheList->Add(ap);// add property to cachedRootProperties
			cacheRequest->Add(ap); // add property to cacheRequest
			//Console::WriteLine("caching property {0}", ap->ProgrammaticName);
		}
	}
	cachedRootProperties = cacheList->ToArray();
	cacheRequest->Push(); //enable UI Automation Cache
	//cachedRootProperties = AutomationElement::RootElement->GetSupportedProperties();
}

int AutomationBridge::addEnumFilter(System::String ^propertyName, System::String ^propertyValue)
{
	enumFilters->Add(propertyName, propertyValue);
	return enumFilters->Count;
}

void AutomationBridge::clearEnumFilters()
{
	enumFilters->Clear();
}

Boolean AutomationBridge::isElementFiltered(System::Windows::Automation::AutomationElement ^element)
{
	return isElementFiltered(element, nullptr);
}

Boolean AutomationBridge::isElementFiltered(System::Windows::Automation::AutomationElement ^element, List<System::String ^> ^filterModifierList)
{
	Boolean result = false;
	int filterMatchCount = 0;
	if (enumFilters->Count == 0)
		return result;
	//array<AutomationProperty^> ^aps = cachedRootProperties;//element->GetSupportedProperties();
	for each(AutomationProperty ^ap in cachedRootProperties) //loop through all supported Properties for a child
	{
		System::String ^currentPropertyStr = L""; //current property values
		System::String ^shortPropName = L" null ";
		if (ap->ProgrammaticName->Contains(L".")) //get short Property name
			shortPropName = ap->ProgrammaticName->Substring(ap->ProgrammaticName->IndexOf(L".") + 1);
		//System::Console::WriteLine("property: {0}", shortPropName);
		for each(System::String ^key in enumFilters->Keys)
		{
			if (filterModifierList != nullptr)
				if (filterModifierList->Contains(key) && key->StartsWith(PARENT_MODIFIER+ "/") ) // modifier has been applied and filters should be ignored
				{
					++filterMatchCount;
					//System::Console::WriteLine("PARENT_MODIFIER {0}", key);
					continue;
				}
				else if(filterModifierList->Contains(key) && key->StartsWith(FIRST_MODIFIER+ "/")) //first already found stop! 
				{
					//System::Console::WriteLine("FIRST_MODIFIER {0}", key);
					return true;
				}
			System::String ^filterProp = key;
			System::String ^modifier = L"";
			int pos = key->IndexOf(L"/");
			if (pos != -1)//tree modifier
			{
				modifier = filterProp->Substring(0, pos);
				filterProp = filterProp->Substring(pos+1);
				//System::Console::WriteLine("modifier: {0}, {1}, {2}", modifier, filterProp, key);
			}
			if (shortPropName->Equals(filterProp) || ap->ProgrammaticName->Equals(filterProp))
			{//this element has a matching filter property
				//System::Console::WriteLine("matched property: {0}", filterProp);
				System::String ^valStr = L"";
				if (ap->ProgrammaticName->Equals(L"AutomationElementIdentifiers.RuntimeIdProperty"))
				{//runtimeId are int array so need to test it differently
					array<System::Int32> ^idArray = (array<System::Int32> ^)element->GetCurrentPropertyValue(ap);
					for each(System::Int32 val in idArray)
					{
						valStr += System::Convert::ToString(val) + L"-";
					}
					valStr = valStr->TrimEnd('-');
					//System::Console::WriteLine("runtimeId: {0}", valStr);
				}
				else //all other property types that are strings
				{
					valStr = element->GetCachedPropertyValue(ap)->ToString();
					//valStr = element->GetCurrentPropertyValue(ap)->ToString();
				}
				//System::Console::WriteLine("test property vals: {0} , {1}", valStr, enumFilters[key]);

				if (valStr->Equals(enumFilters[key])) // value matches filter value
				{
					//System::Console::WriteLine("matched property vals: {0} , {1}", valStr, enumFilters[key]);
					//result = false;
					++filterMatchCount;
					if (filterModifierList != nullptr)
						if (modifier->Equals(PARENT_MODIFIER)) //if modifier is parent then add to modifier list
						{
							//System::Console::WriteLine("modifier added1 {0}", key);
							filterModifierList->Add(key);
						}
						else if(modifier->Equals(FIRST_MODIFIER)) {
							//System::Console::WriteLine("first modifier added1 {0} {1}", key, filterModifierList->Count);
							//for each (System::String ^mod in filterModifierList)
							//	System::Console::WriteLine("mod {0}", mod);
							filterModifierList->Add(key);
							return false;
						}
				}
				else// not matched
					if (filterModifierList != nullptr)
						if (modifier->Equals(ALL_MODIFIER)) //doesn't matter if ALL modifier doesn't match, need to keep searching
						{
							//System::Console::WriteLine("modifier added2 {0}", key);
							filterModifierList->Add(key);
						}
						else if(modifier->Equals(FIRST_MODIFIER))
							filterModifierList->Add(ALL_MODIFIER + "/" + filterProp);
			}
		}

	}
	//System::Console::WriteLine("filterMatchCount: {0}", filterMatchCount);
	if (filterMatchCount > 0)
		return false;
	else
		return true;
	//return result;
}

void AutomationBridge::processFilterModifier(Boolean filtered, Boolean modifierChanged, List<System::String ^> ^filterModifierList)
{
	if (!filtered) //not filtered so return element
	{
		//winInfoList->Add(getWindowInfo(currentElement, properties));
		//winInfoList->AddRange(enumWindowInfo(currentElement, properties, filterModifierList));
		if (modifierChanged  && filterModifierList[filterModifierList->Count - 1]->StartsWith(FIRST_MODIFIER) == false) //modifier was added and needs to be removed
		{// don't remove First modifier
			//System::Console::WriteLine("modifier removed1 {0}", filterModifierList[filterModifierList->Count - 1]);
			filterModifierList->RemoveAt(filterModifierList->Count - 1);
		}
	}
	else //filtered, but if modifier used keep searching children
	{
		if (modifierChanged) //modifier was added and needs to be removed (ALL)
		{
			//winInfoList->AddRange(enumWindowInfo(currentElement, properties, filterModifierList));
			if (filterModifierList[filterModifierList->Count - 1]->StartsWith(FIRST_MODIFIER) == false)// don't remove First modifier
			{
				//System::Console::WriteLine("modifier removed2 {0}", filterModifierList[filterModifierList->Count - 1]);
				filterModifierList->RemoveAt(filterModifierList->Count - 1);
			}
		}
	}

}

System::String ^ AutomationBridge::getRuntimeIdFromElement(System::Windows::Automation::AutomationElement ^element)
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

array<System::String ^> ^ AutomationBridge::enumWindowInfo(System::String ^properties)
{
	return enumWindowInfo(AutomationElement::RootElement, properties);
}

array<System::String ^> ^ AutomationBridge::enumWindowInfo(System::IntPtr windowHandle, System::String ^properties)
{
	List<System::String ^> ^winInfoList = gcnew List<System::String ^>();
	AutomationElement ^element = nullptr;
	try {
		element = AutomationElement::FromHandle(windowHandle);
	} catch (Exception ^ex) {
		output(ex, "");
		return winInfoList->ToArray();
	}
	if (element == nullptr)
		return winInfoList->ToArray();
	if (!isElementFiltered(element)) //test parent should be filtered
		winInfoList->Add(getWindowInfo(element, properties, nullptr));
	winInfoList->AddRange(enumWindowInfo(element, properties));
	return winInfoList->ToArray();
}

array<System::String ^> ^ AutomationBridge::enumWindowInfo(AutomationElement ^element, System::String ^properties)
{
	List<System::String ^> ^filterModifierList = gcnew List<System::String ^>(); //can change descendants filters based on parent's filters
	return enumWindowInfo(element, properties, filterModifierList);
}

array<System::String ^> ^ AutomationBridge::enumWindowInfo(AutomationElement ^parentElement, System::String ^properties, List<System::String ^> ^filterModifierList)
{
	List<System::String ^> ^winInfoList = gcnew List<System::String ^>();
	if (parentElement == nullptr)
		return winInfoList->ToArray();
	TreeWalker ^tw = TreeWalker::RawViewWalker;
	//System::Console::WriteLine("get info: {0}", getWindowInfo(element, properties));
	//AutomationElement ^currentElement = tw->GetFirstChild(element, cacheRequest);
	AutomationElement ^currentElement = nullptr;
	currentElement = tw->GetFirstChild(parentElement, cacheRequest);
	if (currentElement == nullptr)
	{
		//System::Console::WriteLine("no children {0}", element->CachedChildren->Count);
		//System::Console::WriteLine("no children");
		return winInfoList->ToArray();
	}
	//else
	//	System::Console::WriteLine("yes children");

    while (currentElement != nullptr)
    {
		try
		{
			int fmlOriginalSize = filterModifierList->Count;
			Boolean filtered = isElementFiltered(currentElement, filterModifierList);
			Boolean modifierChanged = fmlOriginalSize != filterModifierList->Count;
			if (!filtered) //not filtered so return element
			{
				winInfoList->Add(getWindowInfo(currentElement, properties, parentElement));
				winInfoList->AddRange(enumWindowInfo(currentElement, properties, filterModifierList));
			}
			else //filtered, but if modifier used keep searching children
			{
				if (modifierChanged) //modifier was added search children
					winInfoList->AddRange(enumWindowInfo(currentElement, properties, filterModifierList));
			}
			processFilterModifier(filtered, modifierChanged, filterModifierList); //cleans filterModifierList
			//System::Console::WriteLine("element: {0}", currentElement);
			//currentElement->
		} catch (Exception ^ex)
		{
			output(ex, "");
		}
		currentElement = tw->GetNextSibling(currentElement, cacheRequest);
    }
	return winInfoList->ToArray();
}

System::String ^ AutomationBridge::getWindowInfo(AutomationElement ^element, System::String ^properties, AutomationElement ^optionalParentElement)
{
	System::String ^resultProperties = L"";
	System::String ^propertyNameErrorCheck = L"";
	try
	{
		//when wildcard is enabled it will pull all property names & values
		System::Boolean wildcardEnabled = false;
		if (properties->Equals(L"*"))
			wildcardEnabled = true;

		//setup parentElement
		System::String ^parentRuntimeId = L"";
		if (wildcardEnabled || properties->Contains(L"ParentRuntimeIdProperty"))
		{
			if (optionalParentElement == nullptr)
			{
				TreeWalker ^tw = TreeWalker::ControlViewWalker;
				parentRuntimeId = getRuntimeIdFromElement(tw->GetParent(element, cacheRequest));
			}
			else
				parentRuntimeId = getRuntimeIdFromElement(optionalParentElement);
		}

		//create array for keeping order of properties
		System::String ^delim = L",";
		array<System::String ^> ^propSpltArray = properties->Split(delim->ToCharArray());
		System::Int32 count = 0;
		//array<AutomationProperty^> ^aps = cachedRootProperties;//element->GetSupportedProperties();
		array<System::String ^> ^propValues = gcnew array<System::String ^>(propSpltArray->Length);//keep order
		System::String ^wildcardProperties = L"";
		if (wildcardEnabled) {
			wildcardProperties += "ParentRuntimeIdProperty:" + parentRuntimeId + ",";
			//propValues = gcnew array<System::String ^>(aps->Length +1 );//add one for parent property since it doesn't exist
		}
		for(int i=0 ; i < propValues->Length ; i++)
		{
			propValues[i] = L"";
			if (propSpltArray[i]->Equals("ParentRuntimeIdProperty"))//custom property for getting parent
			{
				propValues[i] = parentRuntimeId;
			}
		}
		for each(AutomationProperty ^ap in cachedRootProperties) //loop through all supported Properties for a child
		{
			propertyNameErrorCheck = ap->ProgrammaticName;//debug purposes
			System::String ^currentPropertyStr = L""; //current property values
			//System::Console::WriteLine("property: {0}", ap->ProgrammaticName);
			System::String ^shortPropName = L" null ";
			if (ap->ProgrammaticName->Contains(L"."))
				shortPropName = ap->ProgrammaticName->Substring(ap->ProgrammaticName->IndexOf(L".") + 1);
			if (properties->Contains(shortPropName) || properties->Contains(ap->ProgrammaticName) || ap->ProgrammaticName->Equals(properties) || wildcardEnabled)
			{
				//System::Console::WriteLine("shortPropName: {0}", shortPropName);
				//System::Object ^currentVal = element->GetCurrentPropertyValue(ap);
				System::Object ^currentVal = nullptr;
				if (shortPropName->Equals("ValueProperty")) //check if Value Pattern is an Available Property
				{
					if (((Boolean)element->GetCurrentPropertyValue(element->IsValuePatternAvailableProperty)) == false)
						continue;
					else
						currentVal = element->GetCurrentPropertyValue(ap); //cached pattern was having issues;
				}
				currentVal = element->GetCachedPropertyValue(ap);
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
			if (wildcardEnabled) {
				wildcardProperties += shortPropName + ":" +currentPropertyStr + ",";
				continue;
			}
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
			resultProperties += propValues[i] + L",";
		if (wildcardEnabled)
			resultProperties += wildcardProperties;
	} catch (Exception ^ex)  //when some elements close during enumeration it might cause valid exceptions
	{
		output(ex, " getWindowInfo on " + propertyNameErrorCheck + ", results: " + resultProperties);
	}
	return resultProperties;

}

System::String ^ AutomationBridge::getWindowInfo(System::Int32 x, System::Int32 y, System::String ^properties)
{
	AutomationElement ^element = AutomationElement::FromPoint(System::Windows::Point(x, y));
	if (element == nullptr)
		return "";
	return getWindowInfo(element, properties, nullptr);
}

System::String ^ AutomationBridge::getWindowInfo(System::IntPtr windowHandle, System::String ^properties)
{
	AutomationElement ^element = nullptr;
	try {
		AutomationElement ^element = AutomationElement::FromHandle(windowHandle);
	} catch (Exception ^ex) {
		output(ex, "");
		return "";
	}
	if (element == nullptr)
		return "";
	return getWindowInfo(element, properties, nullptr);
}

System::String ^ AutomationBridge::getWindowInfo(System::String ^runtimeIdStr, System::String ^properties)
{
	System::String ^filter = L"First/RuntimeIdProperty"; //get first matching runtimeIdProperty
	enumFilters->Add(filter, runtimeIdStr);
	array<System::String ^> ^props = enumWindowInfo(properties);
	enumFilters->Remove(filter);
	if (props->Length > 0) //if result array has a match return first result
		return props[0];
	else
		return "";
	//return getWindowInfo(element, properties);
}

void AutomationBridge::output(Exception ^ex, System::String ^message) 
{
	System::Console::WriteLine("Exception ({0}): {1} \n{2}",  message, ex->Message, ex->StackTrace);
}
