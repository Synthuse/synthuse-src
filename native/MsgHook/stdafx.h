// stdafx.h : include file for standard system include files,
// or project specific include files that are used frequently, but
// are changed infrequently
//

#pragma once

#include "targetver.h"

#define WIN32_LEAN_AND_MEAN             // Exclude rarely-used stuff from Windows headers
// Windows Header Files:
#include <windows.h>
#include <tchar.h>
#include <Psapi.h>
#include <stdlib.h>

#pragma comment( lib, "psapi.lib" )

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
}GLOBALDATA;

#ifndef GLOBAL_VARS_H // header guards
#define GLOBAL_VARS_H

extern "C" __declspec(dllexport) BOOL SetMsgHook(HWND callerHWnd, DWORD threadId);
extern "C" __declspec(dllexport) BOOL RemoveHook();

//Global variables , remember not to initialize here
extern HANDLE hMappedFile;
extern GLOBALDATA* pData;
extern bool bStartingProcess;

#endif