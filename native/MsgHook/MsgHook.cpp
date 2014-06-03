/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/


// MsgHook.cpp : Defines the exported functions for the DLL application.
//
#include "stdafx.h"

//to fix export declaration had to add .def file
LRESULT CALLBACK CwpHookProc(int nCode, WPARAM wParam, LPARAM lParam)
{   
	COPYDATASTRUCT  CDS;
	HEVENT          Event;

	CDS.dwData = 0;
	CDS.cbData = sizeof(Event);
	CDS.lpData = &Event;

	//if (nCode == HC_ACTION)
	{
		//For WH_CALLWNDPROC hook a pointer to a CWPSTRUCT structure that contains details about the message. 
		CWPSTRUCT *cwps = (CWPSTRUCT *)lParam; 
		Event.hWnd = cwps->hwnd;
		Event.lParam = cwps->lParam;
		Event.wParam = cwps->wParam;
		Event.nCode = cwps->message;
		Event.dwHookType = WH_CALLWNDPROC;
		memset((void *)&Event.wParamStr, '\0', sizeof(TCHAR) * 25);
		memset((void *)&Event.lParamStr, '\0', sizeof(TCHAR) * 25);
		bool errorFlg = false;
		if (cwps->message == WM_SETTEXT && cwps->lParam != 0 && cwps->wParam == 0)
		{
			if (IsWindowUnicode(Event.hWnd))
			{
				_tcsncpy_s(Event.lParamStr, 25, (const wchar_t*)Event.lParam, _TRUNCATE);
			}
			else
			{
				int asciiSize = (int)strlen((const char*)Event.lParam);
				int unicodeSize = (int)_tcslen((const wchar_t*)Event.lParam);
				if (unicodeSize > asciiSize)
					_tcsncpy_s(Event.lParamStr, 25, (const wchar_t*)Event.lParam, _TRUNCATE);
				else
				{
					int tstrLen = MultiByteToWideChar(CP_UTF8, 0, (LPCSTR)cwps->lParam, (int)strlen((LPCSTR)cwps->lParam), NULL, 0); //get t len
					if (tstrLen > 24)
						tstrLen = 24;
					MultiByteToWideChar(CP_UTF8, 0, (LPCSTR)cwps->lParam, (int)strlen((LPCSTR)cwps->lParam), Event.lParamStr, tstrLen); // convert char to tchar
				}
			}
		}

		//printf("debug: sending to hwnd (%ld) msg %d, wParam %ld, lParam %ld\n", pData->g_hWnd, Event.nCode, Event.wParam, Event.lParam); 
		if (cwps->hwnd != pData->g_hWnd)
		{
			BOOL bRes = (BOOL)SendMessage(pData->g_hWnd, WM_COPYDATA, 0, (LPARAM)(VOID*)&CDS); // ask the controlling program if the hook should be passed
		}
	}
	return CallNextHookEx(pData->g_CwpHook, nCode, wParam, lParam);  // pass hook to next handler
	//return bRes;  // Don't tell the other hooks about this message.
}

LRESULT CALLBACK MsgHookProc(int nCode, WPARAM wParam, LPARAM lParam)
{   
	COPYDATASTRUCT  CDS;
	HEVENT          Event;

	CDS.dwData = 0;
	CDS.cbData = sizeof(Event);
	CDS.lpData = &Event;

	if (nCode >=0 && nCode == HC_ACTION)
	{
		//For WH_GETMESSAGE hook a pointer to a MSG structure that contains details about the message. 
		MSG *msg = (MSG *)lParam; 
		Event.hWnd = msg->hwnd;
		Event.lParam = msg->lParam;
		Event.wParam = msg->wParam;
		Event.nCode = msg->message;
		Event.dwHookType = WH_GETMESSAGE;
		memset((void *)&Event.wParamStr, '\0', sizeof(TCHAR) * 25);
		memset((void *)&Event.lParamStr, '\0', sizeof(TCHAR) * 25);
		//if (msg->message == WM_SETTEXT && msg->lParam != 0)
		//	_tcscpy_s(Event.lParamStr, 25, (const wchar_t*)Event.lParam);
		//if (msg->message == WM_COMMAND || msg->message == WM_MENUCOMMAND) //infinite loop?
		if (msg->hwnd != pData->g_hWnd)
		{
			BOOL bRes = (BOOL)SendMessage(pData->g_hWnd, WM_COPYDATA, 0, (LPARAM)(VOID*)&CDS); // ask the controlling program if the hook should be passed
		}
	}
	
	return CallNextHookEx(pData->g_MsgHook, nCode, wParam, lParam);  // pass hook to next handler
	//return bRes;  // Don't tell the other hooks about this message.
}

//support for 32-bit/64-bit apps means the dll might be different to match the process we want to see
extern "C" __declspec(dllexport) BOOL SetCustomMsgHookDll(const TCHAR * hookDll, const char * hookDllProcName)
{
	HMODULE dll = LoadLibrary(hookDll); //should provide full dll path and filename
	if (dll == NULL)
	{
		TCHAR errorStr[200];
		_stprintf_s(errorStr, _T("Error loading hook library %s"), hookDll);
		MessageBox(0, errorStr, _T("Set Hook Dll Error"), 0);
		return false;
	}
	HOOKPROC addr = (HOOKPROC)GetProcAddress(dll, hookDllProcName); //should provide the 'CwpHookProc'
	if (addr == NULL)
	{
		char errorStr[200];
		sprintf_s(errorStr, "Error loading hook library procedure %s", hookDllProcName);
		MessageBoxA(0, errorStr, "Set Hook Dll Error", 0);
		return false;
	}
	pData->g_hInstance = dll;
	pData->g_CwpHookProc = addr;
	return true;
}

extern "C" __declspec(dllexport) BOOL SetMsgHook(HWND callerHWnd, DWORD threadId)
{
//	if(bStartingProcess) // if we're just starting the DLL for the first time,
	{
		pData->g_hWnd   = callerHWnd; // remember the windows and hook handle for further instances
		if (pData->g_CwpHookProc == NULL)
			pData->g_CwpHookProc = (HOOKPROC)CwpHookProc;
		pData->g_CwpHook  = SetWindowsHookEx(WH_CALLWNDPROC, pData->g_CwpHookProc, (HINSTANCE)pData->g_hInstance, threadId);
		//pData->g_MsgHook  = SetWindowsHookEx(WH_GETMESSAGE, (HOOKPROC)MsgHookProc, (HINSTANCE)pData->g_hInstance, threadId);   
		if (pData->g_CwpHook == NULL) {
			TCHAR tmp[100];
			_stprintf_s(tmp, _T("Last Error # %ld on threadId %ld"), GetLastError(), threadId);
			MessageBox(0, tmp, _T("Set Hook Error"), 0);
		}

		return (pData->g_CwpHook != NULL); //pData->g_CwpHook != NULL && 
	}
	/*else 
	{
		//MessageBox(0, _T("Error: Not starting process"), _T("Set Hook Error"), 0);
		return false;
	}*/
}

extern "C" __declspec(dllexport) HHOOK GetCurrentHookHandle()
{
	return pData->g_CwpHook; //if NULL hook isn't running
}

extern "C" __declspec(dllexport) void SetGlobalDLLInstance(HANDLE dllInstance)
{
	pData->g_hInstance = dllInstance;
}

extern "C" __declspec(dllexport) BOOL RemoveHook()
{
	if (pData == NULL)
		return false;
	if(pData->g_MsgHook)       // if the hook is defined
	{
		UnhookWindowsHookEx(pData->g_MsgHook);
		pData->g_MsgHook = NULL;
	}
	if(pData->g_CwpHook)       // if the hook is defined
	{
		BOOL ret = UnhookWindowsHookEx(pData->g_CwpHook);
		pData->g_hWnd = NULL;  // reset data
		pData->g_CwpHook = NULL;
		pData->g_CwpHookProc = NULL;
		return ret;
	}
	return false;
}


//testing if process 64 bit, needed to verify this dll can hook & attach to target process
typedef BOOL (WINAPI *LPFN_ISWOW64PROCESS) (HANDLE, PBOOL);
LPFN_ISWOW64PROCESS fnIsWow64Process;

extern "C" __declspec(dllexport) BOOL IsCurrentProcess64Bit()
{
	return IsProcess64Bit(_getpid());
}

extern "C" __declspec(dllexport) BOOL IsProcess64Bit(DWORD procId)
{
	SYSTEM_INFO stInfo;
	GetNativeSystemInfo(&stInfo); // if native system is x86 skip wow64 test
	if (stInfo.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_INTEL)
		return false; //printf( "Processor Architecture: Intel x86\n");

    BOOL bIsWow64 = FALSE;
    //IsWow64Process is not available on all supported versions of Windows.
    //Use GetModuleHandle to get a handle to the DLL that contains the function
    //and GetProcAddress to get a pointer to the function if available.

    fnIsWow64Process = (LPFN_ISWOW64PROCESS)GetProcAddress(GetModuleHandle(TEXT("kernel32")),"IsWow64Process");
    if(fnIsWow64Process != NULL)
    {
		HANDLE procHandle = NULL;//GetCurrentProcess();
		procHandle = OpenProcess(PROCESS_QUERY_INFORMATION, false, procId);
        if (!fnIsWow64Process(procHandle, &bIsWow64))
        {
            //handle error
        }
		CloseHandle(procHandle);
		if (bIsWow64) // NOT a native 64bit process
			return false;
		return true;// is a native 64bit process
    }
    return false; //some error finding function "IsWow64Process" assume not 64-bit
}

extern "C" __declspec(dllexport) DWORD GetProcessMainThreadId(DWORD procId)
{

#ifndef MAKEULONGLONG
	#define MAKEULONGLONG(ldw, hdw) ((ULONGLONG(hdw) << 32) | ((ldw) & 0xFFFFFFFF))
#endif
#ifndef MAXULONGLONG
	#define MAXULONGLONG ((ULONGLONG)~((ULONGLONG)0))
#endif

	DWORD dwMainThreadID = 0;
	ULONGLONG ullMinCreateTime = MAXULONGLONG;
	//includes all threads in the system
	HANDLE hThreadSnap = CreateToolhelp32Snapshot(TH32CS_SNAPTHREAD, 0);
	if (hThreadSnap != INVALID_HANDLE_VALUE) {
		THREADENTRY32 th32;
		th32.dwSize = sizeof(THREADENTRY32);
		BOOL bOK = TRUE;
		//Enumerate all threads in the system and filter on th32OwnerProcessID = pid
		for (bOK = Thread32First(hThreadSnap, &th32); bOK ; bOK = Thread32Next(hThreadSnap, &th32)) {
			//if (th32.dwSize >= FIELD_OFFSET(THREADENTRY32, th32OwnerProcessID) + sizeof(th32.th32OwnerProcessID)) {
			if (th32.th32OwnerProcessID == procId && (th32.dwSize >= FIELD_OFFSET(THREADENTRY32, th32OwnerProcessID) + sizeof(th32.th32OwnerProcessID))) {
				//_tprintf(_T("DEBUG Enumerate Process (%ld) Thread Id: %ld\n"), procId, th32.th32ThreadID);
				HANDLE hThread = OpenThread(THREAD_QUERY_INFORMATION, TRUE, th32.th32ThreadID);
				if (hThread) {
					FILETIME afTimes[4] = {0};
					if (GetThreadTimes(hThread,	&afTimes[0], &afTimes[1], &afTimes[2], &afTimes[3])) {
						ULONGLONG ullTest = MAKEULONGLONG(afTimes[0].dwLowDateTime, afTimes[0].dwHighDateTime);
						if (ullTest && ullTest < ullMinCreateTime) { //check each thread's creation time
							ullMinCreateTime = ullTest;
							dwMainThreadID = th32.th32ThreadID; // let it be main thread
						}
					}
					CloseHandle(hThread); //must close opened thread
				}
			}
		}
#ifndef UNDER_CE
		CloseHandle(hThreadSnap); //close thread snapshot
#else
		CloseToolhelp32Snapshot(hThreadSnap); //close thread snapshot
#endif
	}
	return dwMainThreadID; //returns main thread id or returns 0 if can't find it
}