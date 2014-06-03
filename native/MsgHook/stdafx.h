// stdafx.h : include file for standard system include files,
// or project specific include files that are used frequently, but
// are changed infrequently
//

#pragma once

#include "targetver.h"

#define WIN32_LEAN_AND_MEAN             // Exclude rarely-used stuff from Windows headers
// Windows Header Files:
#include <stdio.h>
#include <windows.h>
#include <process.h>
#include <tchar.h>
#include <Psapi.h>
#include <stdlib.h>
#include <tlhelp32.h> //CreateToolhelp32Snapshot

#pragma comment( lib, "psapi.lib" )
//#pragma comment( lib, "kernel32.lib" )

// TODO: reference additional headers your program requires here

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

typedef struct
{
	HHOOK g_CwpHook;
	HHOOK g_MsgHook;
	//HHOOK g_hHook;
	HWND g_hWnd;
	HANDLE g_hInstance;
	HOOKPROC g_CwpHookProc;
}GLOBALDATA;

#ifndef GLOBAL_VARS_H // header guards
#define GLOBAL_VARS_H

extern "C" __declspec(dllexport) void CreateMsgHookWindow(LPTSTR lpCmdLine);
extern "C" __declspec(dllexport) BOOL SetCustomMsgHookDll(const TCHAR * hookDll, const char * hookDllProcName);
extern "C" __declspec(dllexport) BOOL SetMsgHook(HWND callerHWnd, DWORD threadId);
extern "C" __declspec(dllexport) HHOOK GetCurrentHookHandle();
extern "C" __declspec(dllexport) void SetGlobalDLLInstance(HANDLE dllInstance);
extern "C" __declspec(dllexport) BOOL RemoveHook();
extern "C" __declspec(dllexport) BOOL IsCurrentProcess64Bit();
extern "C" __declspec(dllexport) BOOL IsProcess64Bit(DWORD procId);
extern "C" __declspec(dllexport) DWORD GetProcessMainThreadId(DWORD procId);

//void ExtractResource(const WORD nID, LPCTSTR szFilename);

//Global variables , remember not to initialize here
extern HANDLE hMappedFile;
extern GLOBALDATA* pData;
extern bool bStartingProcess;

#define MAX_TEST_SIZE 100
extern TCHAR targetHwndStr[MAX_TEST_SIZE];
extern TCHAR targetProcessId[MAX_TEST_SIZE];
extern TCHAR targetClassname[MAX_TEST_SIZE];
extern TCHAR dll32bitName[500];
extern TCHAR dll64bitName[500];
extern char dllProcName[500];

#endif