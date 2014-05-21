// dllmain.cpp : Defines the entry point for the DLL application.
#include "stdafx.h"

HANDLE hMappedFile;
GLOBALDATA* pData;
bool bStartingProcess = false;


BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
		{
			TCHAR szBaseName[_MAX_FNAME], szTmp[_MAX_FNAME];
			memset((void *)&szBaseName, '\0', sizeof(TCHAR) * _MAX_FNAME);

			if (GetModuleBaseName(GetCurrentProcess(), (HMODULE)hModule, szTmp, sizeof(szTmp)))// compute MMF-filename from current module base name, uses Psapi
			_wsplitpath(szTmp, NULL, NULL, szBaseName, NULL);

			wcscat(szBaseName, TEXT("MsgHookSharedMem"));       // add specifier string

			hMappedFile = CreateFileMapping(INVALID_HANDLE_VALUE, NULL, PAGE_READWRITE, 0, sizeof(GLOBALDATA), szBaseName);
			pData = (GLOBALDATA*)MapViewOfFile(hMappedFile, FILE_MAP_WRITE, 0, 0, 0);
			bStartingProcess = (hMappedFile != NULL) && (GetLastError() != ERROR_ALREADY_EXISTS);

			if(bStartingProcess)       // if the MMF doesn't exist, we have the first instance
			{
				pData->g_hInstance  = hModule;  // so set the instance handle
				pData->g_hWnd       = NULL;     // and initialize the other handles
				pData->g_hHook      = NULL;
			}
			DisableThreadLibraryCalls((HMODULE)hModule);
		}
		break;
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
			CloseHandle(hMappedFile);     // on detaching the DLL, close the MMF
		break;
	}
	return TRUE;
}

