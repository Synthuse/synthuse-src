/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/
// SetMsgHook.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "MsgHook.h"


int _tmain(int argc, _TCHAR* argv[])
{
	if (argc == 1) //no args passed, show MsgHook Viewer gui
	{
		MsgHook_CreateMsgHookWindow(NULL);
		return 0;
	}

	HWND hookHwnd = NULL;
	long procId = 0;
	TCHAR *stopStr;

	for (int i = 1 ; i < argc ; i++)
	{
		if (_tcscmp(argv[i], _T("?")) == 0 || _tcscmp(argv[i], _T("-?")) == 0 || argc == 3 || argc > 4)
		{
			printf("SetMsgHook version 1.0 by Edward Jakubowski \n\n");
			printf("Usage: SetMsgHook.exe [(MSG_HOOK_DLL) (MSG_HOOK_HWND_OR_ZERO) (TARGET_PID)] [MSG_HOOK_DLL] [?] \n\n");
			printf(" Additional Notes:\n");
			printf("   Message Hook Viewer Gui - To open the gui you must provide the path to the msg hook dll as the ");
			printf("only argument when running SetMsgHook.exe.  Also setting the (MSG_HOOK_HWND) argument to 0 (zero) ");
			printf("will start the gui and message hook on the given Process Id.\n\n");
			HWND currentHwnd = FindWindow(_T("MSGHOOKVIEW"), NULL);
			printf("   Current MSG_HOOK_HWND: %ld\n", (long)currentHwnd);
			return 0;
		}
		if (i == 1)
			_tcsncpy_s(MSGHOOK_DLL_NAME, MAX_NAME_SIZE, argv[i], _TRUNCATE);
		if (i == 2)
			hookHwnd = (HWND)_tcstol(argv[i], &stopStr, 10);
		if (i == 3)
			procId = (long)_tcstol(argv[i], &stopStr, 10);
	}

	if (argc == 2) //one arg passed (dll), show MsgHook Viewer gui
	{
		printf("Starting msg hook viewer...");
		MsgHook_CreateMsgHookWindow(NULL);
		return 0;
	}
	if (argc == 4 && hookHwnd == 0)
	{
		printf("Starting msg hook viewer on pid %ld...", (long)procId);
		TCHAR tmp[100];
		_stprintf_s(tmp, _T("%ld"), (long)procId);
		MsgHook_CreateMsgHookWindow(tmp);
		//_getch();
		return 0;
	}

	char tmp[MAX_NAME_SIZE];
	size_t convertedCnt = 0;
	wcstombs_s(&convertedCnt, tmp, MAX_NAME_SIZE, MSGHOOK_DLL_NAME, _TRUNCATE);
	printf("MsgHook DLL: %s, HWND: %ld, PID: %ld", tmp, (long)hookHwnd, procId);
	DWORD threadId = MsgHook_GetProcessMainThreadId(procId);
	printf(", ThreadId: %ld\n", (long)threadId);

	if (MsgHook_SetMsgHook(hookHwnd, threadId))
		printf("Hook successfully initialized\n");
	else
	{
		printf("Hook failed to initialize\n");
		return -1;
	}

	//don't exit SetMsgHook until hooked process exits
    HANDLE process = OpenProcess(SYNCHRONIZE, FALSE, procId);
    while(WaitForSingleObject(process, 0) == WAIT_TIMEOUT)
	{
		Sleep(1000); //check once per second
		if (GetCurrentHookHandle() == NULL)
		{
			printf("unhooked.");
			break;
		}
	}
	CloseHandle(process);
	MsgHook_RemoveHook();

	//_getch();
	printf("done.");
	return 0;
}

