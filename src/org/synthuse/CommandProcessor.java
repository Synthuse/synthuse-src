/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import com.sun.jna.platform.win32.WinDef.HWND;

public class CommandProcessor implements Runnable{
	
	private static String WIN_XML = "";
	public static long LAST_UPDATED_XML = 0;
	public static long SPEED = 1000; // ms
	public static double XML_UPDATE_THRESHOLD = 5.0; // seconds
	public static long WAIT_TIMEOUT_THRESHOLD = 30000; //ms
	public static AtomicBoolean STOP_PROCESSOR = new AtomicBoolean(false);
	
	public String lastError = "";
	public String scriptStr = "";
	private Api api = new Api();
	
	private int executeErrorCount = 0;
	private String currentCommandText = "";
	
	public static interface Events {
		void statusChanged(String status);
		void executionCompleted();
	}
	public Events events = new Events() {
		public void statusChanged(String status){
			System.out.println(status);
		}
		public void executionCompleted(){
			
		}
	};
	
	public CommandProcessor () {
	}
	
	public CommandProcessor (String scriptStr, Events customEvents) { //multithreading support
		this.scriptStr = scriptStr;
		this.events = customEvents;
	}
	
	@Override
	public void run() { //multithreading support
		executeAllScriptCommands(scriptStr);
	}
	
	public static void executeThreaded(String scriptStr, Events customEvents) { //multithreading support
		Thread t = new Thread(new CommandProcessor(scriptStr, customEvents));
        t.start();
	}
	
	public void executeAllScriptCommands(String scriptStr) {
		events.statusChanged("Executing Test Script...");
		//CommandProcessor cmdProcessor = new CommandProcessor();
		int errors = 0;
		long startTime = System.nanoTime();
		String[] lines = scriptStr.split("\n");
		for (String line : lines) {
			
			if (!line.trim().startsWith("|")) 
				continue; //skip if it doesn't start with bar
			String[] parsed = line.split("\\|");
			//System.out.println("line: " + line);
			//System.out.println("parsed len = " + parsed.length);
			//System.out.println("parsed 2 = " + parsed[2]);
			//System.out.println("parsed 4 = " + parsed[4]);
			events.statusChanged("Executing line: " + line);
			if (parsed.length == 4 || parsed.length == 3)
				execute(parsed[2].trim(), new String[] {});
			if (parsed.length == 6)
				execute(parsed[2].trim(), new String[] {parsed[4].trim()});
			if (parsed.length == 7)
				execute(parsed[2].trim(), new String[] {parsed[4].trim(), parsed[6].trim()});
			
			if (executeErrorCount > 0) //check if any errors occurred
				++errors;

			if (STOP_PROCESSOR.get())
				break;
		}
		double seconds = ((double)(System.nanoTime() - startTime) / 1000000000);
		String forcedStr = "Completed";
		if (STOP_PROCESSOR.get())
			forcedStr = "Stopped";
		events.statusChanged("Script Execution " + forcedStr + " with " + errors + " error(s) in " + new DecimalFormat("#.###").format(seconds) + " seconds");
		events.executionCompleted();
		if (errors > 0)
			JOptionPane.showMessageDialog(null, lastError);
	}
	
	public Object execute(String command, String[] args) {
		executeErrorCount = 0;
		currentCommandText = command;
		String joinedArgs = "";
		for (String arg : args) 
			joinedArgs += arg + " | ";
		if (joinedArgs.endsWith("| "))
			joinedArgs = joinedArgs.substring(0, joinedArgs.length()-2);
		StatusWindow sw = new StatusWindow(command + " " + joinedArgs, -1);
		
		Object result = executeCommandSwitch(command, args);
		if (SPEED > 0) 
			try { Thread.sleep(SPEED); } catch (Exception e) {e.printStackTrace();}
		sw.dispose();
		return result;
	}
	
	private Object executeCommandSwitch(String command, String[] args) {
		try {

			//Key commands
			if (command.equals("sendKeys")) 
				return cmdSendKeys(args);
			if (command.equals("keyDown")) 
				return cmdKeyDown(args);
			if (command.equals("keyUp")) 
				return cmdKeyUp(args);
			if (command.equals("keyCopy")) 
				return cmdKeyCopy(args);
			if (command.equals("keyPaste")) 
				return cmdKeyPaste(args);
			if (command.equals("keyEscape")) 
				return cmdKeyEscape(args);
			if (command.equals("keyFunctionX")) 
				return cmdKeyFunc(args);

			
			//Mouse commands
			if (command.equals("click")) 
				return cmdClick(args);
			if (command.equals("doubleClick")) 
				return cmdDoubleClick(args);
			if (command.equals("rightClick")) 
				return cmdRightClick(args);
			if (command.equals("winClick")) 
				return cmdWinClick(args);
			if (command.equals("winDoubleClick")) 
				return cmdWinDoubleClick(args);
			if (command.equals("winRightClick")) 
				return cmdWinRightClick(args);
			if (command.equals("dragAndDrop")) 
				return cmdRightClick(args);
			if (command.equals("mouseDown")) 
				return cmdMouseDown(args);
			if (command.equals("mouseUp")) 
				return cmdMouseUp(args);
			if (command.equals("mouseDownRight")) 
				return cmdMouseDownRight(args);
			if (command.equals("mouseUpRight")) 
				return cmdMouseUpRight(args);
			if (command.equals("mouseMove")) 
				return cmdMouseMove(args);
			
			//Windows Api Commands
			if (command.equals("windowFocus")) 
				return cmdWindowFocus(args);
			if (command.equals("windowMinimize")) 
				return cmdWindowMinimize(args);
			if (command.equals("windowMaximize")) 
				return cmdWindowMaximize(args);
			if (command.equals("windowRestore")) 
				return cmdWindowRestore(args);
			if (command.equals("windowShow")) 
				return cmdWindowShow(args);
			if (command.equals("windowHide")) 
				return cmdWindowHide(args);
			if (command.equals("windowSwitchToThis")) 
				return cmdWindowSwitchToThis(args);
			if (command.equals("windowClose")) 
				return cmdWindowClose(args);
			if (command.equals("setWindowText")) 
				return cmdSetText(args);
			if (command.equals("getWindowText")) 
				return cmdGetText(args);
			
			// Misc Commands
			if (command.equals("delay") || command.equals("pause")) {
				Thread.sleep(Long.parseLong(args[0]));
				return true;
			}
			
			if (command.equals("setSpeed")) 
				return cmdSetSpeed(args);
			if (command.equals("setTimeout")) 
				return cmdSetTimeout(args);
			if (command.equals("waitForTitle")) 
				return cmdWaitForTitle(args);
			if (command.equals("waitForText")) 
				return cmdWaitForText(args);
			if (command.equals("waitForClass")) 
				return cmdWaitForClass(args);
			if (command.equals("waitForVisible")) 
				return cmdWaitForVisible(args);
			
		}
		catch (Exception e) {
			appendError(e);
			return false;
		}
		appendError("Error: Command '" + command + "' not found.");
		return false;
	}

	private void appendError(Exception e) {
		++executeErrorCount;
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		lastError += new Timestamp((new Date()).getTime()) + " - " + sw.toString() + "\n";
		try {
			sw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void appendError(String msg) {
		++executeErrorCount;
		lastError += new Timestamp((new Date()).getTime()) + " - " + msg + "\n";
	}

	private boolean checkArgumentLength(String[] args, int expectedLength) {
		if (args.length < expectedLength) {
			appendError("Error: expected at least " + expectedLength + " arguments (" + currentCommandText + "[" + args.length + "])");
			return false;
		}
		return true;
	}
	
	private boolean whenFalseAppendError(boolean cmdResult) {
		if (!cmdResult)
			appendError("Error: command '" + currentCommandText + "' failed");
		return cmdResult;
	}

	private HWND findHandleWithXpath(String xpath) {
		return findHandleWithXpath(xpath, false);
	}
	
	private HWND findHandleWithXpath(String xpath, boolean ignoreFailedFind) {
		HWND result = null;
		double secondsFromLastUpdate = ((double)(System.nanoTime() - LAST_UPDATED_XML) / 1000000000);
		if (secondsFromLastUpdate > XML_UPDATE_THRESHOLD) { //default 5 second threshold
			WIN_XML = WindowsEnumeratedXml.getXml();
			LAST_UPDATED_XML = System.nanoTime();
		}
		WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		String resultStr =  "";
		List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		for(String item: resultList) {
			if (item.contains("hwnd=")) {
				List<String> hwndList = WindowsEnumeratedXml.evaluateXpathGetValues(item, "//@hwnd");
				resultStr = hwndList.get(0);
			}
			else
				resultStr = item;
			break;
		}
		result = Api.GetHandleFromString(resultStr);
		if (result == null && !ignoreFailedFind)
			appendError("Error: Failed to find window handle matching: " + xpath);
		return result;
	}
	
	private boolean cmdSendKeys(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		return whenFalseAppendError(RobotMacro.sendKeys(args[0]));
	}
	
	private boolean cmdKeyDown(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		if (args[0].length() < 1){
			appendError("Error: command '" + currentCommandText + "' failed, expected first argument length > 0");
			return false;
		}
		char keyChar = args[0].charAt(0);
		return RobotMacro.keyDown(keyChar);
	}
	
	private boolean cmdKeyUp(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		if (args[0].length() < 1){
			appendError("Error: command '" + currentCommandText + "' failed, expected first argument length > 0");
			return false;
		}
		char keyChar = args[0].charAt(0);
		return RobotMacro.keyUp(keyChar);
	}
	
	private boolean cmdKeyCopy(String[] args) {
		RobotMacro.copyKey();
		return true;
	}
	
	private boolean cmdKeyPaste(String[] args) {
		RobotMacro.pasteKey();
		return true;
	}
	
	private boolean cmdKeyEscape(String[] args) {
		RobotMacro.escapeKey();
		return true;
	}
	
	private boolean cmdKeyFunc(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		if (args[0].length() < 1){
			appendError("Error: command '" + currentCommandText + "' failed, expected first argument length > 0");
			return false;
		}
		int fNum = Integer.parseInt(args[0]);
		RobotMacro.functionKey(fNum);
		return true;
	}
	
	private boolean cmdClick(String[] args) {
		RobotMacro.leftClickMouse();
		return true;
	}

	private boolean cmdDoubleClick(String[] args) {
		RobotMacro.doubleClickMouse();
		return true;
	}

	private boolean cmdRightClick(String[] args) {
		RobotMacro.rightClickMouse();
		return true;
	}
	
	private boolean cmdMouseDown(String[] args) {
		RobotMacro.leftMouseDown();
		return true;
	}
	
	private boolean cmdMouseUp(String[] args) {
		RobotMacro.leftMouseUp();
		return true;
	}
	
	private boolean cmdMouseDownRight(String[] args) {
		RobotMacro.rightMouseDown();
		return true;
	}
	
	private boolean cmdMouseUpRight(String[] args) {
		RobotMacro.rightMouseUp();
		return true;
	}
	
	private boolean cmdMouseMove(String[] args) {
		if (!checkArgumentLength(args, 2))
			return false;
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		RobotMacro.mouseMove(x, y);
		return true;
	}
	
	private boolean cmdWinClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.sendClick(handle);
		return true;
	}

	private boolean cmdWinDoubleClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.sendDoubleClick(handle);
		return true;
	}

	private boolean cmdWinRightClick(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.sendRightClick(handle);
		return true;
	}
	
	private boolean cmdWindowFocus(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.activateWindow(handle);
		//api.showWindow(handle);
		return true;
	}
	
	private boolean cmdWindowMinimize(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.minimizeWindow(handle);
		return true;
	}

	private boolean cmdWindowMaximize(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.maximizeWindow(handle);
		return true;
	}

	private boolean cmdWindowRestore(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.restoreWindow(handle);
		return true;
	}
	
	private boolean cmdWindowHide(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.hideWindow(handle);
		return true;
	}

	private boolean cmdWindowShow(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.showWindow(handle);
		return true;
	}

	private boolean cmdWindowSwitchToThis(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.switchToThisWindow(handle, true);
		return true;
	}

	
	private boolean cmdWindowClose(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.closeWindow(handle);
		return true;
	}
	
	private boolean cmdSetText(String[] args) {
		if (!checkArgumentLength(args, 2))
			return false;
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return false;
		api.sendWmSetText(handle, args[1]);
		return true;
	}
	
	private String cmdGetText(String[] args) {
		if (!checkArgumentLength(args, 1))
			return "";
		HWND handle = findHandleWithXpath(args[0]);
		if (handle == null)
			return "";
		return api.sendWmGetText(handle);
	}
	
	private boolean cmdSetSpeed(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long speed = Long.parseLong(args[0]);
		SPEED = speed;
		return true;
	}
	
	private boolean cmdSetTimeout(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long timeout = Long.parseLong(args[0]);
		WAIT_TIMEOUT_THRESHOLD = timeout;
		return true;
	}
	
	private boolean cmdWaitForTitle(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long totalAttempts = (long) (WAIT_TIMEOUT_THRESHOLD / (XML_UPDATE_THRESHOLD * 1000));
		long attemptCount = 0;
		String xpath = "/EnumeratedWindows/win[@TEXT='" + WindowsEnumeratedXml.escapeXmlAttributeValue(args[0].toUpperCase()) + "']";
		HWND handle = findHandleWithXpath(xpath, true);
		if (handle != null)// first test without a timeout
			return true;
		while (attemptCount < totalAttempts) {
			handle = findHandleWithXpath(xpath, true);
			if (handle != null)
				break;
			try {Thread.sleep((long)(XML_UPDATE_THRESHOLD * 1000));} catch (Exception e) {e.printStackTrace();}
			++attemptCount;
			if (STOP_PROCESSOR.get())
				break;
		}
		return handle != null;
	}
	
	private boolean cmdWaitForText(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long totalAttempts = (long) (WAIT_TIMEOUT_THRESHOLD / (XML_UPDATE_THRESHOLD * 1000));
		long attemptCount = 0;
		String xpath = "//[@TEXT='" + WindowsEnumeratedXml.escapeXmlAttributeValue(args[0].toUpperCase()) + "']";
		HWND handle = findHandleWithXpath(xpath, true);
		if (handle != null)// first test without a timeout
			return true;
		while (attemptCount < totalAttempts) {
			handle = findHandleWithXpath(xpath, true);
			if (handle != null)
				break;
			try {Thread.sleep((long)(XML_UPDATE_THRESHOLD * 1000));} catch (Exception e) {e.printStackTrace();}
			++attemptCount;
			if (STOP_PROCESSOR.get())
				break;
		}
		return handle != null;
	}
	
	private boolean cmdWaitForClass(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long totalAttempts = (long) (WAIT_TIMEOUT_THRESHOLD / (XML_UPDATE_THRESHOLD * 1000));
		long attemptCount = 0;
		String xpath = "//win[@CLASS='" + WindowsEnumeratedXml.escapeXmlAttributeValue(args[0].toUpperCase()) + "']";
		HWND handle = findHandleWithXpath(xpath, true);
		if (handle != null)// first test without a timeout
			return true;
		while (attemptCount < totalAttempts) {
			handle = findHandleWithXpath(xpath, true);
			if (handle != null)
				break;
			try {Thread.sleep((long)(XML_UPDATE_THRESHOLD * 1000));} catch (Exception e) {e.printStackTrace();}
			++attemptCount;
			if (STOP_PROCESSOR.get())
				break;
		}
		return handle != null;
	}
	
	private boolean cmdWaitForVisible(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long totalAttempts = (long) (WAIT_TIMEOUT_THRESHOLD / (XML_UPDATE_THRESHOLD * 1000));
		long attemptCount = 0;
		HWND handle = findHandleWithXpath(args[0], true);
		if (handle != null)// first test without a timeout
			return true;
		while (attemptCount < totalAttempts) {
			handle = findHandleWithXpath(args[0], true);
			if (handle != null)
				break;
			try {Thread.sleep((long)(XML_UPDATE_THRESHOLD * 1000));} catch (Exception e) {e.printStackTrace();}
			++attemptCount;
			if (STOP_PROCESSOR.get())
				break;
		}
		return handle != null;
	}
	
	
}
