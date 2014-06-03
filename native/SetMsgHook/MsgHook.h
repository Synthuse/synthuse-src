/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/

#include <windows.h>

typedef struct
{
	HHOOK g_CwpHook;
	HHOOK g_MsgHook;
	//HHOOK g_hHook;
	HWND g_hWnd;
	HANDLE g_hInstance;
	HOOKPROC g_CwpHookProc;
}GLOBALDATA;

//#define MSGHOOKER_FILE TEXT("MsgHook.dll")
TCHAR MSGHOOK_DLL_NAME[MAX_NAME_SIZE] = _T("MsgHook.dll");

HINSTANCE msgHookDll;

//void CreateMsgHookWindow(LPTSTR lpCmdLine)
typedef VOID (* CREATEMSGHOOKWINDOW)(LPTSTR);
CREATEMSGHOOKWINDOW CreateMsgHookWindow;

//BOOL SetCustomMsgHookDll(const TCHAR * hookDll, const char * hookDllProcName)
typedef BOOL (* SETCUSTOMMSGHOOKDLL)(LPCTSTR, LPCSTR);
SETCUSTOMMSGHOOKDLL SetCustomMsgHookDll;

//BOOL SetMsgHook(HWND callerHWnd, DWORD threadId)
typedef BOOL (* SETMSGHOOK)(HWND, DWORD);
SETMSGHOOK SetMsgHook;

//HHOOK GetCurrentHookHandle()
typedef HHOOK (* GETCURRENTHOOKHANDLE)(VOID);
GETCURRENTHOOKHANDLE GetCurrentHookHandle;

//void SetGlobalDLLInstance(HANDLE dllInstance)
typedef VOID (* SETGLOBALDLLINSTANCE)(HANDLE);
SETGLOBALDLLINSTANCE SetGlobalDLLInstance;

//BOOL RemoveHook()
typedef BOOL (* REMOVEHOOK)(VOID);
REMOVEHOOK RemoveHook;

// DWORD GetProcessMainThreadId(DWORD procId)
typedef DWORD (* GETPROCESSMAINTHREADID)(DWORD);
GETPROCESSMAINTHREADID GetProcessMainThreadId;


typedef struct
{
	HWND hWnd;
	int nCode;
	DWORD dwHookType;
	WPARAM wParam;
	LPARAM lParam;
	TCHAR wParamStr[25];
	TCHAR lParamStr[25];
}HEVENT;

/*
typedef struct {
    DWORD vkCode;
    DWORD scanCode;
    DWORD flags;
    DWORD time;
    ULONG_PTR dwExtraInfo;
} KBDLLHOOKSTRUCT, *PKBDLLHOOKSTRUCT;
*/

void MsgHook_CreateMsgHookWindow(LPTSTR args)
{
	msgHookDll = LoadLibrary(MSGHOOK_DLL_NAME);
	if (msgHookDll != NULL)
	{
		CreateMsgHookWindow = (CREATEMSGHOOKWINDOW)GetProcAddress(msgHookDll, "CreateMsgHookWindow");
		if (CreateMsgHookWindow)
		{
			CreateMsgHookWindow(args);
		}
	}
}

BOOL MsgHook_SetMsgHook(HWND hw, int threadId)
{
	msgHookDll = LoadLibrary(MSGHOOK_DLL_NAME);
	if (msgHookDll != NULL)
	{
		SetMsgHook = (SETMSGHOOK)GetProcAddress(msgHookDll, "SetMsgHook");
		GetCurrentHookHandle = (GETCURRENTHOOKHANDLE)GetProcAddress(msgHookDll, "GetCurrentHookHandle");
		SetGlobalDLLInstance = (SETGLOBALDLLINSTANCE)GetProcAddress(msgHookDll, "SetGlobalDLLInstance");
		RemoveHook = (REMOVEHOOK)GetProcAddress(msgHookDll, "RemoveHook");
		if (SetMsgHook)
		{
			//printf("LoadLibrary MSGHOOK %ld\n", (long)msgHookDll);
			SetGlobalDLLInstance(msgHookDll);
			return SetMsgHook(hw, threadId);
		}
	}
	return false;
}

void MsgHook_RemoveHook()
{
	if (RemoveHook)
		RemoveHook();

	if (msgHookDll != NULL)
		FreeLibrary(msgHookDll);
}

DWORD MsgHook_GetProcessMainThreadId(DWORD procId)
{
	msgHookDll = LoadLibrary(MSGHOOK_DLL_NAME);
	if (msgHookDll != NULL)
	{
		GetProcessMainThreadId = (GETPROCESSMAINTHREADID)GetProcAddress(msgHookDll, "GetProcessMainThreadId");
		if (GetProcessMainThreadId)
		{
			return GetProcessMainThreadId(procId);
		}
	}
	printf("error, failed loading library");
	return 0;
}
