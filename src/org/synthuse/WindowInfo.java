/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.Point;
import java.awt.Rectangle;
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
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.PointerByReference;

public class WindowInfo {
	
	public static String UIA_PROPERTY_LIST = "RuntimeIdProperty,ParentRuntimeIdProperty,ProcessIdProperty,FrameworkIdProperty,LocalizedControlTypeProperty,ClassNameProperty,NameProperty,ValueProperty";
	public static String UIA_PROPERTY_LIST_ADV = "RuntimeIdProperty,ParentRuntimeIdProperty,ProcessIdProperty,FrameworkIdProperty,LocalizedControlTypeProperty,ClassNameProperty,NameProperty,ValueProperty,BoundingRectangleProperty";
	public static String UIA_RUNTIME_ID = "RuntimeIdProperty";
	public static String UIA_BOUNDING_RECT = "BoundingRectangleProperty";
	public static int MAX_TEXT_SIZE = 200;
	
	public HWND hwnd;
	public String hwndStr = "";
	public HWND parent = null;
	public String parentStr = "";
	public RECT rect;
	public String text;
	public String value;
	public String controlType = "";
	public String className = "";
	public boolean isChild = false;
	public String processName = "";
	public long pid = 0;
	public Object xmlObj = null;
	public String framework = "win32";//default as win32
	public String runtimeId = "";
	public int menus = 0;
	public HMENU menu = null;
	public boolean useUiaBridge = false;
	public boolean is64bit = false;
	
	public Map<String, String> extra = null;
    
	//Default Win32 support
    public WindowInfo(HWND hWnd, boolean isChild) {
    	this.framework = "win32";
        byte[] buffer = new byte[1024];
        User32Ex.instance.GetWindowTextA(hWnd, buffer, buffer.length);
        text = Native.toString(buffer);
        if (text.isEmpty())
        	text = new Api().sendWmGetText(hWnd);
        //if (text.isEmpty()) {
        	//System.out.println("getting toolbar text");
        //}
        if (text.length() > MAX_TEXT_SIZE) //if text is too large it will slow down xml display
        	text = text.substring(0, MAX_TEXT_SIZE);
        
        //Get item count depending on what type of control it is
    	LRESULT tbCount = Api.User32Ex.instance.SendMessage(hWnd, Api.TB_BUTTONCOUNT, new WPARAM(0), new LPARAM(0));
    	if (tbCount.intValue() > 0) { // toolbar button count
    		//System.out.println("TB_BUTTONCOUNT: " + tbCount.intValue());
    		if (extra == null)
    			extra = new LinkedHashMap<String, String>(); 
    		extra.put("tbCount", tbCount.intValue() + "");
    		//Api.User32.instance.SendMessageA(hWnd, Api.TB_GETTOOLTIPS, 0, buffer);
    		//text = Native.toString(buffer);
    	}
    	LRESULT lvCount = Api.User32Ex.instance.SendMessage(hWnd, Api.LVM_GETITEMCOUNT, new WPARAM(0), new LPARAM(0));
    	if (lvCount.intValue() > 0) { // listview item count
    		if (extra == null)
    			extra = new LinkedHashMap<String, String>(); 
    		extra.put("lvCount", lvCount.intValue() + "");
    	}
    	LRESULT lbCount = Api.User32Ex.instance.SendMessage(hWnd, Api.LB_GETCOUNT, new WPARAM(0), new LPARAM(0));
    	if (lbCount.intValue() > 0) { // listbox item count
    		if (extra == null)
    			extra = new LinkedHashMap<String, String>(); 
    		extra.put("lbCount", lbCount.intValue() + "");
    	}
    	LRESULT cbCount = Api.User32Ex.instance.SendMessage(hWnd, Api.CB_GETCOUNT, new WPARAM(0), new LPARAM(0));
    	if (cbCount.intValue() > 0) { // listbox item count
    		if (extra == null)
    			extra = new LinkedHashMap<String, String>(); 
    		extra.put("cbCount", cbCount.intValue() + "");
    	}
    	LRESULT tvCount = Api.User32Ex.instance.SendMessage(hWnd, Api.TVM_GETCOUNT, new WPARAM(0), new LPARAM(0));
		if (tvCount.intValue() > 0) { //treeview node count
			if (extra == null)
				extra = new LinkedHashMap<String, String>(); 
			extra.put("tvCount", tvCount.intValue() + "");
		}
		
        char[] buffer2 = new char[1026];
		User32Ex.instance.GetClassName(hWnd, buffer2, 1026);
		className = Native.toString(buffer2);
		
		//check if window has a menu
    	HMENU hmenu = Api.User32Ex.instance.GetMenu(hWnd);
		if (hmenu != null) { //menu item count
	    	int menuCount = Api.User32Ex.instance.GetMenuItemCount(hmenu);
	    	if (menuCount > 0) {
				this.menus = menuCount;
				this.menu = hmenu;
	    	}
		}
		else // if (className.equals("#32768")) //check if its a popup menu window
		{
    		//LRESULT result = Api.User32.instance.PostMessage(hWnd, Api.MN_GETHMENU, new WPARAM(0), new LPARAM(0));
			LRESULT result = Api.User32Ex.instance.SendMessage(hWnd, Api.MN_GETHMENU, new WPARAM(0), new LPARAM(0));
    		if (result.longValue() != 1)
    		{
    			//System.out.println("MN_GETHMENU: " + result.longValue());
    			hmenu = new HMENU(new Pointer(result.longValue()));
    			int menuCount = Api.User32Ex.instance.GetMenuItemCount(hmenu);
    			if (menuCount > 0)
    			{
        			//System.out.println("Popup Win: " + menuCount);
    				this.menus = menuCount;
    				this.menu = hmenu;
    			}
    		}
		}

		
		rect = new RECT();
		User32Ex.instance.GetWindowRect(hWnd, rect);
		
		this.isChild = isChild;
		if (isChild) {
			parent = User32Ex.instance.GetParent(hWnd);
			parentStr = Api.GetHandleAsString(parent);
			// test to see if uiaBridge should be used on this child
			if (this.className.startsWith("HwndWrapper") || this.className.startsWith("MicrosoftSilverlight") || this.className.startsWith("GeckoPluginWindow"))
				useUiaBridge = true;
		}
		else {
			PointerByReference pointer = new PointerByReference();
			User32Ex.instance.GetWindowThreadProcessId(hWnd, pointer);
			pid = pointer.getPointer().getInt(0);
			getProcessInfo();
		    //test to see if uiaBridge should be used on this parent
			if (this.className.startsWith("HwndWrapper") || this.className.startsWith("WindowsForms"))
				useUiaBridge = true;
		}
		this.hwnd = hWnd;
		hwndStr = Api.GetHandleAsString(hWnd);
    	if (this.hwndStr == null)
    		this.hwndStr = "";
    }
    
    //support for WPF, Silverlight, WinForms
    public WindowInfo(String enumProperties, boolean isChild) {
    	//WPF_PROPERTY_LIST = "RuntimeIdProperty,ParentRuntimeIdProperty,ProcessIdProperty,FrameworkIdProperty,LocalizedControlTypeProperty,ClassNameProperty,NameProperty,ValueProperty";
    	String[] spltProperties = enumProperties.split(",");
    	this.isChild = isChild;
    	this.useUiaBridge = true;
    	if (SynthuseDlg.config.isFilterUiaDisabled()) { //use wildcard mode
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
    			else if (propertyNameAndValue[0].equals("LocalizedControlTypeProperty"))
    				this.controlType = propertyNameAndValue[1];
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
        	if (text != null)
        		if (text.length() > MAX_TEXT_SIZE)
        			text = text.substring(0, MAX_TEXT_SIZE);
        	if (this.hwndStr == null)
        		this.hwndStr = "";
        	//if (this.framework == null)
        	//	this.framework = "na";
        	if(this.controlType.equals("window"))
        		this.isChild = false;
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
    		this.controlType = UiaBridge.replaceEscapedCodes(spltProperties[4]);
    	if (spltProperties.length > 5)
    		this.className = UiaBridge.replaceEscapedCodes(spltProperties[5]);
    	if (spltProperties.length > 6)
    		this.text = UiaBridge.replaceEscapedCodes(spltProperties[6]);
    	if (spltProperties.length > 7)
    		this.value = UiaBridge.replaceEscapedCodes(spltProperties[7]);
    	if (this.className == "")
    		this.className = this.controlType;
    	if (text != null)
    		if (text.length() > MAX_TEXT_SIZE)
    			text = text.substring(0, MAX_TEXT_SIZE);
    	if (value != null)
    		if (value.length() > MAX_TEXT_SIZE)
    			value = value.substring(0, MAX_TEXT_SIZE);
    	if (this.hwndStr == null)
    		this.hwndStr = "";
    	getProcessInfo();
    	if(this.controlType.equals("window"))
    		this.isChild = false;
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
    
    private void getProcessInfo()
    {
    	if (pid == 0)
    		return;
        char[] buffer = new char[1026];
	    Pointer process = Kernel32Ex.instance.OpenProcess(Api.PROCESS_QUERY_INFORMATION | Api.PROCESS_VM_READ, false, new Pointer(pid));
	    PsapiEx.instance.GetModuleBaseNameW(process, null, buffer, 512);
	    processName = Native.toString(buffer);
	    Kernel32Ex.instance.CloseHandle(new HANDLE(process));
		is64bit = Api.isProcess64bit((int)pid);
    }
    
    
    public static String getRuntimeIdFromProperties(String enumProperties)
    {
    	String[] spltProperties = enumProperties.split(",");
    	if (spltProperties.length > 0)
    		return spltProperties[0];
    	return "";
    }
    
    public static String getFrameworkFromProperties(String enumProperties)
    {
    	String[] spltProperties = enumProperties.split(",");
    	if (spltProperties.length > 3)
    		return spltProperties[3];
    	return "";
    }
    
    public static Point findOffset(Rectangle rect, int xPos, int yPos)
    {
    	Point offset = new Point();
    	int x = ((rect.width) /2) + rect.x;
    	int y = ((rect.height) /2) + rect.y;
    	
    	offset.x = xPos - x;
    	offset.y = yPos - y;
    	
    	return offset;
    }
    
    public String toString() {
        return String.format("%s \"%s\" [%s] (%s) {%s}", framework, text, className, controlType, hwndStr);
    }

}
