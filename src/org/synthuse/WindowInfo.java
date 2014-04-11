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
	
	public static String WPF_PROPERTY_LIST = "RuntimeIdProperty,ParentRuntimeIdProperty,ProcessIdProperty,FrameworkIdProperty,ClassNameProperty,NameProperty,ValueProperty";
	
	public HWND hwnd;
	public String hwndStr = "";
	public HWND parent = null;
	public String parentStr = "";
	public RECT rect;
	public String text;
	public String value;
	public String className = "";
	public boolean isChild = false;
	public String processName = "";
	public long pid = 0;
	public Object xmlObj = null;
	public String framework = "win32";//default as win32
	public String runtimeId = "";
    
	//Default Win32 support
    public WindowInfo(HWND hWnd, boolean isChild) {
    	this.framework = "win32";
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
			
			PointerByReference pointer = new PointerByReference();
			User32.instance.GetWindowThreadProcessId(hWnd, pointer);
			pid = pointer.getPointer().getInt(0);
		    Pointer process = Kernel32.instance.OpenProcess(Api.PROCESS_QUERY_INFORMATION | Api.PROCESS_VM_READ, false, pointer.getValue());
		    Psapi.instance.GetModuleBaseNameW(process, null, buffer2, 512);
		    processName = Native.toString(buffer2);
		    
		}
		this.hwnd = hWnd;
		hwndStr = Api.GetHandleAsString(hWnd);
    }
    
    public String replaceEscapedCodes(String input) {
    	//&#44; is a comma ,
    	String result = input;
    	result = result.replaceAll("&#44;", ",");
    	result = result.replaceAll("&lt;", "<");
    	result = result.replaceAll("&gt;", ">");
    	result = result.replaceAll("&apos;", "'");
    	result = result.replaceAll("&quot;", "\"");
    	result = result.replaceAll("&amp;", "&");
    	return result;
    }
    
    //support for WPF and Silverlight
    public WindowInfo(String enumProperties, boolean isChild) {
    	//WPF_PROPERTY_LIST = "RuntimeIdProperty,ParentRuntimeIdProperty,ProcessIdProperty,FrameworkIdProperty,ClassNameProperty,NameProperty";
    	String[] spltProperties = enumProperties.split(",");
    	if (spltProperties.length > 0)
    		this.runtimeId = spltProperties[0];
    	this.hwndStr = this.runtimeId;
    	if (spltProperties.length > 1 && isChild)
    		this.parentStr = spltProperties[1];
    	this.isChild = isChild;
    	if (spltProperties.length > 2)
    		this.pid = Long.parseLong(spltProperties[2]);
    	if (spltProperties.length > 3)
    		this.framework = spltProperties[3];
    	if (spltProperties.length > 4)
    		this.className = replaceEscapedCodes(spltProperties[4]);
    	if (spltProperties.length > 5)
    		this.text = replaceEscapedCodes(spltProperties[5]);
    	if (spltProperties.length > 6)
    		this.value = replaceEscapedCodes(spltProperties[6]);
    	/*
    	this.rect = new RECT();
    	try {
	    	String rectStr = wb.getProperty("BoundingRectangleProperty", runtimeId);
	    	String[] rectSplt = rectStr.split(",");
	    	this.rect.right = Integer.parseInt(rectSplt[0]);
	    	this.rect.bottom = Integer.parseInt(rectSplt[1]);
	    	this.rect.left = Integer.parseInt(rectSplt[2]);
	    	this.rect.top = Integer.parseInt(rectSplt[3]);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	*/
    }
    
    public String toString() {
        return String.format("(%d,%d)-(%d,%d) : \"%s\" [%s] {%s}", rect.left,rect.top,rect.right,rect.bottom,text,className,hwnd.toString());
    }

}
