/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.util.LinkedHashMap;
import java.util.Map;

import org.synthuse.Api.*;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
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
	public int menus = 0;
	public HMENU menu = null;
	
	public Map<String, String> extra = null;
    
	//Default Win32 support
    public WindowInfo(HWND hWnd, boolean isChild) {
    	this.framework = "win32";
        byte[] buffer = new byte[1024];
        User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
        text = Native.toString(buffer);
        if (text.isEmpty())
        	text = new Api().sendWmGetText(hWnd);
        if (text.isEmpty()) {
        	//System.out.println("getting toolbar text");
        }
        
        //Get item count depending on what type of control it is
    	LRESULT tbCount = Api.User32.instance.SendMessage(hWnd, Api.TB_BUTTONCOUNT, new WPARAM(0), new LPARAM(0));
    	if (tbCount.intValue() > 0) { // toolbar button count
    		//System.out.println("TB_BUTTONCOUNT: " + tbCount.intValue());
    		if (extra == null)
    			extra = new LinkedHashMap<String, String>(); 
    		extra.put("tbCount", tbCount.intValue() + "");
    		//Api.User32.instance.SendMessageA(hWnd, Api.TB_GETTOOLTIPS, 0, buffer);
    		//text = Native.toString(buffer);
    	}
    	LRESULT lvCount = Api.User32.instance.SendMessage(hWnd, Api.LVM_GETITEMCOUNT, new WPARAM(0), new LPARAM(0));
    	if (lvCount.intValue() > 0) { // listview item count
    		if (extra == null)
    			extra = new LinkedHashMap<String, String>(); 
    		extra.put("lvCount", lvCount.intValue() + "");
    	}
    	LRESULT lbCount = Api.User32.instance.SendMessage(hWnd, Api.LB_GETCOUNT, new WPARAM(0), new LPARAM(0));
    	if (lbCount.intValue() > 0) { // listbox item count
    		if (extra == null)
    			extra = new LinkedHashMap<String, String>(); 
    		extra.put("lbCount", lbCount.intValue() + "");
    	}
    	LRESULT cbCount = Api.User32.instance.SendMessage(hWnd, Api.CB_GETCOUNT, new WPARAM(0), new LPARAM(0));
    	if (cbCount.intValue() > 0) { // listbox item count
    		if (extra == null)
    			extra = new LinkedHashMap<String, String>(); 
    		extra.put("cbCount", cbCount.intValue() + "");
    	}
    	LRESULT tvCount = Api.User32.instance.SendMessage(hWnd, Api.TVM_GETCOUNT, new WPARAM(0), new LPARAM(0));
		if (tvCount.intValue() > 0) { //treeview node count
			if (extra == null)
				extra = new LinkedHashMap<String, String>(); 
			extra.put("tvCount", tvCount.intValue() + "");
		}
		
		//check if window has a menu
    	HMENU hmenu = Api.User32.instance.GetMenu(hWnd);
		if (hmenu != null) { //menu item count
	    	int menuCount = Api.User32.instance.GetMenuItemCount(hmenu);
	    	if (menuCount > 0) {
				this.menus = menuCount;
				this.menu = hmenu;
	    	}
	    	else
	    	{
	    		LRESULT result = Api.User32.instance.PostMessage(hWnd, Api.MN_GETHMENU, new WPARAM(0), new LPARAM());
	    		if (result.longValue() != 1)
	    			System.out.println("MN_GETHMENU: " + result.longValue());
	    	}
		}
		
		if (isChild) {
			int ctrlID =  Api.User32.instance.GetDlgCtrlID(hWnd);
			if (ctrlID > 0){
				//parent = User32.instance.GetParent(hWnd);
				int dtresult = Api.User32.instance.GetDlgItemText(hWnd, ctrlID, buffer, 1024);
				if (dtresult > 0) {
			        String dgText = Native.toString(buffer);
					if (extra == null)
						extra = new LinkedHashMap<String, String>(); 
					extra.put("dgText", dgText + "");
				}
			}
		}
		
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
    	this.isChild = isChild;
    	if (SynthuseDlg.config.isFilterWpfDisabled()) { //use wildcard mode
    		extra = new LinkedHashMap<String, String>();
    		for(String prop: spltProperties) {
    			String[] propertyNameAndValue = prop.split(":", 2);
    			if (propertyNameAndValue.length < 2)
    				continue;
    			
    			if (propertyNameAndValue[0].equals("RuntimeIdProperty"))
    				this.runtimeId = propertyNameAndValue[1];
    			else if (propertyNameAndValue[0].equals("ParentRuntimeIdProperty"))
    				this.parentStr = propertyNameAndValue[1];
    			else if (propertyNameAndValue[0].equals("ProcessIdProperty"))
    				this.pid = Long.parseLong(propertyNameAndValue[1]);
    			else if (propertyNameAndValue[0].equals("FrameworkIdProperty"))
    				this.framework = propertyNameAndValue[1];
    			else if (propertyNameAndValue[0].equals("ClassNameProperty"))
    				this.className = propertyNameAndValue[1];
    			else if (propertyNameAndValue[0].equals("NameProperty"))
    				this.text = propertyNameAndValue[1];
    			else if (propertyNameAndValue[0].equals("ValueProperty"))
    				this.value = propertyNameAndValue[1];
    			else{
    				extra.put(propertyNameAndValue[0], propertyNameAndValue[1]);
    			}
    		}
        	this.hwndStr = this.runtimeId;
    		return;
    	}
    	// non-wildcard mode
    	if (spltProperties.length > 0)
    		this.runtimeId = spltProperties[0];
    	this.hwndStr = this.runtimeId;
    	if (spltProperties.length > 1 && isChild)
    		this.parentStr = spltProperties[1];
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
