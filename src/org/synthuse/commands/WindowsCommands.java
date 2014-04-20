package org.synthuse.commands;

import org.synthuse.*;

import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

public class WindowsCommands extends BaseCommand {

	public WindowsCommands(CommandProcessor cp) {
		super(cp);
	}
	
	public boolean cmdWindowFocus(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.activateWindow(handle.hWnd);
		//api.showWindow(handle);
		return true;
	}
	
	public boolean cmdWindowMinimize(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.minimizeWindow(handle.hWnd);
		return true;
	}

	public boolean cmdWindowMaximize(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.maximizeWindow(handle.hWnd);
		return true;
	}

	public boolean cmdWindowRestore(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.restoreWindow(handle.hWnd);
		return true;
	}
	
	public boolean cmdWindowHide(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.hideWindow(handle.hWnd);
		return true;
	}

	public boolean cmdWindowShow(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.showWindow(handle.hWnd);
		return true;
	}

	public boolean cmdWindowSwitchToThis(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.switchToThisWindow(handle.hWnd, true);
		return true;
	}

	
	public boolean cmdWindowClose(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.closeWindow(handle.hWnd);
		return true;
	}
	
	public boolean cmdSetText(String[] args) {
		if (!checkArgumentLength(args, 2))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.sendWmSetText(handle.hWnd, args[1]);
		return true;
	}
	
	public String cmdGetText(String[] args) {
		if (!checkArgumentLength(args, 1))
			return "";
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return "";
		return api.sendWmGetText(handle.hWnd);
	}

	public boolean cmdSelectMenu(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		int id = findMenuIdWithXpath(args[0]);
		//LRESULT result = 
		//System.out.println("PostMessage to " + handle.hWndStr + " for id " + id);
		api.user32.PostMessage(handle.hWnd, Api.WM_COMMAND, new WPARAM(id), new LPARAM(0));
		return true;
	}
}
