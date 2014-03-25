/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.Point;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HPEN;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;

public class Api {
	
    public static int WM_SETTEXT = 0x000c;
    public static int WM_GETTEXT = 0x000D;
    public static int WM_GETTEXTLENGTH = 0x000E;
    public static int WM_MOUSEMOVE = 0x200;
    public static int WM_LBUTTONDOWN = 0x0201;
    public static int WM_LBUTTONUP = 0x0202;
    public static int WM_LBUTTONDBLCLK = 0x203;
    public static int WM_RBUTTONDOWN = 0x0204;
    public static int WM_RBUTTONUP = 0x0205;
    public static int WM_RBUTTONDBLCLK = 0x206;
    public static int WM_MBUTTONDOWN = 0x207;
    public static int WM_MBUTTONUP = 0x208;
    public static int WM_MBUTTONDBLCLK = 0x209;
    public static int WM_MOUSEWHEEL = 0x20A;
    public static int WM_MOUSEHWHEEL = 0x20E;
    public static int WM_MOUSEHOVER = 0x2A1;
    public static int WM_NCMOUSELEAVE = 0x2A2;
    public static int WM_MOUSELEAVE = 0x2A3;
    
    public static int WM_CLOSE = 0x10;
    public static int WM_DESTROY = 0x0002;
    public static int WM_NCDESTROY = 0x0082;
    public static int WM_QUIT = 0x12; 
    
    public static int WM_SETFOCUS = 0x0007;
    public static int WM_NEXTDLGCTL = 0x0028;
    public static int WM_ENABLE = 0x000A; 
    public static int WM_KEYFIRST = 0x100;
    public static int WM_KEYDOWN = 0x100;
    public static int WM_KEYUP = 0x101;
    public static int WM_CHAR = 0x102;
    public static int WM_DEADCHAR = 0x103;
    public static int WM_SYSKEYDOWN = 0x104;
    public static int WM_SYSKEYUP = 0x105;
    public static int WM_SYSCHAR = 0x106;
    
	public static int WM_CUT = 0x300;
	public static int WM_COPY = 0x301;
	public static int WM_PASTE = 0x302;
	public static int WM_CLEAR = 0x303;
	public static int WM_UNDO = 0x304;

    
    public static int PROCESS_QUERY_INFORMATION = 0x0400;
    public static int PROCESS_VM_READ = 0x0010;
    
    public static int PS_SOLID = 0x0;
    public static int HOLLOW_BRUSH = 0x5;
    
    public static int WM_PAINT = 0x0F;
    public static int WM_SETREDRAW = 0x0B;
    public static int WM_ERASEBKGND = 0x14;
    
    public static int RDW_FRAME = 0x0400;
    public static int RDW_INVALIDATE = 0x0001;
    public static int RDW_UPDATENOW = 0x0100;
    public static int RDW_ALLCHILDREN = 0x0080;

    public static int VK_SHIFT = 16;
    public static int VK_LSHIFT = 0xA0;
    public static int VK_RSHIFT = 0xA1;
    public static int VK_CONTROL = 17;
    public static int VK_LCONTROL = 0xA2;
    public static int VK_RCONTROL = 0xA3;
    public static int VK_MENU = 18;
    public static int VK_LMENU = 0xA4;
    public static int VK_RMENU = 0xA5;
    
    public static int CWP_ALL = 0x0000; //    Does not skip any child windows
	
    public User32 user32;
    public Psapi psapi;
    public Kernel32 kernel32;

    public static final int POINT_Y(long i)
    {
        return (int) (i  >> 32);
    }
    
    public static final int POINT_X(long i)
    {
        return (int) (i & 0xFFFF);
    }    
	
	public interface User32 extends W32APIOptions {  
		User32 instance = (User32) Native.loadLibrary("user32", User32.class, DEFAULT_OPTIONS);  
		
		boolean ShowWindow(HWND hWnd, int nCmdShow);  
		boolean SetForegroundWindow(HWND hWnd);
		void SwitchToThisWindow(HWND hWnd, boolean fAltTab);
		HWND SetFocus(HWND hWnd);
		
		HWND FindWindow(String winClass, String title);
		LRESULT PostMessage(HWND hWnd, int Msg, WPARAM wParam, LPARAM lParam);
		LRESULT SendMessage(HWND hWnd, int Msg, WPARAM wParam, LPARAM lParam);
	    LRESULT SendMessageA(HWND editHwnd, int wmGettext, long l, byte[] lParamStr);
	    boolean DestroyWindow(HWND hWnd);
		
		boolean EnumWindows (WNDENUMPROC wndenumproc, int lParam);
		boolean EnumChildWindows(HWND hWnd, WNDENUMPROC lpEnumFunc, Pointer data);
		HWND GetParent(HWND hWnd);
		boolean IsWindowVisible(HWND hWnd);
		
		int GetWindowRect(HWND hWnd, RECT r);
		int MapWindowPoints(HWND hWndFrom, HWND hWndTo, RECT r, int cPoints);
		HWND GetDesktopWindow();
		HDC GetWindowDC(HWND hWnd);
		int ReleaseDC(HWND hWnd, HDC hDC);
		boolean InvalidateRect(HWND hWnd, long lpRect, boolean bErase);
		boolean UpdateWindow(HWND hWnd);
		boolean RedrawWindow(HWND hWnd, long lprcUpdate, long hrgnUpdate, int flags);
		
		void GetWindowTextA(HWND hWnd, byte[] buffer, int buflen);
		int GetTopWindow(HWND hWnd);
		int GetWindow(HWND hWnd, int flag);
		final int GW_HWNDNEXT = 2;
		int GetClassName(HWND hWnd, char[] buffer2, int i);
		int GetWindowModuleFileName(HWND hWnd, char[] buffer2, int i);
		int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
		
		boolean GetCursorPos(long[] lpPoint); //use macros POINT_X() and POINT_Y() on long lpPoint[0]

		HWND WindowFromPoint(long point);
		HWND ChildWindowFromPointEx(HWND hwndParent, long point, int uFlags);
		boolean ClientToScreen(HWND hWnd, long[] lpPoint);//use macros POINT_X() and POINT_Y() on long lpPoint[0]
		boolean ScreenToClient(HWND hWnd, long[] lpPoint);//use macros POINT_X() and POINT_Y() on long lpPoint[0]
		//HWND WindowFromPoint(int xPoint, int yPoint);
		//HWND WindowFromPoint(POINT point);
	}  
	
	public interface Gdi32 extends W32APIOptions {  
		Gdi32 instance = (Gdi32) Native.loadLibrary("gdi32", Gdi32.class, DEFAULT_OPTIONS); 
		HANDLE SelectObject(HDC hdc, HANDLE hgdiobj);
		HANDLE GetStockObject(int fnObject);
		boolean Rectangle(HDC hdc, int nLeftRect, int nTopRect, int nRightRect, int nBottomRect);
		HPEN CreatePen(int fnPenStyle, int nWidth, int crColor);
	}
	
	public interface Psapi extends W32APIOptions {  
		Psapi instance = (Psapi) Native.loadLibrary("psapi", Psapi.class, DEFAULT_OPTIONS); 
		int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
	}

	public interface Kernel32 extends W32APIOptions {
		Kernel32 instance = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, DEFAULT_OPTIONS);  
		boolean GetDiskFreeSpaceEx(String lpDirectoryName, LARGE_INTEGER.ByReference lpFreeBytesAvailable, LARGE_INTEGER.ByReference lpTotalNumberOfBytes, LARGE_INTEGER.ByReference lpTotalNumberOfFreeBytes);
	    int GetLastError();
	    Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
	}
	
	
	public Api() {
		user32 = User32.instance;
	    psapi = Psapi.instance;
	    kernel32 = Kernel32.instance;
	}
	
    public static String GetHandleAsString(HWND hWnd) {
    	if (hWnd == null)
    		return "0";
    	//String longHexStr = hWnd.toString().substring("native@".length());
    	String longHexStr = hWnd.getPointer().toString().substring("native@".length());
    	Long l = Long.decode(longHexStr);
    	return l.toString();
    }

    public static HWND GetHandleFromString(String hWnd) {
    	if (hWnd == null)
    		return null;
    	if (hWnd.isEmpty())
    		return null;
    	String cleanNumericHandle = hWnd.replaceAll("[^\\d.]", "");
    	try {
    		return (new HWND(new Pointer(Long.parseLong(cleanNumericHandle))));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public static String GetWindowClassName(HWND hWnd) {
        char[] buffer = new char[1026];
		User32.instance.GetClassName(hWnd, buffer, 1026);
		return Native.toString(buffer);
    }
    
    public static String GetWindowText(HWND hWnd) {
    	String text = "";
        byte[] buffer = new byte[1024];
        User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
        text = Native.toString(buffer);
        if (text.isEmpty())
        	text = new Api().sendWmGetText(hWnd);
        return text;
    }
    
    public static HWND GetWindowFromPoint(Point p) {
    	
    	long[] getPos = new long [1];
    	User32.instance.GetCursorPos(getPos);
    	HWND hwnd = User32.instance.WindowFromPoint(getPos[0]);
    	HWND childHwnd = GetHiddenChildWindowFromPoint(hwnd, getPos[0]);
    	hwnd = childHwnd;
    	//System.out.println(getPos[0] + "," + getPos[1] + " int: " + x + ", " + y);
    	//System.out.println("child: " + GetHandleAsString(childHwnd)  + " " + POINT_X(getPos[0]) +", " + POINT_Y(getPos[0]));
    	return hwnd;
    }
    
    public static HWND GetHiddenChildWindowFromPoint(HWND inHwnd, long point)
    {
    	//int x = POINT_X(point);int y = POINT_Y(point);

    	long[] getPos = new long [1];
    	getPos[0] = point;
        //if (!User32.instance.ClientToScreen(inHwnd, getPos)) return lHWND;
    	//x = POINT_X(getPos[0]);y = POINT_Y(getPos[0]);
        //System.out.println("ClientToScreen " + GetHandleAsString(inHwnd) + ", " + x  + ", " + y);
        
        if (!User32.instance.ScreenToClient(inHwnd, getPos)) return inHwnd; // if point is not correct use original hwnd.
        //x = POINT_X(getPos[0]);y = POINT_Y(getPos[0]);
        //System.out.println("ScreenToClient " + GetHandleAsString(inHwnd) + ", " + x  + ", " + y);

        HWND childHwnd = User32.instance.ChildWindowFromPointEx(inHwnd, getPos[0], CWP_ALL);
    	//System.out.println("ChildWindowFromPointEx2 " + GetHandleAsString(inHwnd) + ", " + x  + ", " + y  + " = " + GetHandleAsString(childHwnd));

        if (childHwnd == null) // if childHwnd is not correct use original hwnd.
        	return inHwnd;
       
        return childHwnd;
    }
    
	public HWND findWindowByTitle(String title) {
		HWND handle = user32.FindWindow(null, title);
		return handle;
	}
	
	public boolean activateWindow(HWND handle) {
		boolean result = user32.SetForegroundWindow(handle);
		user32.SetFocus(handle);
		return result;
	}
	
	public void SetDialogFocus(HWND hdlg, HWND hwndControl) {
		WPARAM wp = new WPARAM(hwndControl.getPointer().getLong(0));
		LPARAM lp = new LPARAM(1);
		user32.SendMessage(hdlg, WM_NEXTDLGCTL, wp, lp); 
	}
	
	public boolean showWindow(HWND handle) {
		return user32.ShowWindow(handle, WinUser.SW_SHOW); 
	}
	
	public boolean hideWindow(HWND handle) {
		return user32.ShowWindow(handle, WinUser.SW_HIDE); 
	}

	public boolean minimizeWindow(HWND handle) {
		return user32.ShowWindow(handle, WinUser.SW_MINIMIZE); 
	}
	
	public boolean maximizeWindow(HWND handle) {
		return user32.ShowWindow(handle, WinUser.SW_MAXIMIZE); 
	}
	
	public boolean restoreWindow(HWND handle) {
		return user32.ShowWindow(handle, WinUser.SW_RESTORE); 
	}
	
	public boolean closeWindow(HWND handle) {
		//return user32.DestroyWindow(handle);
		//user32.SendMessage(handle, WM_CLOSE , null, null);
		user32.PostMessage(handle, WM_CLOSE , null, null);
		//user32.SendMessage(handle, WM_NCDESTROY , null, null);
		return true;
	}

	public void switchToThisWindow(HWND handle, boolean fAltTab) {
		user32.SwitchToThisWindow(handle, fAltTab);
	}

	public String sendWmGetText(HWND handle) {
		int bufSize = 8192;
		byte[] lParamStr = new byte[bufSize];
		user32.SendMessageA(handle, WM_GETTEXT, bufSize, lParamStr);
		return (Native.toString(lParamStr));
	}
	
	public void sendWmSetText(HWND handle, String text) {
		user32.SendMessageA(handle, WM_SETTEXT, 0, Native.toByteArray(text));
	}
	
	public void sendClick(HWND handle) {
		user32.SendMessageA(handle, WM_LBUTTONDOWN, 0, null);
		user32.SendMessageA(handle, WM_LBUTTONUP, 0, null);
	}

	public void sendDoubleClick(HWND handle) {
		user32.SendMessageA(handle, WM_LBUTTONDBLCLK, 0, null);
		//user32.SendMessageA(handle, WM_LBUTTONUP, 0, null);
	}

	public void sendRightClick(HWND handle) {
		user32.SendMessageA(handle, WM_RBUTTONDOWN, 0, null);
		user32.SendMessageA(handle, WM_RBUTTONUP, 0, null);
	}
	
	public void sendKeyDown(HWND handle, int keyCode) {
		user32.SendMessageA(handle, WM_KEYDOWN, keyCode, null);
		//user32.SendMessageA(handle, WM_KEYUP, keyCode, null);
	}
	
	public void sendKeyUp(HWND handle, int keyCode) {
		//user32.SendMessageA(handle, WM_KEYDOWN, keyCode, null);
		user32.SendMessageA(handle, WM_KEYUP, keyCode, null);
	}
	
	public Point getWindowPosition(HWND handle) {
		Point windowPoint = new Point();
		RECT rect = new RECT();
		user32.GetWindowRect(handle, rect);
		//System.out.println("rect: l" + rect.left + ",t" + rect.top + ",r" + rect.right + ",b" + rect.bottom);
		//user32.MapWindowPoints(user32.GetDesktopWindow(), user32.GetParent(handle), rect, 2);
		windowPoint.x = ((rect.right - rect.left) / 2) + rect.left;
		windowPoint.y = ((rect.bottom - rect.top) / 2) + rect.top;
		return windowPoint;
	}
	
	public int getDiskUsedPercentage() {
		return getDiskUsedPercentage(null);
	}
	
	public int getDiskUsedPercentage(String target) {
		LARGE_INTEGER.ByReference lpFreeBytesAvailable = new LARGE_INTEGER.ByReference();
        LARGE_INTEGER.ByReference lpTotalNumberOfBytes = new LARGE_INTEGER.ByReference();
        LARGE_INTEGER.ByReference lpTotalNumberOfFreeBytes = new LARGE_INTEGER.ByReference();
        Kernel32.instance.GetDiskFreeSpaceEx(target, lpFreeBytesAvailable, lpTotalNumberOfBytes, lpTotalNumberOfFreeBytes);
        double freeBytes = lpTotalNumberOfFreeBytes.getValue();
        double totalBytes = lpTotalNumberOfBytes.getValue();
        //System.out.println("freespace " + humanReadableByteCount(freeBytes) + "/ totalspace " + humanReadableByteCount(totalBytes));
        return (int)(((totalBytes-freeBytes)/totalBytes) * 100.0);
	}
	
	public static String humanReadableByteCount(double bytes) {
		boolean si = true;
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	// creates highlight around selected window
	public static void highlightWindow(HWND hwnd){
		//COLORREF i.e. 0x00804070  Red = 0x70 green = 0x40 blue = 0x80
		//g_hRectanglePen = CreatePen (PS_SOLID, 3, RGB(256, 0, 0));
		HPEN rectPen = Gdi32.instance.CreatePen(PS_SOLID, 3, 0x00000099); //RGB(255, 0, 0)
		RECT rect = new RECT();
		User32.instance.GetWindowRect(hwnd, rect);
		HDC dc = User32.instance.GetWindowDC(hwnd);
		if (dc != null) {
			// Select our created pen into the DC and backup the previous pen.
		    HANDLE prevPen = Gdi32.instance.SelectObject(dc, rectPen);
		    
		    // Select a transparent brush into the DC and backup the previous brush.
		    HANDLE prevBrush = Gdi32.instance.SelectObject(dc, Gdi32.instance.GetStockObject(HOLLOW_BRUSH));

		    // Draw a rectangle in the DC covering the entire window area of the found window.
		    Gdi32.instance.Rectangle (dc, 0, 0, rect.right - rect.left, rect.bottom - rect.top);

		    // Reinsert the previous pen and brush into the found window's DC.
		    Gdi32.instance.SelectObject(dc, prevPen);
		    Gdi32.instance.SelectObject(dc, prevBrush);

		    // Finally release the DC.
		    User32.instance.ReleaseDC(hwnd, dc);
		}
	}
	
	public static void refreshWindow(HWND hwnd) {
		User32.instance.InvalidateRect(hwnd, 0, true);
		User32.instance.UpdateWindow(hwnd);
		User32.instance.RedrawWindow(hwnd, 0, 0, RDW_FRAME | RDW_INVALIDATE | RDW_UPDATENOW | RDW_ALLCHILDREN);
	}
	
}
