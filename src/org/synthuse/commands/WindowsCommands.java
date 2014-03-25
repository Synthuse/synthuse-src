package org.synthuse.commands;

import org.synthuse.*;

import com.sun.jna.platform.win32.WinDef.HWND;

public class WindowsCommands extends BaseCommand {

	public WindowsCommands(CommandProcessor cp) {
		super(cp);
	}
	
	public boolean cmdWindowFocus(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.activateWindow(handle);
		//api.showWindow(handle);
		return true;
	}
	
	public boolean cmdWindowMinimize(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.minimizeWindow(handle);
		return true;
	}

	public boolean cmdWindowMaximize(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.maximizeWindow(handle);
		return true;
	}

	public boolean cmdWindowRestore(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.restoreWindow(handle);
		return true;
	}
	
	public boolean cmdWindowHide(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.hideWindow(handle);
		return true;
	}

	public boolean cmdWindowShow(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.showWindow(handle);
		return true;
	}

	public boolean cmdWindowSwitchToThis(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.switchToThisWindow(handle, true);
		return true;
	}

	
	public boolean cmdWindowClose(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.closeWindow(handle);
		return true;
	}
	
	public boolean cmdSetText(String[] args) {
		if (!checkArgumentLength(args, 2))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.sendWmSetText(handle, args[1]);
		return true;
	}
	
	public String cmdGetText(String[] args) {
		if (!checkArgumentLength(args, 1))
			return "";
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return "";
		return api.sendWmGetText(handle);
	}
}
