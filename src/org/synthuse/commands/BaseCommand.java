/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse.commands;


import java.awt.Point;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.synthuse.*;

import com.sun.jna.platform.win32.WinDef.HWND;

public class BaseCommand {
	
	static String WIN_XML = "";
	static long LAST_UPDATED_XML = 0;
	
	protected Api api = new Api();
	protected UiaBridge uiabridge = new UiaBridge();
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
	
	public void forceXmlRefresh() {
		WIN_XML = WindowsEnumeratedXml.getXml();
		LAST_UPDATED_XML = System.nanoTime();
	}
	
	public void forceWin32OnlyXmlRefresh() {
		WIN_XML = WindowsEnumeratedXml.getWin32Xml();
		LAST_UPDATED_XML = System.nanoTime();
	}
	
	public void onlyRefreshWin32(boolean flg) {
		SynthuseDlg.config.disableUiaBridge = flg + "";
	}

	public void targetXmlRefresh(String xpath) {
		if (WIN_XML.isEmpty()) //can't target refresh unless there is XML to start with
		{
			forceXmlRefresh();
			return;
		}
		//WIN_XML = WindowsEnumeratedXml.getXml();
		LAST_UPDATED_XML = System.nanoTime();
		
		String resultStr =  "";
		String resultHwndStr =  "";
		List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		for(String item: resultList) {
			//System.out.println("xpath result item: " + item);
			resultStr = item;
			if (item.contains("hwnd=")) {
				List<String> hwndList = WindowsEnumeratedXml.evaluateXpathGetValues(item, "//@hwnd");
				if (hwndList.size() > 0)
					resultHwndStr = hwndList.get(0).replaceAll("[^\\d-.]", ""); //get first hwnd;
			}
			else
				resultStr = item;
			break;
		}
		String newXml = "";
		Map<String, WindowInfo> infoList;
		if (resultHwndStr.contains("-")) { //uiabridge target refresh
			resultHwndStr = resultHwndStr.split("-")[1];
			infoList = WindowsEnumeratedXml.EnumerateWindowsWithUiaBridge(uiabridge, resultHwndStr, "*");
			newXml = WindowsEnumeratedXml.generateWindowsXml(infoList, "updates");
			//System.out.println("newXml: " + newXml);
		}
		else
		{ // native target refresh
			infoList = new LinkedHashMap<String, WindowInfo>();
			HWND parentHwnd = Api.GetHandleFromString(resultHwndStr);
			WindowInfo wi = new WindowInfo(parentHwnd, false);
			infoList.put(wi.hwndStr, wi);
			infoList.putAll(WindowsEnumeratedXml.EnumerateWin32ChildWindows(parentHwnd));
			//WindowsEnumeratedXml.appendUiaBridgeWindows(infoList); //if we need this we should specify a runtimeID handle instead
			newXml = WindowsEnumeratedXml.generateWindowsXml(infoList, "updates");
			System.out.println("newNativeXml: " + newXml);
		}
		
		int pos = WIN_XML.indexOf(resultStr);
		WIN_XML = WIN_XML.substring(0, pos) + newXml + WIN_XML.substring(pos + resultStr.length());
		
	}
	
	public String getWindowTypeWithXpath(String xpath) {
		String result = "";
		double secondsFromLastUpdate = ((double)(System.nanoTime() - LAST_UPDATED_XML) / 1000000000);
		if (secondsFromLastUpdate > CommandProcessor.XML_UPDATE_THRESHOLD) { //default 5 second threshold
			WIN_XML = WindowsEnumeratedXml.getXml();
			LAST_UPDATED_XML = System.nanoTime();
		}
		String resultStr =  "";
		List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		if (resultList.size() > 0)
		{
			resultStr = resultList.get(0).trim();
			if (resultStr.startsWith("<winfrm "))
				result = "winfrm";
			else if(resultStr.startsWith("<win "))
				result = "win";
			else if(resultStr.startsWith("<wpf "))
				result = "wpf";
			else if(resultStr.startsWith("<silver "))
				result = "silver";
			else if(resultStr.startsWith("<menu "))
				result = "menu";
			else
				result = "other";
		}
		return result;
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
		//WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		String resultStr =  "";
		List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		for(String item: resultList) {
			//System.out.println("xpath result item: " + item);
			if (item.contains("hwnd=")) {
				List<String> hwndList = WindowsEnumeratedXml.evaluateXpathGetValues(item, "//@hwnd");
				if (hwndList.size() > 0)
					resultStr = hwndList.get(0); //get first hwnd;
			}
			else
				resultStr = item;
			if (item.contains("hmenu=")) { //get menu information, useful for getting center of menu
				List<String> hmenuList = WindowsEnumeratedXml.evaluateXpathGetValues(item, "//@hmenu");
				if (hmenuList.size() > 0)
					result.hmenuStr = hmenuList.get(0).replaceAll("[^\\d-.]", ""); //get first hmenu;
				if (item.contains("id=")) {
					List<String> menuidList = WindowsEnumeratedXml.evaluateXpathGetValues(item, "//@position");
					if (menuidList.size() > 0)
						result.hmenuPos = Integer.parseInt(menuidList.get(0).replaceAll("[^\\d-.]", "")); //get first id;
				}
			}
			break;// we only care about the first item
		}
		
		resultStr = resultStr.replaceAll("[^\\d-.]", ""); //remove all non-numeric values (except dash -)
		if (WinPtr.isWpfRuntimeIdFormat(resultStr)) {
			result.runtimeId = resultStr;
			if (!ignoreFailedFind && result.isEmpty())
				appendError("Error: Failed to find window handle matching: " + xpath);
		}
		else {
			result.hWnd = Api.GetHandleFromString(resultStr);
			if (!ignoreFailedFind && !api.user32.IsWindow(result.hWnd))
				appendError("Error: Failed to locate window HWND(" + resultStr + ") from : " + xpath);
		}
		return result;
	}
	
	public int findMenuIdWithXpath(String xpath) {
		int result = 0;
		double secondsFromLastUpdate = ((double)(System.nanoTime() - LAST_UPDATED_XML) / 1000000000);
		if (secondsFromLastUpdate > CommandProcessor.XML_UPDATE_THRESHOLD) { //default 5 second threshold
			WIN_XML = WindowsEnumeratedXml.getXml();
			LAST_UPDATED_XML = System.nanoTime();
		}
		//WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		String resultStr =  "";
		List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(WIN_XML, xpath);
		for(String item: resultList) {
			if (item.contains("hmenu=")) {
				List<String> list = WindowsEnumeratedXml.evaluateXpathGetValues(item, "//@id");
				if (list.size() > 0)
					resultStr = list.get(0); //get first id
			}
			else
				resultStr = item;
			break;
		}
		resultStr = resultStr.replaceAll("[^\\d.]", ""); //remove all non-numeric values
		//System.out.println("findMenuIdWithXpath: " + resultStr);
		if (resultStr.isEmpty())
			appendError("Error: Failed to find window handle matching: " + xpath);
		else
			result = Integer.parseInt(resultStr);
		return result;
	}

	
	public Point getCenterWindowPosition(WinPtr handle) {
		Point p = null;
		if (handle.isWin32())
			p = api.getWindowPosition(handle.hWnd);
		else
			p = uiabridge.getCenterOfElement(handle.runtimeId);
		return p;
	}
	
	public Point getCenterWindowPosition(WinPtr handle, String windowType) {
		Point p = null;

		if (handle.isWpf() || windowType.equals("winfrm") || windowType.equals("wpf") || windowType.equals("silver"))
			p = uiabridge.getCenterOfElement(handle.runtimeId);
		else if (windowType.equals("win"))
			p = api.getWindowPosition(handle.hWnd);
		else if (windowType.equals("menu"))
			p = api.getMenuItemPosition(handle.hWnd, MenuInfo.GetHandleMenuFromString(handle.hmenuStr), handle.hmenuPos);
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
