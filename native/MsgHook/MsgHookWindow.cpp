/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/

// MsgHookTest.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include "resource.h"
#include "MsgLookup.h"
#include "ResExtract.h"
//#include "MsgHookTest.h"
//#include "MsgHook.h"

#define MAX_LOADSTRING 100

// Global Variables:
HINSTANCE hInst;								// current instance
TCHAR szTitle[MAX_LOADSTRING];					// The title bar text
TCHAR szWindowClass[MAX_LOADSTRING];			// the main window class name
HWND mainHwnd = NULL;
HMENU mainMenu = NULL;
#define TXTBOX_LIMIT 700000
HWND txtbox = NULL;
HWND targetHwnd = NULL;
DWORD targetPid = 0;
const int txtboxSpacing = 2;

long msgCount = 0;

//message filters flags
bool filterWmCommand = false;
bool filterWmNotify = false;
bool filterCustom = false;
bool filterAbove = false;

TCHAR dll32bitName[500] = _T("");
TCHAR dll64bitName[500] = _T("");
char dllProcName[500] = "CwpHookProc";

//#define MAX_TEST_SIZE 100
//TCHAR targetClassname[MAX_TEST_SIZE] = _T("Notepad");
TCHAR targetProcessId[MAX_TEST_SIZE] = _T("");
TCHAR targetClassname[MAX_TEST_SIZE] = _T("");
TCHAR targetHwndStr[MAX_TEST_SIZE] = _T("");
TCHAR testWmSettextL[MAX_TEST_SIZE] = _T("This is a test");
TCHAR testWmSettextW[MAX_TEST_SIZE] = _T("0");
TCHAR testWmCommandL[MAX_TEST_SIZE] = _T("0");
TCHAR testWmCommandW[MAX_TEST_SIZE] = _T("1");

TCHAR customMsgStr[MAX_TEST_SIZE] = _T("WM_SETTEXT");

const int hotkeyIdOffset = 0;
const int pauseHotKey = 'P'; //P
bool isPaused = false;

// Forward declarations of functions included in this code module:
int APIENTRY StartWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPTSTR lpCmdLine, int nCmdShow);
ATOM				MyRegisterClass(HINSTANCE hInstance);
BOOL				InitInstance(HINSTANCE, int);
LRESULT CALLBACK	WndProc(HWND, UINT, WPARAM, LPARAM);
INT_PTR CALLBACK	DlgProc(HWND, UINT, WPARAM, LPARAM);

void AppendText(HWND txtHwnd, LPCTSTR newText)
{
	if (isPaused)
		return;
	DWORD len = GetWindowTextLength(txtHwnd);
	if (len > (TXTBOX_LIMIT - 500))
	{//need to truncate the beginning so the text doesn't go past it's limit
		SendMessage(txtHwnd, EM_SETSEL, 0, 20000);
		SendMessage(txtHwnd, EM_REPLACESEL, 0, (LPARAM)_T(""));
		len = GetWindowTextLength(txtHwnd);
	}
	//DWORD l,r;
	//SendMessage(txtHwnd, EM_GETSEL,(WPARAM)&l,(LPARAM)&r);
	SendMessage(txtHwnd, EM_SETSEL, len, len);
	SendMessage(txtHwnd, EM_REPLACESEL, 0, (LPARAM)newText);
	len = GetWindowTextLength(txtHwnd);
	SendMessage(txtHwnd, EM_SETSEL, len, len);
	//SendMessage(txtHwnd, EM_SETSEL,l,r);
}

void InitMsgFiltersAndLookup()
{
	if (!filterWmCommand && !filterAbove && !filterWmNotify && !filterCustom)
		InitializeMsgLookup();
	else
	{
		int allowList[4];
		for (int i = 0; i < 4; i ++)
			allowList[i] = -1;
		
		if (filterWmCommand) {
			AppendText(txtbox, _T("filtering on WM_COMMAND & WM_MENUCOMMAND\r\n"));
			allowList[0] = WM_COMMAND;
			allowList[1] = WM_MENUCOMMAND;
		}
		if (filterWmNotify)
		{
			AppendText(txtbox, _T("filtering on WM_NOTIFY\r\n"));
			allowList[2] = WM_NOTIFY;
		}
		//if (filterAbove)
		//	allowList[0] = WM_COMMAND;
		if (filterCustom && _tcslen(customMsgStr) > 0) 
		{
			InitializeMsgLookup(); //initialize full msg list and do reverse lookup based on custom filter string
			for (int x = 0; x < MAX_MSG_LOOKUP; x++)
			{
				if (_tcscmp(customMsgStr, MSG_LOOKUP[x]) == 0) {
					TCHAR tmp[100];
					_stprintf_s(tmp, _T("filtering on %s (%d)\r\n"), customMsgStr, x);
					AppendText(txtbox, tmp);
					allowList[3] = x;
				}
			}
		}
		InitializeMsgLookup(allowList, 4);
	}
}

void StartMessageHook()
{
	AppendText(txtbox, _T("Starting Message Hook\r\n"));
	//targetHwnd = FindWindow(targetClassname, NULL);
	
	TCHAR tmp[500];
	
	DWORD tid = 0;
	if (_tcscmp(targetHwndStr, _T("")) != 0) //if target HWND was used
	{
		TCHAR *stopStr;
		targetHwnd = (HWND)_tcstol(targetHwndStr, &stopStr, 10);
		tid = GetWindowThreadProcessId(targetHwnd, NULL);
		_stprintf_s(tmp, _T("Target Handle: %ld, and Thread Id: %ld\r\n"), targetHwnd, tid);
	}

	targetPid = 0;
	if (_tcscmp(targetProcessId, _T("")) != 0) //if target pid was used
	{
		TCHAR *stopStr;
		targetPid = (DWORD)_tcstol(targetProcessId, &stopStr, 10);
		tid = GetProcessMainThreadId(targetPid);
		_stprintf_s(tmp, _T("Target PId: %ld, and Thread Id: %ld\r\n"), targetPid, tid);		
	}
	
	InitMsgFiltersAndLookup();
	//InitializeMsgLookup();

	AppendText(txtbox, tmp);
	
	//block self/global msg hook
	if (tid == 0) {
		AppendText(txtbox, _T("Target thread not found\r\n"));
		return;
	}
	
	if (targetPid != 0) // handle various types of bit matching
	{
		BOOL current64bit = IsCurrentProcess64Bit();
		if (IsProcess64Bit(targetPid) && current64bit)
		{
			_stprintf_s(tmp, _T("Target PId (%ld) is a matching 64 bit process\r\n"), targetPid);
			SetCustomMsgHookDll(dll64bitName, dllProcName);
		}
		else if(!IsProcess64Bit(targetPid) && !current64bit)
		{
			_stprintf_s(tmp, _T("Target PId (%ld) is a matching 32 bit process\r\n"), targetPid);
			SetCustomMsgHookDll(dll32bitName, dllProcName);
		}
		else
		{
			if (current64bit)
				_stprintf_s(tmp, _T("Target PId (%ld) is a not matching 64 bit process.\r\n"), targetPid);
			else
				_stprintf_s(tmp, _T("Target PId (%ld) is a not matching 32 bit process.\r\n"), targetPid);
			AppendText(txtbox, tmp);
			TCHAR *dllname = dll32bitName;
			TCHAR *exename = _T("SetMsgHook32.exe");
			int setMsgHookRes = IDR_SETMH32;
			if (IsProcess64Bit(targetPid))
			{
				dllname = dll64bitName;
				exename = _T("SetMsgHook64.exe");
				setMsgHookRes = IDR_SETMH64;
			}
			_tcscat_s(tmp, 500, _T("Do you wish to open a new matching Message Hook Window?"));
			int mbResult = MessageBox(mainHwnd, tmp, _T("Message Hook"), MB_ICONQUESTION | MB_YESNO);
			if (mbResult == IDNO)
				return ;
			_stprintf_s(tmp, _T("%s %s 0 %d"), exename, dllname, targetPid);
			RunResource(setMsgHookRes, tmp);
			//EnableMenuItem(mainMenu, ID_FILE_STOPHOOK, MF_ENABLED);
			//EnableMenuItem(mainMenu, ID_FILE_STARTHOOK, MF_DISABLED | MF_GRAYED);
			_tcscat_s(tmp, 500, _T("\r\n"));
			AppendText(txtbox, tmp);
			PostQuitMessage(2);
			return;
		}
		AppendText(txtbox, tmp);
	}
	if (SetMsgHook(mainHwnd, tid))
	{
		EnableMenuItem(mainMenu, ID_FILE_STOPHOOK, MF_ENABLED);
		EnableMenuItem(mainMenu, ID_FILE_STARTHOOK, MF_DISABLED | MF_GRAYED);
		AppendText(txtbox, _T("Hook successfully initialized\r\n"));
	}
	else
		AppendText(txtbox, _T("Hook failed to initialize\r\n"));
}

void StopMessageHook()
{
	EnableMenuItem(mainMenu, ID_FILE_STOPHOOK, MF_DISABLED | MF_GRAYED);
	EnableMenuItem(mainMenu, ID_FILE_STARTHOOK, MF_ENABLED);
	AppendText(txtbox, TEXT("Stopping Message Hook\r\n"));
	//KillHook();
	RemoveHook();
	msgCount = 0;
}

bool OnCopyData(COPYDATASTRUCT* pCopyDataStruct) // WM_COPYDATA lParam will have this struct
{
	if( pCopyDataStruct->cbData!=sizeof(HEVENT))
		return false;
	HEVENT Event;
	memcpy(&Event, (HEVENT*)pCopyDataStruct->lpData, sizeof(HEVENT)); // transfer data to internal variable
	if (Event.dwHookType == WH_KEYBOARD)
	{
		//KBDLLHOOKSTRUCT* pkh = (KBDLLHOOKSTRUCT*) Event.lParam;
		//char tmp[50];
		//return wkvn->KeyboardData(pkh->vkCode,Event.wParam);
	}
	else if (Event.dwHookType == WH_MOUSE)
	{
		//MSLLHOOKSTRUCT* pmh = (MSLLHOOKSTRUCT*) Event.lParam;
		//char tmp[50];
		//if (Event.wParam == WM_LBUTTONDOWN)
		//	return wkvn->MouseClickData(1,true);
		//	else
		//		return wkvn->MouseMoveData(pmh->pt.x,pmh->pt.y);
	}
	else if (Event.dwHookType == WH_CALLWNDPROC)
	{
		TCHAR *msgName = _T("unknown");
		if (Event.nCode < MAX_MSG_LOOKUP)
			msgName = MSG_LOOKUP[Event.nCode];
		else
		{
			if (!filterAbove)
				return false;
		}
		if (_tcscmp(msgName, _T("")) != 0)
		{
			++msgCount;
			TCHAR msgHwndClassname[20];
			GetClassName(Event.hWnd, msgHwndClassname, 20);
			TCHAR tmp[200];
			_stprintf_s(tmp, _T("<%07ld>  hwnd: %ld (%s), msg: %s (%ld), wparam: '%s'[%ld], lparam: '%s'{%ld}\r\n"), msgCount, Event.hWnd, msgHwndClassname, msgName, Event.nCode, Event.wParamStr, Event.wParam, Event.lParamStr,Event.lParam);
			AppendText(txtbox, tmp);
		}
	}
	return false;
}

void SendWmSettext() //ID_TESTMSGS_WM
{
	//SetWindowText(targetHwnd, _T("This is a test"));
	//TCHAR txt[] = _T("This is a test");
	TCHAR *stopStr;
	long wparam = _tcstol(testWmSettextW, &stopStr, 10);
	SendMessage(targetHwnd, WM_SETTEXT, wparam, (LPARAM)testWmSettextL);
	//PostMessage(targetHwnd, WM_SETTEXT, 0 , (LPARAM)txt);
}


void SendWmCommand() //ID_TESTMSGS_WM
{
	TCHAR *stopStr;
	HWND sendHwnd = targetHwnd;
	if (_tcscmp(targetHwndStr, _T("")) != 0)
	{
		sendHwnd = (HWND)_tcstol(targetHwndStr, &stopStr, 10);
	}
	long wparam = _tcstol(testWmCommandW, &stopStr, 10);
	long lparam = _tcstol(testWmCommandL, &stopStr, 10);
	SendMessage(sendHwnd, WM_COMMAND, wparam, lparam);

	/*
	TCHAR tmp[500];
	_stprintf_s(tmp, _T("hook handle %ld\r\n"), (long)GetCurrentHookHandle());
	AppendText(txtbox, tmp); */
}

void HotKeyPressed(WPARAM wParam)
{
	//AppendText(txtbox, _T("hotkey test"));
	if (wParam == (pauseHotKey + hotkeyIdOffset))
	{
		if (!isPaused) 
		{
			AppendText(txtbox, _T("Paused\r\n"));
			isPaused = true;
		}
		else
		{
			isPaused = false;
			AppendText(txtbox, _T("Unpaused\r\n"));
		}
	}
}

extern "C" __declspec(dllexport) void CreateMsgHookWindow(LPTSTR lpCmdLine)
{
	//StartWinMain(GetModuleHandle(NULL), NULL, lpCmdLine, SW_SHOW);
	StartWinMain((HINSTANCE)pData->g_hInstance, NULL, lpCmdLine, SW_SHOW);
	
}

int APIENTRY StartWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPTSTR lpCmdLine, int nCmdShow)
{
	// get this Dlls path, by default set both 32 and 64 bit names the same
	if (_tcscmp(dll32bitName, _T("")) == 0 && _tcscmp(dll64bitName, _T("")) == 0)
	{
		HMODULE hm = NULL;
		if (!GetModuleHandleEx(GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS | GET_MODULE_HANDLE_EX_FLAG_UNCHANGED_REFCOUNT,(LPCWSTR) &StartWinMain, &hm))
		{
			int ret = GetLastError();
			fprintf(stderr, "GetModuleHandle returned %d\n", ret);
		}
		GetModuleFileName(hm, dll32bitName, sizeof(dll32bitName));
		GetModuleFileName(hm, dll64bitName, sizeof(dll64bitName));
		//MessageBox(0, dll32bitname, dll64bitname, 0);
	}

	UNREFERENCED_PARAMETER(hPrevInstance);
	UNREFERENCED_PARAMETER(lpCmdLine);

 	// TODO: Place code here.
	MSG msg;
	HACCEL hAccelTable;

	// Initialize global strings
	LoadString(hInstance, IDS_APP_TITLE, szTitle, MAX_LOADSTRING);
	LoadString(hInstance, IDC_MSGHOOKTEST, szWindowClass, MAX_LOADSTRING);
	MyRegisterClass(hInstance);

	// Perform application initialization:
	if (!InitInstance (hInstance, nCmdShow))
	{
		return FALSE;
	}

	hAccelTable = LoadAccelerators(hInstance, MAKEINTRESOURCE(IDC_MSGHOOKTEST));

	if (lpCmdLine != NULL) //process command line args
	{
		if (_tcslen(lpCmdLine) > 0)
		{
			TCHAR *stopStr;
			targetPid = (DWORD)_tcstol(lpCmdLine, &stopStr, 10);
			_stprintf_s(targetProcessId, _T("%ld"), (long)targetPid);
			StartMessageHook();
		}
	}

	// Main message loop:
	while (GetMessage(&msg, NULL, 0, 0))
	{
		//if (msg.message == WM_HOTKEY)
		//	HotKeyPressed(msg.wParam);
		if (!TranslateAccelerator(msg.hwnd, hAccelTable, &msg))
		{
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
	}
	UnregisterHotKey(mainHwnd, pauseHotKey + hotkeyIdOffset);

	return (int) msg.wParam;
}

//
//  FUNCTION: MyRegisterClass()
//
//  PURPOSE: Registers the window class.
//
//  COMMENTS:
//
//    This function and its usage are only necessary if you want this code
//    to be compatible with Win32 systems prior to the 'RegisterClassEx'
//    function that was added to Windows 95. It is important to call this function
//    so that the application will get 'well formed' small icons associated
//    with it.
//
ATOM MyRegisterClass(HINSTANCE hInstance)
{
	WNDCLASSEX wcex;

	wcex.cbSize = sizeof(WNDCLASSEX);

	wcex.style			= CS_HREDRAW | CS_VREDRAW;
	wcex.lpfnWndProc	= WndProc;
	wcex.cbClsExtra		= 0;
	wcex.cbWndExtra		= 0;
	wcex.hInstance		= hInstance;
	wcex.hIcon			= LoadIcon(hInstance, MAKEINTRESOURCE(IDI_MSGHOOKICO));
	wcex.hCursor		= LoadCursor(NULL, IDC_ARROW);
	wcex.hbrBackground	= (HBRUSH)(COLOR_WINDOW+1);
	wcex.lpszMenuName	= MAKEINTRESOURCE(IDC_MSGHOOKTEST);
	wcex.lpszClassName	= szWindowClass;
	wcex.hIconSm		= LoadIcon(wcex.hInstance, MAKEINTRESOURCE(IDI_SMALL));

	return RegisterClassEx(&wcex);
}

//
//   FUNCTION: InitInstance(HINSTANCE, int)
//
//   PURPOSE: Saves instance handle and creates main window
//
//   COMMENTS:
//
//        In this function, we save the instance handle in a global variable and
//        create and display the main program window.
//
BOOL InitInstance(HINSTANCE hInstance, int nCmdShow)
{
   HWND hWnd;

   hInst = hInstance; // Store instance handle in our global variable

   hWnd = CreateWindow(szWindowClass, szTitle, WS_OVERLAPPEDWINDOW,
      CW_USEDEFAULT, 0, 700, 300, NULL, NULL, hInstance, NULL);

   if (!hWnd) {
	    DWORD lastErr = GetLastError();
		printf("Error Creating Window %d\n", lastErr);
		_tprintf(_T("Window Class Name: %s, Instance: %ld\n"), szWindowClass, (long)hInstance);
		return FALSE;
   }
   mainHwnd = hWnd;
   
   RECT rect;
   GetClientRect(hWnd, &rect);
   // make the txtbox edit control almost the same size as the parent window
   //WS_CHILD | WS_VISIBLE | WS_VSCROLL | ES_LEFT | ES_MULTILINE | ES_AUTOVSCROLL
   txtbox = CreateWindow(TEXT("Edit"),TEXT(""), WS_CHILD | WS_VISIBLE | ES_MULTILINE | WS_VSCROLL | ES_AUTOVSCROLL | ES_READONLY, 
	   txtboxSpacing, txtboxSpacing,rect.right-(txtboxSpacing*2), rect.bottom-(txtboxSpacing*2), hWnd, NULL, NULL, NULL);
   SendMessage(txtbox, EM_SETLIMITTEXT, (WPARAM)TXTBOX_LIMIT, 0);

   HFONT hFont = CreateFont(14, 0, 0, 0, FW_DONTCARE, FALSE, FALSE, FALSE, ANSI_CHARSET, 
	  OUT_TT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, 
	  DEFAULT_PITCH | FF_DONTCARE, TEXT("Arial"));
   SendMessage(txtbox, WM_SETFONT, (WPARAM)hFont, TRUE);

   mainMenu = GetMenu(mainHwnd);
   EnableMenuItem(mainMenu, ID_FILE_STOPHOOK, MF_DISABLED | MF_GRAYED);

   RegisterHotKey(mainHwnd, pauseHotKey + hotkeyIdOffset, MOD_NOREPEAT | MOD_SHIFT | MOD_CONTROL, pauseHotKey);

   ShowWindow(hWnd, nCmdShow);
   UpdateWindow(hWnd);

   //set always on top
   SetWindowPos(mainHwnd, HWND_TOPMOST, 0, 0, 0, 0, SWP_NOSIZE| SWP_NOMOVE);

   return TRUE;
}

//
//  FUNCTION: WndProc(HWND, UINT, WPARAM, LPARAM)
//
//  PURPOSE:  Processes messages for the main window.
//
//  WM_COMMAND	- process the application menu
//  WM_PAINT	- Paint the main window
//  WM_DESTROY	- post a quit message and return
//
//
LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	int wmId, wmEvent;
	PAINTSTRUCT ps;
	HDC hdc;

	switch (message)
	{
	case WM_CREATE:
		//appendText(txtbox, TEXT("test\r\n"));
		break;
	case WM_COPYDATA:
		return (OnCopyData((COPYDATASTRUCT *) lParam));
		break;
	case WM_COMMAND:
		wmId    = LOWORD(wParam);
		wmEvent = HIWORD(wParam);
		// Parse the menu selections:
		switch (wmId)
		{
		case ID_FILE_STARTHOOK:
			StartMessageHook();
			break;
		case ID_FILE_STOPHOOK:
			StopMessageHook();
			break;
		case ID_TESTMSGS_WM:
			SendWmSettext();
			break;
		case ID_TESTMSGS_WMCOM:
			SendWmCommand();
			break;
		case ID_PROC64TEST:
			if (_tcscmp(targetProcessId, _T("")) != 0) //if target pid was used
			{
				TCHAR tmp[500];
				TCHAR *stopStr;
				targetPid = (DWORD)_tcstol(targetProcessId, &stopStr, 10);
				BOOL current64bit = IsCurrentProcess64Bit();
				if (IsProcess64Bit(targetPid) && current64bit)
					_stprintf_s(tmp, _T("Target pid (%ld) is a matching 64 bit process\r\n"), targetPid);
				else if(!IsProcess64Bit(targetPid) && !current64bit)
					_stprintf_s(tmp, _T("Target pid (%ld) is a matching 32 bit process\r\n"), targetPid);
				else if (IsProcess64Bit(targetPid))
					_stprintf_s(tmp, _T("Target pid (%ld) is 64 bit process\r\n"), targetPid);
				else
					_stprintf_s(tmp, _T("Target pid (%ld) is 32 bit process\r\n"), targetPid);
				AppendText(txtbox, tmp);
				//ExtractResource(IDR_SETMH32, _T("SetMsgHook32.exe"));
				//_stprintf_s(tmp, _T(" %s %ld %d"), dll32bitName, (long)mainHwnd, targetPid);
				//RunResource(IDR_SETMH32, tmp);

				//MessageBox(0, , _T("64 bit Test"), 0);
			}
			break;
		case ID_FILE_SETTINGS:
			DialogBox(hInst, MAKEINTRESOURCE(IDD_DIALOG1), hWnd, DlgProc);
			break;
		case IDM_ABOUT:
			DialogBox(hInst, MAKEINTRESOURCE(IDD_ABOUTBOX), hWnd, DlgProc);
			break;
		case ID_FILE_CLEAR:
			SetWindowText(txtbox, _T(""));
			break;
		case IDM_EXIT:
			DestroyWindow(hWnd);
			break;
		default:
			return DefWindowProc(hWnd, message, wParam, lParam);
		}
		break;
	case WM_HOTKEY:
		HotKeyPressed(wParam);
		break;
	case WM_PAINT:
		hdc = BeginPaint(hWnd, &ps);
		// TODO: Add any drawing code here...
		EndPaint(hWnd, &ps);
		break;
	case WM_SIZE:
		{ //resize the txtbox when the parent window size changes
			int nWidth = LOWORD(lParam);
			int nHeight = HIWORD(lParam);
			SetWindowPos(txtbox, HWND_NOTOPMOST, txtboxSpacing, txtboxSpacing, nWidth-(txtboxSpacing*2), nHeight-(txtboxSpacing*2), SWP_NOZORDER|SWP_NOMOVE);
		}
		break;
	case WM_DESTROY:
		PostQuitMessage(0);
		break;
	default:
		return DefWindowProc(hWnd, message, wParam, lParam);
	}
	return 0;
}

// Message handler for about box.
INT_PTR CALLBACK DlgProc(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	UNREFERENCED_PARAMETER(lParam);
	switch (message)
	{
	case WM_INITDIALOG:
		{
			//IDC_EDIT1
			//SendDlgItemMessage(hDlg, IDC_EDIT1, WM_SETTEXT, 0 , (LPARAM)targetClassname);
			SendDlgItemMessage(hDlg, IDC_TARGETPID, WM_SETTEXT, 0 , (LPARAM)targetProcessId);
			if (filterWmCommand)
				SendDlgItemMessage(hDlg, IDC_CHECK_CMD, BM_SETCHECK, BST_CHECKED, 0);
			if (filterWmNotify)
				SendDlgItemMessage(hDlg, IDC_CHECK_NOT, BM_SETCHECK, BST_CHECKED, 0);
			if (filterAbove)
				SendDlgItemMessage(hDlg, IDC_CHECK_ABO, BM_SETCHECK, BST_CHECKED, 0);
			if (filterCustom)
				SendDlgItemMessage(hDlg, IDC_CUSTOMCHK, BM_SETCHECK, BST_CHECKED, 0);
			SendDlgItemMessage(hDlg, IDC_WMCOMW, WM_SETTEXT, 0 , (LPARAM)testWmCommandW);
			SendDlgItemMessage(hDlg, IDC_WMCOML, WM_SETTEXT, 0 , (LPARAM)testWmCommandL);
			SendDlgItemMessage(hDlg, IDC_WMSETW, WM_SETTEXT, 0 , (LPARAM)testWmSettextW);
			SendDlgItemMessage(hDlg, IDC_WMSETL, WM_SETTEXT, 0 , (LPARAM)testWmSettextL);
			SendDlgItemMessage(hDlg, IDC_HWND, WM_SETTEXT, 0 , (LPARAM)targetHwndStr);
			SendDlgItemMessage(hDlg, IDC_CUSTOMMSG, WM_SETTEXT, 0 , (LPARAM)customMsgStr);			
		}
		return (INT_PTR)TRUE;

	case WM_COMMAND:
		if (LOWORD(wParam) == IDOK) //only save on OK
		{
			//GetDlgItemText(hDlg, IDC_EDIT1, targetClassname, MAX_TEST_SIZE);
			GetDlgItemText(hDlg, IDC_TARGETPID, targetProcessId, MAX_TEST_SIZE);
			GetDlgItemText(hDlg, IDC_WMCOMW, testWmCommandW, MAX_TEST_SIZE);
			GetDlgItemText(hDlg, IDC_WMCOML, testWmCommandL, MAX_TEST_SIZE);
			GetDlgItemText(hDlg, IDC_WMSETW, testWmSettextW, MAX_TEST_SIZE);
			GetDlgItemText(hDlg, IDC_WMSETL, testWmSettextL, MAX_TEST_SIZE);
			GetDlgItemText(hDlg, IDC_HWND, targetHwndStr, MAX_TEST_SIZE);
			GetDlgItemText(hDlg, IDC_CUSTOMMSG, customMsgStr, MAX_TEST_SIZE);
			// check filter options
			filterWmCommand = (SendDlgItemMessage(hDlg, IDC_CHECK_CMD, BM_GETCHECK, 0, 0) == BST_CHECKED); // the hard way
			filterWmNotify = (IsDlgButtonChecked(hDlg, IDC_CHECK_NOT) == BST_CHECKED);// the easy way
			filterAbove = (IsDlgButtonChecked(hDlg, IDC_CHECK_ABO) == BST_CHECKED);
			filterCustom = (IsDlgButtonChecked(hDlg, IDC_CUSTOMCHK) == BST_CHECKED);

			InitMsgFiltersAndLookup();
		}
		if (LOWORD(wParam) == IDOK || LOWORD(wParam) == IDCANCEL)
		{
			EndDialog(hDlg, LOWORD(wParam));
			return (INT_PTR)TRUE;
		}
		break;
	}
	return (INT_PTR)FALSE;
}
