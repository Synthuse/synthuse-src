/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
#pragma once
#include "WpfAutomation.h"
namespace Globals
{
	using namespace System;

	public ref class Global
	{
	public:
		static WpfAutomation ^WPF_AUTO = gcnew WpfAutomation();
	};

}

