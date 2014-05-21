/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/


// MsgHook.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"


LRESULT CALLBACK HookProc(int nCode, WPARAM wParam, LPARAM lParam)
{   
	COPYDATASTRUCT  CDS;
	HEVENT          Event;

	CDS.dwData = 0;
	CDS.cbData = sizeof(Event);
	CDS.lpData = &Event;

	if (nCode == HC_ACTION) {
		//For WH_CALLWNDPROC hook a pointer to a CWPSTRUCT structure that contains details about the message. 
		CWPSTRUCT *cwps = (CWPSTRUCT *)lParam; 

		Event.lParam      = cwps->lParam;
		Event.wParam      = cwps->wParam;
		Event.nCode       = cwps->message;
		Event.dwHookType  = WH_CALLWNDPROC;

		BOOL bRes = SendMessage(pData->g_hWnd, WM_COPYDATA, 0, (LPARAM)(VOID*)&CDS); // ask the controlling program if the hook should be passed
	}
	return CallNextHookEx(pData->g_hHook, nCode, wParam, lParam);  // pass hook to next handler
	//return bRes;  // Don't tell the other hooks about this message.
}

extern "C" __declspec(dllexport) BOOL SetHook(HWND callerHWnd, DWORD threadId)
{
	if(bStartingProcess) // if we're just starting the DLL for the first time,
	{
		pData->g_hWnd   = callerHWnd; // remember the windows and hook handle for further instances
		pData->g_hHook  = SetWindowsHookEx(WH_CALLWNDPROC, (HOOKPROC)HookProc, (HINSTANCE)pData->g_hInstance, threadId);   

		return (pData->g_hHook != NULL);
	}
		else 
			return false;
}

extern "C" __declspec(dllexport) BOOL RemoveHook()
{
	if (pData == NULL)
		return false;
	if(pData->g_hHook)       // if the hook is defined
	{
		bool ret = UnhookWindowsHookEx(pData->g_hHook);
		pData->g_hWnd = NULL;  // reset data
		pData->g_hHook = NULL;
		return ret;
	}
	return false;
}