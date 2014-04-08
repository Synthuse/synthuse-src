package org.synthuse;

import com.sun.jna.platform.win32.WinDef.HWND;

public class WinPtr {

	public HWND hWnd = null;
	public String hWndStr = "";
	public String runtimeId = "";
	
	public WinPtr() {
	}
	
	public WinPtr(HWND hWnd) {
		this.hWnd = hWnd;
		this.hWndStr = Api.GetHandleAsString(hWnd);
	}

	public WinPtr(String runtimeId) {
		this.runtimeId = runtimeId;
	}
	
	public boolean isWin32() {
		return (hWnd != null || !hWndStr.equals(""));
	}
	
	public boolean isWpf() {
		return (!runtimeId.equals(""));
	}
	
	public boolean isEmpty() {
		return (hWnd == null && hWndStr.equals("") && runtimeId.equals(""));
	}
	
	public static boolean isWpfRuntimeIdFormat(String runtimeIdTest) {
		return (runtimeIdTest.contains("-"));
	}
	
	public String toString() {
		if (isWin32() && !hWndStr.equals(""))
			return hWndStr;
		else if (isWin32() && hWnd != null)
		{
			hWndStr = Api.GetHandleAsString(hWnd);
			return hWndStr;
		}
		else if (isWpf())
			return runtimeId;
		else
			return null;
	}
}
