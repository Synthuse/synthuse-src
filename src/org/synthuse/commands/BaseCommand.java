package org.synthuse.commands;


import java.awt.Point;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.synthuse.*;

public class BaseCommand {
	
	static String WIN_XML = "";
	static long LAST_UPDATED_XML = 0;
	
	protected Api api = new Api();
	protected WpfBridge wpf = new WpfBridge();
	protected CommandProcessor parentProcessor = null;
	
	protected int getExecuteErrorCount() {
		return parentProcessor.executeErrorCount;
	}
	
	protected void setExecuteErrorCount(int val) {
		parentProcessor.executeErrorCount = val;
	}

	protected String getCurrentCommand() {
		return parentProcessor.currentCommandText;
	}
	
	protected void setCurrentCommand(String val) {
		parentProcessor.currentCommandText = val;
	}
	
	protected boolean isProcessorStopped() {
		return CommandProcessor.STOP_PROCESSOR.get();
	}
	
	public BaseCommand(CommandProcessor cp) { // should pass in the parent command processor
		parentProcessor = cp;
	}

	public void appendError(Exception e) {
		setExecuteErrorCount(getExecuteErrorCount() + 1);
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		parentProcessor.lastError += new Timestamp((new Date()).getTime()) + " - " + sw.toString() + "\n";
		try {
			sw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void appendError(String msg) {
		setExecuteErrorCount(getExecuteErrorCount() + 1);
		parentProcessor.lastError += new Timestamp((new Date()).getTime()) + " - " + msg + "\n";
	}

	public boolean checkArgumentLength(String[] args, int expectedLength) {
		if (args.length < expectedLength) {
			appendError("Error: expected at least " + expectedLength + " arguments (" + getCurrentCommand() + "[" + args.length + "])");
			return false;
		}
		return true;
	}

	public boolean checkFirstArgumentLength(String[] args) {
		if (args[0].length() <= 0) {
			appendError("Error: command '" + getCurrentCommand() + "' failed, expected first argument length > 0");
			return false;
		}
		return true;
	}
	
	public boolean checkIsNumeric(String val) {
		try { 
			Long.parseLong(val); 
		} catch(NumberFormatException e) { 
			appendError("Error: command '" + getCurrentCommand() + "' failed, was expecting a numeric value instead '" + val + "'");
	    	return false; 
		}
		return true;
	}
	
	public boolean whenFalseAppendError(boolean cmdResult) {
		if (!cmdResult)
			appendError("Error: command '" + getCurrentCommand() + "' failed");
		return cmdResult;
	}

	public WinPtr findHandleWithXpath(String xpath) {
		return findHandleWithXpath(xpath, false);
	}
	
	public WinPtr findHandleWithXpath(String xpath, boolean ignoreFailedFind) {
		WinPtr result = new WinPtr();
		double secondsFromLastUpdate = ((double)(System.nanoTime() - LAST_UPDATED_XML) / 1000000000);
		if (secondsFromLastUpdate > CommandProcessor.XML_UPDATE_THRESHOLD) { //default 5 second threshold
			WIN_XML = WindowsEnumeratedXml.getXml();
			LAST_UPDATED_XML = System.nanoTime();
		}
		WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		String resultStr =  "";
		List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		for(String item: resultList) {
			if (item.contains("hwnd=")) {
				List<String> hwndList = WindowsEnumeratedXml.evaluateXpathGetValues(item, "//@hwnd");
				if (hwndList.size() > 0)
					resultStr = hwndList.get(0); //get first hwnd;
			}
			else
				resultStr = item;
			break;
		}
		if (WinPtr.isWpfRuntimeIdFormat(resultStr))
			result.runtimeId = resultStr;
		else {
			result.hWnd = Api.GetHandleFromString(resultStr);
			if (!api.user32.IsWindow(result.hWnd))
				appendError("Error: Failed to located HWND(" + resultStr + ") from : " + xpath);
		}
		if (result.isEmpty())
			appendError("Error: Failed to find window handle matching: " + xpath);
		return result;
	}
	
	public Point getCenterWindowPosition(WinPtr handle) {
		Point p = null;
		if (handle.isWin32())
			p = api.getWindowPosition(handle.hWnd);
		else
			p = wpf.getCenterOfElement(handle.runtimeId);
		return p;
	}
	
	public String convertListToString(List<String> listStr, String delimiter) {
		StringBuilder result = new StringBuilder("");
		for (String item: listStr) {
			result.append(item + delimiter);
		}
		return result.toString();
	}
	
	public void killStatusWindow() {
		parentProcessor.currentStatusWin.dispose();
		parentProcessor.currentStatusWin = null;
	}
	
}