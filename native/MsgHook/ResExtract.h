//This function will extract a binary resource.
//
//IDR_SETMH64            BINARY  MOVEABLE PURE   "..\\SetMsgHook\\bin\\SetMsgHook64.exe"
//IDR_SETMH32            BINARY  MOVEABLE PURE   "..\\SetMsgHook\\bin\\SetMsgHook32.exe"

#include "stdafx.h"

void ExtractResource(const WORD nID, LPCTSTR szFilename)
{
	const HINSTANCE hInstance = (HINSTANCE)pData->g_hInstance;//GetModuleHandle(NULL);
	HRSRC hResource = FindResource(hInstance, MAKEINTRESOURCE(nID), _T("BINARY"));// _ASSERTE(hResource);
	if (hResource == NULL) // no resource found.
	{
		//MessageBoxA(0, "error, no resource found", "error", 0);
		printf("error, resource %d not found\n", nID);
		return;
	}
	HGLOBAL hFileResource = LoadResource(hInstance, hResource);// _ASSERTE(hFileResource);
	LPVOID lpFile = LockResource(hFileResource);

	DWORD dwSize = SizeofResource(hInstance, hResource);

	// Open the file and filemap
	HANDLE hFile = CreateFile(szFilename, GENERIC_READ | GENERIC_WRITE, 0, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
	HANDLE hFilemap = CreateFileMapping(hFile, NULL, PAGE_READWRITE, 0, dwSize, NULL);

	// Get a pointer to write to
	LPVOID lpBaseAddress = MapViewOfFile(hFilemap, FILE_MAP_WRITE, 0, 0, 0);

	// Write the file
	CopyMemory(lpBaseAddress, lpFile, dwSize);

	// Unmap the file and close the handles
	UnmapViewOfFile(lpBaseAddress);
	CloseHandle(hFilemap);
	CloseHandle(hFile);
	
}

void RunResource(const WORD nID, LPWSTR params)
{
	TCHAR tmpFilename[500];
	TCHAR tmpPath[500];
	GetTempPath(500, tmpPath);
	if (GetTempFileName(tmpPath, _T(""), 0, tmpFilename) == 0)
	{
		MessageBox(0,_T("Error getting temp file name"), _T("Error"), 0);
		return;
	}

	ExtractResource(nID, tmpFilename);
	//MessageBox(0, tmpFilename, _T("tmp file2"), 0);

	STARTUPINFO si;
    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);

    PROCESS_INFORMATION pi;
    ZeroMemory( &pi, sizeof(pi) );

	si.wShowWindow = SW_MINIMIZE;
	
    // Start the child process. 
    if(!CreateProcess(tmpFilename,   // No module name (use command line)
        params,        // Command line
        NULL,           // Process handle not inheritable
        NULL,           // Thread handle not inheritable
        FALSE,          // Set handle inheritance to FALSE
        0,              // No creation flags
        NULL,           // Use parent's environment block
        NULL,           // Use parent's starting directory 
        &si,            // Pointer to STARTUPINFO structure
        &pi ))          // Pointer to PROCESS_INFORMATION structure
    {
		MessageBox(0, _T("CreateProcess failed"), _T("error"), 0);
        printf( "CreateProcess failed (%d).\n", GetLastError() );
        return;
    }
	//MessageBox(0, tmpFilename, _T("tmp file3"), 0);

    // Wait until child process exits.
    //WaitForSingleObject( pi.hProcess, INFINITE );
    // Close process and thread handles. 
    //CloseHandle( pi.hProcess );
    //CloseHandle( pi.hThread );
}