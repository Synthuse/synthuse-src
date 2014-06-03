/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.synthuse.commands.*;

public class CommandProcessor implements Runnable{
	
	public static long SPEED = 1000; // ms
	public static double XML_UPDATE_THRESHOLD = 5.0; // seconds
	public static long WAIT_TIMEOUT_THRESHOLD = 30000; //ms
	public static AtomicBoolean STOP_PROCESSOR = new AtomicBoolean(false); //stop script from executing
	public static boolean DEFAULT_QUIET = false; //by default is quiet enabled
	
	protected CommandProcessor CommandProcessor = null;
	public int executeCount = 0;
	public int executeErrorCount;
	public String lastError = "";
	public String currentCommandText = "";
	public Point targetOffset = new Point();
	public StatusWindow currentStatusWin = null;
			
	public String scriptStr = "";
	public BaseCommand base = new BaseCommand(this);
	public MouseCommands mouse = new MouseCommands(this);
	public KeyboardCommands keyboard = new KeyboardCommands(this);
	public WindowsCommands win = new WindowsCommands(this);
	public MainCommands main = new MainCommands(this);

	private boolean isQuiet = DEFAULT_QUIET;
	private int scriptErrorCount = 0;
	
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
	
	public int getErrors() {
		return scriptErrorCount;
	}
	
	public void setScript(String Script) {
		scriptStr = Script;
	}
	
	public void setQuiet(boolean val) {
		isQuiet = val;
	}
	
	public CommandProcessor () {
	}

	public CommandProcessor (String scriptStr) {
		this.scriptStr = scriptStr;
	}
	
	public CommandProcessor (String scriptStr, Events customEvents) { //multithreading support
		this.scriptStr = scriptStr;
		if (customEvents != null)
			this.events = customEvents;
	}
	
	@Override
	public void run() { //multithreading support
		executeAllScriptCommands(scriptStr);
	}
	
	public static CommandProcessor executeThreaded(String scriptStr, Events customEvents) { //multithreading support
		CommandProcessor cp = new CommandProcessor(scriptStr, customEvents);
		Thread t = new Thread(cp);
        t.start();
        return cp;
	}
	
	public void executeAllScriptCommands(String scriptStr) {
		events.statusChanged("Executing Test Script...");
		//CommandProcessor cmdProcessor = new CommandProcessor();
		scriptErrorCount = 0;
		executeCount = 0;
		lastError = "";
		long startTime = System.nanoTime();
		String[] lines = scriptStr.split("\n");
		for (String line : lines) {
			
			if (!line.trim().startsWith("|")) 
				continue; //skip if it doesn't start with bar
			String[] parsed = line.trim().split("\\|");
			//
			
			//System.out.println("line: " + line);
			//System.out.println("parsed len = " + parsed.length);
			//System.out.println("parsed 2 = " + parsed[2]);
			//System.out.println("parsed 4 = " + parsed[4]);
			events.statusChanged("Executing line: " + line);
			if (parsed.length == 4 || parsed.length == 3)
				execute(parsed[2].trim(), new String[] {});
			if (parsed.length == 6 || parsed.length == 5)
				execute(parsed[2].trim(), new String[] {parsed[4].trim()});
			if (parsed.length == 7)
				execute(parsed[2].trim(), new String[] {parsed[4].trim(), parsed[6].trim()});
			if (parsed.length == 9)
				execute(parsed[2].trim(), new String[] {parsed[4].trim(), parsed[6].trim(), parsed[8].trim()});
			if (parsed.length == 11)
				execute(parsed[2].trim(), new String[] {parsed[4].trim(), parsed[6].trim(), parsed[8].trim(), parsed[10].trim()});
			
			if (executeErrorCount > 0) //check if any errors occurred
				++scriptErrorCount;

			if (STOP_PROCESSOR.get())
				break;
		}
		double seconds = ((double)(System.nanoTime() - startTime) / 1000000000);
		String forcedStr = "Completed";
		if (STOP_PROCESSOR.get())
			forcedStr = "Stopped";
		events.statusChanged("Script Execution " + forcedStr + " " + executeCount + " command(s) with " + scriptErrorCount + " error(s) in " + new DecimalFormat("#.###").format(seconds) + " seconds");
		events.executionCompleted();
		if (scriptErrorCount > 0 && !isQuiet)
		{
			JOptionPane optionPane = new JOptionPane(lastError);
			JDialog dialog = optionPane.createDialog("Errors");
			dialog.setAlwaysOnTop(SynthuseDlg.config.isAlwaysOnTop());
			dialog.setVisible(true);
			dialog.dispose();
			//JOptionPane.showMessageDialog(null, lastError);
		}
	}
	
	public Object execute(String command, String[] args) {
		++executeCount;
		executeErrorCount = 0;
		currentCommandText = command;
		String joinedArgs = "";
		for (String arg : args) 
			joinedArgs += arg + " | ";
		if (joinedArgs.endsWith("| "))
			joinedArgs = joinedArgs.substring(0, joinedArgs.length()-2);
		//StatusWindow sw = null;
		if (!isQuiet)
			currentStatusWin = new StatusWindow(command + " " + joinedArgs, -1);
		
		Object result = executeCommandSwitch(command, args);
		if (SPEED > 0) 
			try { Thread.sleep(SPEED); } catch (Exception e) {e.printStackTrace();}
		
		if (!isQuiet && currentStatusWin != null)
			currentStatusWin.dispose();
		return result;
	}
	
	private Object executeCommandSwitch(String command, String[] args) {
		try {

			//Key commands
			if (command.equals("sendKeys")) 
				return keyboard.cmdSendKeys(args);
			if (command.equals("keyDown")) 
				return keyboard.cmdKeyDown(args);
			if (command.equals("keyUp")) 
				return keyboard.cmdKeyUp(args);
			if (command.equals("keyCopy")) 
				return keyboard.cmdKeyCopy(args);
			if (command.equals("keyPaste")) 
				return keyboard.cmdKeyPaste(args);
			if (command.equals("keyEscape")) 
				return keyboard.cmdKeyEscape(args);
			if (command.equals("keyFunctionX")) 
				return keyboard.cmdKeyFunc(args);

			
			//Mouse commands
			if (command.equals("click")) 
				return mouse.cmdClick(args);
			if (command.equals("doubleClick")) 
				return mouse.cmdDoubleClick(args);
			if (command.equals("rightClick")) 
				return mouse.cmdRightClick(args);
			if (command.equals("winClick")) 
				return mouse.cmdWinClick(args);
			if (command.equals("winDoubleClick")) 
				return mouse.cmdWinDoubleClick(args);
			if (command.equals("winRightClick")) 
				return mouse.cmdWinRightClick(args);
			if (command.equals("dragAndDrop")) 
				return mouse.cmdRightClick(args);
			if (command.equals("mouseDown")) 
				return mouse.cmdMouseDown(args);
			if (command.equals("mouseUp")) 
				return mouse.cmdMouseUp(args);
			if (command.equals("mouseDownRight")) 
				return mouse.cmdMouseDownRight(args);
			if (command.equals("mouseUpRight")) 
				return mouse.cmdMouseUpRight(args);
			if (command.equals("mouseMove"))
				return mouse.cmdMouseMove(args);
			if (command.equals("mouseMoveXy"))
				return mouse.cmdMouseMoveXy(args);
			if (command.equals("setTargetOffset"))
				return mouse.cmdSetTargetOffset(args);
			
			//Windows Api Commands
			if (command.equals("windowFocus")) 
				return win.cmdWindowFocus(args);
			if (command.equals("selectMenu")) 
				return win.cmdSelectMenu(args);
			if (command.equals("selectContextMenuId")) 
				return win.cmdSelectContextMenuId(args);
			if (command.equals("sendCommandMsg")) 
				return win.cmdSendCommandMsg(args);
			if (command.equals("sendMessage")) 
				return win.cmdSendMessage(args);
			if (command.equals("windowMinimize")) 
				return win.cmdWindowMinimize(args);
			if (command.equals("windowMaximize")) 
				return win.cmdWindowMaximize(args);
			if (command.equals("windowRestore")) 
				return win.cmdWindowRestore(args);
			if (command.equals("windowShow")) 
				return win.cmdWindowShow(args);
			if (command.equals("windowHide")) 
				return win.cmdWindowHide(args);
			if (command.equals("windowSwitchToThis")) 
				return win.cmdWindowSwitchToThis(args);
			if (command.equals("windowClose")) 
				return win.cmdWindowClose(args);
			if (command.equals("setWindowText")) 
				return win.cmdSetText(args);
			if (command.equals("getWindowText")) 
				return win.cmdGetText(args);
			
			// Misc Command and Test/Sample command
			if (command.equals("delay") || command.equals("pause")) {
				if (!base.checkArgumentLength(args, 1))
					return false;
				if (!base.checkFirstArgumentLength(args))
					return false;
				if (!base.checkIsNumeric(args[0]))
					return false;
				//System.out.println("sleeping for " + args[0] );
				Thread.sleep(Long.parseLong(args[0]));
				return true;
			}
			
			if (command.equals("open")) 
				return main.cmdOpen(args);
			if (command.equals("displayText")) 
				return main.cmdDisplayText(args);
			if (command.equals("forceRefresh")) 
				return main.cmdForceRefresh(args);
			if (command.equals("setSpeed")) 
				return main.cmdSetSpeed(args);
			if (command.equals("setTimeout")) 
				return main.cmdSetTimeout(args);
			if (command.equals("setUpdateThreshold")) 
				return main.cmdSetUpdateThreshold(args);
			if (command.equals("verifyElementNotPresent")) 
				return main.cmdVerifyElementNotPresent(args);
			if (command.equals("verifyElementPresent")) 
				return main.cmdVerifyElementPresent(args);
			if (command.equals("targetRefresh")) 
				return main.cmdTargetRefresh(args);
			if (command.equals("waitForTitle")) 
				return main.cmdWaitForTitle(args);
			if (command.equals("waitForText")) 
				return main.cmdWaitForText(args);
			if (command.equals("waitForClass")) 
				return main.cmdWaitForClass(args);
			if (command.equals("waitForVisible")) 
				return main.cmdWaitForVisible(args);
			
		}
		catch (Exception e) {
			base.appendError(e);
			return false;
		}
		base.appendError("Error: Command '" + command + "' not found.");
		return false;
	}
	
}
