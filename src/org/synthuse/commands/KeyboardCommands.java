/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse.commands;

import org.synthuse.*;

public class KeyboardCommands extends BaseCommand {

	public KeyboardCommands(CommandProcessor commandProcessor) {
		super(commandProcessor);
	}

	public boolean cmdSendKeys(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		return whenFalseAppendError(RobotMacro.sendKeys(args[0]));
	}
	
	public boolean cmdKeyDown(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		if (!checkFirstArgumentLength(args))
			return false;
		char keyChar = args[0].charAt(0);
		return RobotMacro.keyDown(keyChar);
	}
	
	public boolean cmdKeyUp(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		if (!checkFirstArgumentLength(args))
			return false;
		char keyChar = args[0].charAt(0);
		return RobotMacro.keyUp(keyChar);
	}
	
	public boolean cmdKeyCopy(String[] args) {
		RobotMacro.copyKey();
		return true;
	}
	
	public boolean cmdKeyPaste(String[] args) {
		RobotMacro.pasteKey();
		return true;
	}
	
	public boolean cmdKeyEscape(String[] args) {
		RobotMacro.escapeKey();
		return true;
	}
	
	public boolean cmdKeyFunc(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		if (!checkFirstArgumentLength(args))
			return false;
		int fNum = Integer.parseInt(args[0]);
		RobotMacro.functionKey(fNum);
		return true;
	}

}
