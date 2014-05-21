package org.synthuse.commands;

import java.awt.Point;

import org.synthuse.*;

public class MouseCommands extends BaseCommand {
		
	public MouseCommands(CommandProcessor commandProcessor) {
		super(commandProcessor);
	}

	public boolean cmdClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		//System.out.println("cmdClick1: " + args[0]);
		if (handle.isEmpty())
			return false;
		String wtype = getWindowTypeWithXpath(args[0]);
		System.out.println("wtype: " + wtype + " hwnd " + handle.hWnd + " hmenu " + handle.hmenuStr + " pos " + handle.hmenuPos);
		Point p = getCenterWindowPosition(handle, wtype);
		System.out.println("cmdClick: " + p.x + "," + p.y);
		RobotMacro.mouseMove(p.x + parentProcessor.targetOffset.x, p.y + parentProcessor.targetOffset.y);
		RobotMacro.leftClickMouse();
		return true;
	}

	public boolean cmdDoubleClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		String wtype = getWindowTypeWithXpath(args[0]);
		Point p = getCenterWindowPosition(handle, wtype);
		RobotMacro.mouseMove(p.x + parentProcessor.targetOffset.x, p.y + parentProcessor.targetOffset.y);
		RobotMacro.doubleClickMouse();
		return true;
	}

	public boolean cmdRightClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		String wtype = getWindowTypeWithXpath(args[0]);
		Point p = getCenterWindowPosition(handle, wtype);
		RobotMacro.mouseMove(p.x + parentProcessor.targetOffset.x, p.y + parentProcessor.targetOffset.y);
		RobotMacro.rightClickMouse();
		return true;
	}
	
	public boolean cmdMouseDown(String[] args) {
		RobotMacro.leftMouseDown();
		return true;
	}
	
	public boolean cmdMouseUp(String[] args) {
		RobotMacro.leftMouseUp();
		return true;
	}
	
	public boolean cmdMouseDownRight(String[] args) {
		RobotMacro.rightMouseDown();
		return true;
	}
	
	public boolean cmdMouseUpRight(String[] args) {
		RobotMacro.rightMouseUp();
		return true;
	}
	
	public boolean cmdMouseMove(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		String wtype = getWindowTypeWithXpath(args[0]);
		Point p = getCenterWindowPosition(handle, wtype);
		RobotMacro.mouseMove(p.x + parentProcessor.targetOffset.x, p.y + parentProcessor.targetOffset.y);
		//System.out.println("point " + p.x + "," + p.y);
		return true;
	}

	public boolean cmdSetTargetOffset(String[] args) {
		if (!checkArgumentLength(args, 2))
			return false;
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		parentProcessor.targetOffset.x = x;
		parentProcessor.targetOffset.y = y;
		return true;
	}	
	public boolean cmdMouseMoveXy(String[] args) {
		if (!checkArgumentLength(args, 2))
			return false;
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		RobotMacro.mouseMove(x, y);
		return true;
	}
	
	public boolean cmdWinClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.sendClick(handle.hWnd);
		return true;
	}

	public boolean cmdWinDoubleClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.sendDoubleClick(handle.hWnd);
		return true;
	}

	public boolean cmdWinRightClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0]);
		if (handle.isEmpty())
			return false;
		api.sendRightClick(handle.hWnd);
		return true;
	}
}
