/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
// uiabridge.h

#pragma once

using namespace System;
using namespace System::Collections::Generic;
using namespace System::Windows::Automation;

namespace uiabridge {

	public ref class AutomationBridge
	{
	public:
		AutomationBridge(void);
		~AutomationBridge();
		int addEnumFilter(System::String ^propertyName, System::String ^propertyValue);
		void clearEnumFilters();
		Boolean isElementFiltered(System::Windows::Automation::AutomationElement ^element);
		Boolean isElementFiltered(System::Windows::Automation::AutomationElement ^element, List<System::String ^> ^filterModifierList);
		System::String ^ getRuntimeIdFromElement(System::Windows::Automation::AutomationElement ^element);
		array<System::String ^> ^ enumWindowInfo(System::String ^properties);
		array<System::String ^> ^ enumWindowInfo(System::IntPtr windowHandle, System::String ^properties);
		array<System::String ^> ^ enumWindowInfo(AutomationElement ^element, System::String ^properties);
		array<System::String ^> ^ enumWindowInfo(AutomationElement ^element, System::String ^properties, List<System::String ^> ^filterModifierList);
		System::String ^ getWindowInfo(AutomationElement ^element, System::String ^properties);
		System::String ^ getWindowInfo(System::Int32 x, System::Int32 y, System::String ^properties);
		System::String ^ getWindowInfo(System::IntPtr windowHandle, System::String ^properties);
		System::String ^ getWindowInfo(System::String ^runtimeIdStr, System::String ^properties);

		static System::String ^ALL_MODIFIER = L"All";// find all matching elements of this filter
		static System::String ^PARENT_MODIFIER = L"Parent";//find all children of this matching parent filter
		static System::String ^FIRST_MODIFIER = L"First"; //find first element matching this filter then stop
	private:
		void initializeCache();
		Dictionary<System::String ^, System::String ^> ^enumFilters;
		void AutomationBridge::processFilterModifier(Boolean filtered, Boolean modifierChanged, List<System::String ^> ^filterModifierList);
		CacheRequest ^cacheRequest;
		array<AutomationProperty^> ^cachedRootProperties;
	};
}
