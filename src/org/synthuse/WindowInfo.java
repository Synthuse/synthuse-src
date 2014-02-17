/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import org.synthuse.Api.*;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.ptr.PointerByReference;

public class WindowInfo {
	
	public HWND hwnd;
	public String hwndStr = "";
	public HWND parent = null;
	public String parentStr = "";
	public RECT rect;
	public String text;
	public String className = "";
	public boolean isChild = false;
	public String processName = "";
	public long pid = 0;
	public Object xmlObj = null;
    
    public WindowInfo(HWND hWnd, boolean isChild) {
        byte[] buffer = new byte[1024];
        User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
        text = Native.toString(buffer);
        if (text.isEmpty())
        	text = new Api().sendWmGetText(hWnd);
        
        char[] buffer2 = new char[1026];
		User32.instance.GetClassName(hWnd, buffer2, 1026);
		className = Native.toString(buffer2);
		
		rect = new RECT();
		User32.instance.GetWindowRect(hWnd, rect);
		
		this.isChild = isChild;
		if (isChild) {
			parent = User32.instance.GetParent(hWnd);
			parentStr = Api.GetHandleAsString(parent);
		}
		else {
			//User32.instance.GetWindowModuleFileName(hWnd, path, 512);
			PointerByReference pointer = new PointerByReference();
			User32.instance.GetWindowThreadProcessId(hWnd, pointer);
			Pointer p = pointer.getPointer();
			pid = p.getLong(0);
		    Pointer process = Kernel32.instance.OpenProcess(Api.PROCESS_QUERY_INFORMATION | Api.PROCESS_VM_READ, false, pointer.getValue());
		    Psapi.instance.GetModuleBaseNameW(process, null, buffer2, 512);
		    processName = Native.toString(buffer2);
			//processName = Native.toString(path);
		}
		this.hwnd = hWnd;
		hwndStr = Api.GetHandleAsString(hWnd);
		
    }
    
    public String toString() {
        return String.format("(%d,%d)-(%d,%d) : \"%s\" [%s] {%s}", rect.left,rect.top,rect.right,rect.bottom,text,className,hwnd.toString());
    }

}
