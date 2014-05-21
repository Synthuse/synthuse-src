// MsgHookTest.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include "MsgHookTest.h"
#include "MsgHook.h"

#define MAX_LOADSTRING 100

// Global Variables:
HINSTANCE hInst;								// current instance
TCHAR szTitle[MAX_LOADSTRING];					// The title bar text
TCHAR szWindowClass[MAX_LOADSTRING];			// the main window class name
HWND mainHwnd = NULL;
HWND txtbox = NULL;
HWND targetHwnd = NULL;
//TCHAR targetClassname[100] = _T("Notepad");
TCHAR targetClassname[100] = _T("WordPadClass");
bool filterWmCommand = true;
bool filterWmNotify = false;
bool filterAbove = false;


// Forward declarations of functions included in this code module:
ATOM				MyRegisterClass(HINSTANCE hInstance);
BOOL				InitInstance(HINSTANCE, int);
LRESULT CALLBACK	WndProc(HWND, UINT, WPARAM, LPARAM);
INT_PTR CALLBACK	About(HWND, UINT, WPARAM, LPARAM);

int APIENTRY _tWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPTSTR lpCmdLine, int nCmdShow)
{

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

	// Main message loop:
	while (GetMessage(&msg, NULL, 0, 0))
	{
		if (!TranslateAccelerator(msg.hwnd, hAccelTable, &msg))
		{
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
	}

	return (int) msg.wParam;
}

void AppendText(HWND txtHwnd, LPCTSTR newText)
{
	
	DWORD len = GetWindowTextLength(txtHwnd);
	if (len > 25000)
	{//need to truncate the beginning so the text doesn't go past it's limit
		SendMessage(txtHwnd, EM_SETSEL, 0, 10000);
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
	if (!filterWmCommand && !filterAbove && !filterWmNotify)
		InitializeMsgLookup();
	else
	{
		int allowList[3];
		for (int i = 0; i < 3; i ++)
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
		InitializeMsgLookup(allowList, 3);
	}
}

void StartMessageHook()
{
	AppendText(txtbox, _T("Starting Message Hook\r\n"));
	targetHwnd = FindWindow(targetClassname, NULL);
	DWORD tid = GetWindowThreadProcessId(targetHwnd, NULL);

	InitMsgFiltersAndLookup();
	//InitializeMsgLookup();

	TCHAR tmp[50];
	_stprintf_s(tmp, _T("Targetting %ld, %ld\r\n"), targetHwnd, tid);
	AppendText(txtbox, tmp);
	
	//block self/global msg hook
	if (targetHwnd == NULL || tid == 0) {
		AppendText(txtbox, _T("Target window not found\r\n"));
		return;
	}
	
	if (InitHook(mainHwnd, tid))
	{
		AppendText(txtbox, _T("Hook successfully initialized\r\n"));
	}
	else
		AppendText(txtbox, _T("Hook failed to initialize\r\n"));
}

void StopMessageHook()
{
	AppendText(txtbox, TEXT("Stopping Message Hook\r\n"));
	KillHook();
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
			TCHAR tmp[200];
			_stprintf_s(tmp, _T("msg: %s (%ld), wparam: %ld, lparam: %ld\r\n"), msgName, Event.nCode, Event.wParam, Event.lParam);
			AppendText(txtbox, tmp);
		}
	}
	return false;
}

void SendWmSettext() //ID_TESTMSGS_WM
{
	//SetWindowText(targetHwnd, _T("This is a test"));
	TCHAR txt[] = _T("This is a test");
	SendMessage(targetHwnd, WM_SETTEXT, 0 , (LPARAM)txt);
	//PostMessage(targetHwnd, WM_SETTEXT, 0 , (LPARAM)txt);
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
	wcex.hIcon			= LoadIcon(hInstance, MAKEINTRESOURCE(IDI_MSGHOOKTEST));
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
      CW_USEDEFAULT, 0, CW_USEDEFAULT, 0, NULL, NULL, hInstance, NULL);

   if (!hWnd)
      return FALSE;
   mainHwnd = hWnd;
   
   RECT rect;
   GetClientRect(hWnd, &rect);
   // make the txtbox edit control almost the same size as the parent window
   //WS_CHILD | WS_VISIBLE | WS_VSCROLL | ES_LEFT | ES_MULTILINE | ES_AUTOVSCROLL
   txtbox = CreateWindow(TEXT("Edit"),TEXT(""), WS_CHILD | WS_VISIBLE | ES_MULTILINE | WS_VSCROLL | ES_AUTOVSCROLL | ES_READONLY, 
	   10, 10,rect.right - 20, rect.bottom - 20, hWnd, NULL, NULL, NULL);

   ShowWindow(hWnd, nCmdShow);
   UpdateWindow(hWnd);

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
		case ID_FILE_SETTINGS:
			DialogBox(hInst, MAKEINTRESOURCE(IDD_DIALOG1), hWnd, About);
			break;
		case IDM_ABOUT:
			DialogBox(hInst, MAKEINTRESOURCE(IDD_ABOUTBOX), hWnd, About);
			break;
		case IDM_EXIT:
			DestroyWindow(hWnd);
			break;
		default:
			return DefWindowProc(hWnd, message, wParam, lParam);
		}
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
			SetWindowPos(txtbox, HWND_NOTOPMOST, 10, 10, nWidth-20, nHeight-20, SWP_NOZORDER|SWP_NOMOVE);
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
INT_PTR CALLBACK About(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	UNREFERENCED_PARAMETER(lParam);
	switch (message)
	{
	case WM_INITDIALOG:
		{
			//IDC_EDIT1
			HWND classnameHwnd = GetDlgItem(hDlg, IDC_EDIT1);  
			if (classnameHwnd != NULL)
				SetWindowText(classnameHwnd, targetClassname);

			if (filterWmCommand)
				SendDlgItemMessage(hDlg, IDC_CHECK_CMD, BM_SETCHECK, BST_CHECKED, 0);
			if (filterWmNotify)
				SendDlgItemMessage(hDlg, IDC_CHECK_NOT, BM_SETCHECK, BST_CHECKED, 0);
			if (filterAbove)
				SendDlgItemMessage(hDlg, IDC_CHECK_ABO, BM_SETCHECK, BST_CHECKED, 0);

		}
		return (INT_PTR)TRUE;

	case WM_COMMAND:
		if (LOWORD(wParam) == IDOK) //only save on OK
		{
			HWND classnameHwnd = GetDlgItem(hDlg, IDC_EDIT1);  
			if (classnameHwnd != NULL)
				GetWindowText(classnameHwnd, targetClassname, 100);
			// check filter options
			if (SendDlgItemMessage(hDlg, IDC_CHECK_CMD, BM_GETCHECK, 0, 0) == BST_CHECKED) // the hard way
				filterWmCommand = true;
			else
				filterWmCommand = false;
			if (IsDlgButtonChecked(hDlg, IDC_CHECK_NOT) == BST_CHECKED) // the easy way
				filterWmNotify = true;
			else
				filterWmNotify = false;
			if (IsDlgButtonChecked(hDlg, IDC_CHECK_ABO) == BST_CHECKED)
				filterAbove = true;
			else
				filterAbove = false;
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
