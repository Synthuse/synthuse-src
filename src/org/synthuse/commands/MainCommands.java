package org.synthuse.commands;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import org.synthuse.*;

public class MainCommands extends BaseCommand {

	public MainCommands(CommandProcessor cp) {
		super(cp);
	}
	
	public boolean cmdOpen(String[] args) throws IOException {
		if (!checkArgumentLength(args, 1))
			return false;
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(args[0]);
		return true;
	}

	public boolean cmdDisplayText(String[] args) throws IOException {
		if (!checkArgumentLength(args, 2))
			return false;
		if (!checkIsNumeric(args[1])) //arg[1] is in milliseconds
			return false;
		this.killStatusWindow();
		//System.out.println("StatusWindow " + args[0] + ", " + Integer.parseInt(args[1]));
		StatusWindow sw = new StatusWindow(args[0], Integer.parseInt(args[1]));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		sw.setLocation(dim.width/2-sw.getSize().width/2, dim.height + StatusWindow.Y_BOTTOM_OFFSET - 80 );
		return true;
	}

	public boolean cmdSetSpeed(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long speed = Long.parseLong(args[0]);
		CommandProcessor.SPEED = speed;
		return true;
	}
	
	public boolean cmdSetTimeout(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long timeout = Long.parseLong(args[0]);
		CommandProcessor.WAIT_TIMEOUT_THRESHOLD = timeout;
		return true;
	}
	
	public boolean cmdSetUpdateThreshold(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long threshold = Long.parseLong(args[0]);
		CommandProcessor.XML_UPDATE_THRESHOLD = threshold;
		return true;
	}
	
	public boolean cmdForceRefresh(String[] args) {
		if (!checkArgumentLength(args, 0))
			return false;
		forceXmlRefresh();
		return true;
	}
	
	public boolean cmdWaitForTitle(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long totalAttempts = (long) (CommandProcessor.WAIT_TIMEOUT_THRESHOLD / (CommandProcessor.XML_UPDATE_THRESHOLD * 1000));
		long attemptCount = 0;
		String xpath = "/EnumeratedWindows/*[@TEXT='" + WindowsEnumeratedXml.escapeXmlAttributeValue(args[0].toUpperCase()) + "']";
		WinPtr handle = findHandleWithXpath(xpath, true);
		if (!handle.isEmpty())// first test without a timeout
			return true;
		while (attemptCount < totalAttempts) {
			handle = findHandleWithXpath(xpath, true);
			if (!handle.isEmpty())
				break;
			try {Thread.sleep((long)(CommandProcessor.XML_UPDATE_THRESHOLD * 1000));} catch (Exception e) {e.printStackTrace();}
			++attemptCount;
			if (isProcessorStopped())
				break;
		}
		if (handle.isEmpty())
			appendError("Error: command '" + getCurrentCommand() + "' failed to find element matching " + args[0]);
		return (!handle.isEmpty());
	}
	
	public boolean cmdWaitForText(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long totalAttempts = (long) (CommandProcessor.WAIT_TIMEOUT_THRESHOLD / (CommandProcessor.XML_UPDATE_THRESHOLD * 1000));
		long attemptCount = 0;
		String xpath = "//[@TEXT='" + WindowsEnumeratedXml.escapeXmlAttributeValue(args[0].toUpperCase()) + "']";
		WinPtr handle = findHandleWithXpath(xpath, true);
		if (!handle.isEmpty())// first test without a timeout
			return true;
		while (attemptCount < totalAttempts) {
			handle = findHandleWithXpath(xpath, true);
			if (!handle.isEmpty())
				break;
			try {Thread.sleep((long)(CommandProcessor.XML_UPDATE_THRESHOLD * 1000));} catch (Exception e) {e.printStackTrace();}
			++attemptCount;
			if (isProcessorStopped())
				break;
		}
		if (handle.isEmpty())
			appendError("Error: command '" + getCurrentCommand() + "' failed to find element matching " + args[0]);
		return (!handle.isEmpty());
	}
	
	public boolean cmdWaitForClass(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long totalAttempts = (long) (CommandProcessor.WAIT_TIMEOUT_THRESHOLD / (CommandProcessor.XML_UPDATE_THRESHOLD * 1000));
		long attemptCount = 0;
		String xpath = "//[@CLASS='" + WindowsEnumeratedXml.escapeXmlAttributeValue(args[0].toUpperCase()) + "']";
		WinPtr handle = findHandleWithXpath(xpath, true);
		if (!handle.isEmpty())// first test without a timeout
			return true;
		while (attemptCount < totalAttempts) {
			handle = findHandleWithXpath(xpath, true);
			if (!handle.isEmpty())
				break;
			try {Thread.sleep((long)(CommandProcessor.XML_UPDATE_THRESHOLD * 1000));} catch (Exception e) {e.printStackTrace();}
			++attemptCount;
			if (isProcessorStopped())
				break;
		}
		if (handle.isEmpty())
			appendError("Error: command '" + getCurrentCommand() + "' failed to find element matching " + args[0]);
		return (!handle.isEmpty());
	}
	
	public boolean cmdWaitForVisible(String[] args) {
		if (!checkArgumentLength(args, 1))
			return false;
		long totalAttempts = (long) (CommandProcessor.WAIT_TIMEOUT_THRESHOLD / (CommandProcessor.XML_UPDATE_THRESHOLD * 1000));
		long attemptCount = 0;
		WinPtr handle = findHandleWithXpath(args[0], true);
		if (!handle.isEmpty())// first test without a timeout
			return true;
		while (attemptCount < totalAttempts) {
			handle = findHandleWithXpath(args[0], true);
			if (!handle.isEmpty())
				break;
			try {Thread.sleep((long)(CommandProcessor.XML_UPDATE_THRESHOLD * 1000));} catch (Exception e) {e.printStackTrace();}
			++attemptCount;
			if (isProcessorStopped())
				break;
		}
		if (handle.isEmpty())
			appendError("Error: command '" + getCurrentCommand() + "' failed to find element matching " + args[0]);
		return (!handle.isEmpty());
	}
	
	public boolean cmdVerifyElementNotPresent(String[] args)
	{
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0], true);
		if (!handle.isEmpty())
		{
			appendError("Error: command '" + getCurrentCommand() + "' failed to NOT find element matching " + args[0]);
			return false;
		}
		else
			return true;
	}
	
	public boolean cmdVerifyElementPresent(String[] args)
	{
		if (!checkArgumentLength(args, 1))
			return false;
		WinPtr handle = findHandleWithXpath(args[0], true);
		if (!handle.isEmpty())
			return true;
		else
		{
			appendError("Error: command '" + getCurrentCommand() + "' failed to find element matching " + args[0]);
			return false;
		}
	}
	
}
