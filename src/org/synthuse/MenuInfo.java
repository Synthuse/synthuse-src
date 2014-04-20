package org.synthuse;

import java.math.BigInteger;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HMENU;

public class MenuInfo {
	public HMENU hmenu = null;
	public String hmenuStr = "";
	public String hwndStr = "";
	public int menuCount = 0;
	public String text = "";
	public String unaltered = "";
	public int id = 0;
	public int position = 0;
	public boolean hasSubMenu = false;
	public HMENU submenu = null;
	public String submenuStr = "";
	public int submenuCount = 0;
	
	public MenuInfo(HMENU hmenu) {
		loadMenuBase(hmenu);
	}
	
	public MenuInfo(HMENU hmenu, int position) {
		loadMenuBase(hmenu);
		if (this.menuCount > 0)
			loadMenuDetails(hmenu, position);
	}

	public void loadMenuBase(HMENU hmenu) {
		Api api = new Api();
		this.hmenu = hmenu;
		this.hmenuStr = GetHandleMenuAsString(hmenu);
		this.menuCount = api.user32.GetMenuItemCount(hmenu);
	}

	public void loadMenuDetails(HMENU hmenu, int position) {
		Api api = new Api();
		this.position = position;
		this.unaltered = api.GetMenuItemText(hmenu, position);
		this.text = unaltered;
		if (this.unaltered.contains("\t"))
			this.text = this.text.substring(0, this.text.indexOf("\t"));
		this.text = text.replaceAll("[^a-zA-Z0-9.,\\+ ]", "");
		this.id = api.user32.GetMenuItemID(hmenu, position);
		HMENU submenu = api.user32.GetSubMenu(hmenu, position);
		if (submenu != null) {
			int subCount = api.user32.GetMenuItemCount(submenu);
			if (subCount > 0) {
				this.hasSubMenu = true;
				this.submenu = submenu;
				this.submenuStr = GetHandleMenuAsString(submenu);
				this.submenuCount = subCount;
			}				
		}
		
	}

    public static String GetHandleMenuAsString(HMENU hmenu) {
    	if (hmenu == null)
    		return "0";
    	//String longHexStr = hWnd.toString().substring("native@".length());
    	//String longHexStr = hmenu.getPointer()
    	String longHexStr = hmenu.getPointer().toString().substring("native@0x".length());
    	long l = new BigInteger(longHexStr, 16).longValue();
    	return l + "";
    }

    public static HMENU GetHandleMenuFromString(String hmenu) {
    	if (hmenu == null)
    		return null;
    	if (hmenu.isEmpty())
    		return null;
    	String cleanNumericHandle = hmenu.replaceAll("[^\\d.]", "");
    	try {
    		return (new HMENU(new Pointer(Long.parseLong(cleanNumericHandle))));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
	
}
