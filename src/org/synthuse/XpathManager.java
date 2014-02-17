/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.synthuse.Api.User32;

import com.sun.jna.platform.win32.WinDef.HWND;

public class XpathManager implements Runnable{

	private HWND hwnd = null;
	private JTextPane windowsXmlTextPane = null;
	
	public static interface Events {
		void statusChanged(String status);
		void executionCompleted(Object input, String results);
	}
	public Events events = new Events() {
		public void statusChanged(String status){
			System.out.println(status);
		}
		public void executionCompleted(Object input, String results){
			
		}
	};
	
	public XpathManager(HWND hwnd, JTextPane windowsXmlTextPane, Events events) {
		this.events = events;
		this.hwnd = hwnd;
		this.windowsXmlTextPane = windowsXmlTextPane;
	}
	
	@Override
	public void run() {
		String results = buildXpathStatement();
		events.executionCompleted(hwnd, results);
	}
	
	public static void buildXpathStatementThreaded(HWND hwnd, JTextPane windowsXmlTextPane, Events events) {
		Thread t = new Thread(new XpathManager(hwnd, windowsXmlTextPane, events));
        t.start();
	}
	
	public String buildXpathStatement() {
		String builtXpath = "";
		try {
			String classStr = WindowsEnumeratedXml.escapeXmlAttributeValue(Api.GetWindowClassName(hwnd));
			String handleStr = Api.GetHandleAsString(hwnd);
			String txtOrig = Api.GetWindowText(hwnd);
			String txtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(txtOrig);
			String xml = this.windowsXmlTextPane.getText();
			builtXpath = "//win[@class='" + classStr + "']";
			List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(xml, builtXpath);
			//int matches = nextXpathMatch(builtXpath, textPane, true);
			if (resultList.size() > 1) { // if there are multiple results with the simple xpath then include parent class and text with the xpath statement.
				HWND parent = User32.instance.GetParent(hwnd);
				String parentClassStr = WindowsEnumeratedXml.escapeXmlAttributeValue(Api.GetWindowClassName(parent));
				String parentTxtOrig = Api.GetWindowText(parent);
				String parentTxtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(parentTxtOrig);
				if (!parentTxtStr.isEmpty()) {
					if (parentTxtOrig.length() > 20) {// if the parent text is too long only test the first 20 characters
						parentTxtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(parentTxtOrig.substring(0, 20));
						parentTxtStr = " and starts-with(@text,'" + parentTxtStr + "')";
					}
					else
						parentTxtStr = " and @text='" + parentTxtStr + "'";
				}
				if (!parentClassStr.isEmpty())
					builtXpath = "//win[@class='" + parentClassStr + "'" + parentTxtStr + "]/win[@class='" + classStr + "']";
				resultList = WindowsEnumeratedXml.evaluateXpathGetValues(xml, builtXpath);
				if (resultList.size() > 1) { // if there are still multiple results add position to the xpath
					int position = 1;
					for (String result : resultList) {
						if (result.contains(handleStr)) {
							builtXpath += "[" + position + "]";
							break;
						}
						++position;
					}
				}
				if (resultList.size() == 0) { //some reason a window might have a parent window that is not associated with it's child (orphans!!)
					if (!txtStr.isEmpty()) {
						if (parentTxtOrig.length() > 30) {// if the text is too long only test the first 20 characters
							txtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(txtStr.substring(0, 30));
							txtStr = " and starts-with(@text,'" + txtStr + "')";
						}
						else
							txtStr = " and @text='" + txtStr + "'";
					}
					builtXpath = "//win[@class='" + classStr + "'" + txtStr + "]";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return builtXpath;
	}
	
	public static int nextXpathMatch(String xpathExpr, JTextPane targetText, JLabel lblStatus, boolean alwaysFromTop) {
		int results = 0;
		try {
			if (xpathExpr.length() == 0)
				return results;
			
			if (targetText instanceof JTextPane) {
				final JTextPane target = (JTextPane)targetText;
				target.requestFocus();
				int cPos = target.getCaretPosition();
				if (alwaysFromTop)
					cPos = 0;
				int len = target.getStyledDocument().getLength();
				String xml = target.getText(0, len);
				WindowsEnumeratedXml.lastException = null;
				List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(xml, xpathExpr);
				if (resultList.size() == 0 && WindowsEnumeratedXml.lastException != null) {
					String errMsg = WindowsEnumeratedXml.lastException.getCause().getMessage();
					JOptionPane.showMessageDialog(null, "Exception: " + errMsg, "Error", JOptionPane.ERROR_MESSAGE);
					return -1;
				}
				results = resultList.size();
				String txt = "";
				String targetStr = target.getText(cPos, (len - cPos));
				int matches = 0;
				int mPos = 0;
				target.select(cPos, cPos); //clear selection
				for (int i = 0; i < resultList.size(); i++) {
					txt = resultList.get(i).trim();
					if (txt.length() == 0)
						continue;
					//if (txt.endsWith("\r\n"))
					//	txt = txt.substring(0, txt.length() - 2).trim();
					txt = txt.replaceAll("\r\n", "\n").trim();
					while ((mPos = targetStr.indexOf(txt)) != -1) {
						if (matches == 0){
							if (!alwaysFromTop) 
								flashMatchingWindow(txt);
							//target.setCaretPosition(cPos + mPos);
							//target.select(cPos + mPos, cPos + mPos + txt.length());
							XmlEditorKit.HIGHLIGHTED_START = cPos + mPos;
							XmlEditorKit.HIGHLIGHTED_END = cPos + mPos + txt.length();							
							//System.out.println("HIGHLIGHTED_START = " + (cPos + mPos));
							//System.out.println("HIGHLIGHTED_END = " + (cPos + mPos + txt.length()));
							final int cpos = cPos + mPos +2;
							EventQueue.invokeLater(new Runnable() {
								@Override
								public void run() {
									target.updateUI();
									target.setCaretPosition(cpos);
								}
							});
						}
						targetStr = targetStr.substring(mPos + txt.length());
						++matches;
					}
				}
				lblStatus.setText(results + " matches");
				if (cPos > 0  && matches == 0 && !alwaysFromTop) { //ask if user wants to search from top
					int result = JOptionPane.showConfirmDialog(null, "No more matches found.  Do you want to search from the top of the document?", "Find", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						target.setCaretPosition(0);
						nextXpathMatch(xpathExpr, targetText, lblStatus, alwaysFromTop);
					}
				}
			}
		} catch (Exception e) {
			XmlEditorKit.HIGHLIGHTED_START = 0;
			XmlEditorKit.HIGHLIGHTED_END = 0;						
			e.printStackTrace();
		}
		return results;
	}
	
	public static void flashMatchingWindow(String txtXml) {
		if (txtXml.contains("hwnd")) {
			List<String> hwndList = WindowsEnumeratedXml.evaluateXpathGetValues(txtXml, "//@hwnd");
			if (hwndList.size() > 0) {
				String hwndStr = hwndList.get(0);
				HWND tHwnd = Api.GetHandleFromString(hwndStr);
				Api.highlightWindow(tHwnd);
				try { Thread.sleep(100); } catch (Exception e) {e.printStackTrace();}
				Api.refreshWindow(tHwnd);
				try { Thread.sleep(100); } catch (Exception e) {e.printStackTrace();}
				Api.highlightWindow(tHwnd);
				try { Thread.sleep(100); } catch (Exception e) {e.printStackTrace();}
				Api.refreshWindow(tHwnd);
			}
		}
	}

}
