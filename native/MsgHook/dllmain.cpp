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
			//printf("debug DLL_PROCESS_ATTACH hModule: %ld\n", (long)hModule);
			TCHAR szBaseName[_MAX_FNAME], szTmp[_MAX_FNAME];
			memset((void *)&szBaseName, '\0', sizeof(TCHAR) * _MAX_FNAME);

			if (GetModuleBaseName(GetCurrentProcess(), (HMODULE)hModule, szTmp, sizeof(szTmp)))// compute MMF-filename from current module base name, uses Psapi
			_wsplitpath_s(szTmp, NULL, NULL, szBaseName, _MAX_FNAME, NULL, NULL, NULL, NULL);
			//_wsplitpath(szTmp, NULL, NULL, szBaseName, NULL);

			wcscat_s(szBaseName, TEXT("MsgHookSharedMem"));       // add specifier string
			if (IsCurrentProcess64Bit())
				wcscat_s(szBaseName, TEXT("64"));       // add bit specifier
			else
				wcscat_s(szBaseName, TEXT("32"));       // add bit specifier

			hMappedFile = CreateFileMapping(INVALID_HANDLE_VALUE, NULL, PAGE_READWRITE, 0, sizeof(GLOBALDATA), szBaseName);
			pData = (GLOBALDATA*)MapViewOfFile(hMappedFile, FILE_MAP_WRITE, 0, 0, 0);
			bStartingProcess = (hMappedFile != NULL) && (GetLastError() != ERROR_ALREADY_EXISTS);

			if(bStartingProcess)       // if the MMF doesn't exist, we have the first instance
			{
				pData->g_hInstance = hModule;  // so set the instance handle
				pData->g_hWnd = NULL;     // and initialize the other handles
				pData->g_CwpHook = NULL;
				pData->g_MsgHook = NULL;
				pData->g_CwpHookProc = NULL;
			}
			else
			{
				//open
				hMappedFile = OpenFileMapping(FILE_MAP_ALL_ACCESS, false, szBaseName);
				pData = (GLOBALDATA*)MapViewOfFile(hMappedFile, FILE_MAP_ALL_ACCESS, 0, 0, 0);
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

