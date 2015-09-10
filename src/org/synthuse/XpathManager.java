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

import org.synthuse.Api.User32Ex;

import com.sun.jna.platform.win32.WinDef.HWND;

public class XpathManager implements Runnable{

	private HWND hwnd = null;
	private String enumProperties = null;
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
	
	public XpathManager(HWND hwnd, String enumProperties, JTextPane windowsXmlTextPane, Events events) {
		this.events = events;
		this.hwnd = hwnd;
		this.enumProperties = enumProperties;
		this.windowsXmlTextPane = windowsXmlTextPane;
	}
	
	@Override
	public void run() {
		//@TODO (AJK) create separate action to use text mathing
		String results = buildXpathStatement(true,50,50);
		events.executionCompleted(hwnd, results);
	}
	
	public static void buildXpathStatementThreaded(HWND hwnd, JTextPane windowsXmlTextPane, Events events) {
		Thread t = new Thread(new XpathManager(hwnd, windowsXmlTextPane, events));
        t.start();
	}
	
	public static void buildXpathStatementThreaded(HWND hwnd, String runtimeId, JTextPane windowsXmlTextPane, Events events) {
		Thread t = new Thread(new XpathManager(hwnd, runtimeId, windowsXmlTextPane, events));
        t.start();
	}
	
	public String compareLongTextString(String rawText) {
		String escapedTxtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(rawText);
		if (!escapedTxtStr.isEmpty()) {
			if (rawText.length() > 20) {// if the raw text is too long only test the first 20 characters
				escapedTxtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(rawText.substring(0, 20));
			}
		}
		return escapedTxtStr;
	}
	
	public String buildUiaXpathStatement() {
		if (enumProperties == null)
			return "";
		if (enumProperties.isEmpty())
			return "";
		String builtXpath = "";
		String xml = this.windowsXmlTextPane.getText();
		
		WindowInfo wi = new WindowInfo(enumProperties, true);
		String onlyRuntimeIdXpath = "//*[@hwnd='" + wi.runtimeId + "']";
		List<String> wpfResultList = WindowsEnumeratedXml.evaluateXpathGetValues(xml, onlyRuntimeIdXpath);
		//System.out.println("evaluateXpathGetValues1: " + onlyRuntimeIdXpath + " = " + wpfResultList.size());
		if (wpfResultList.size() == 0)
			return"";
		//System.out.println("enumProperties: " + enumProperties);
		String typeStr = wi.controlType;
		String txtOrig = wi.text;
		//String winValueOrig = wpf.getWindowValue(runtimeId);
		//System.out.println("text: " + txtOrig);
		String txtStr = compareLongTextString(txtOrig);
		
		builtXpath = "//*[@type='" + typeStr + "' and starts-with(@text,'" + txtStr + "')" + "]";
		
		//builtXpath = "//*[@hwnd='" + runtimeId + "']";
		wpfResultList = WindowsEnumeratedXml.evaluateXpathGetValues(xml, builtXpath);
		//System.out.println("evaluateXpathGetValues2: " + builtXpath + " = " + wpfResultList.size());
		if (wpfResultList.size() == 1)
			return builtXpath;
		return onlyRuntimeIdXpath;
	}
	
	public String buildXpathStatement() {
		return buildXpathStatement(false, 20, 30);
	}

	public String buildXpathStatement(boolean useFullTextMatching, int maxParentTextLength, int maxTextLength) {
		String builtXpath = "";
		try {
			String xml = this.windowsXmlTextPane.getText();
			if (enumProperties != null && !SynthuseDlg.config.isUiaBridgeDisabled()) {
				if (!enumProperties.isEmpty()) {
					builtXpath = buildUiaXpathStatement();
				}
			}
			if (builtXpath != "")
				return builtXpath;
			String classStr = WindowsEnumeratedXml.escapeXmlAttributeValue(Api.getWindowClassName(hwnd));
			String handleStr = Api.GetHandleAsString(hwnd);
			String txtOrig = Api.getWindowText(hwnd);
			String txtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(txtOrig);
			builtXpath = "//win[@class='" + classStr + "']";
			List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(xml, builtXpath);
			//int matches = nextXpathMatch(builtXpath, textPane, true);
			if (resultList.size() > 1) { // if there are multiple results with the simple xpath then include parent class and text with the xpath statement.
				HWND parent = User32Ex.instance.GetParent(hwnd);
				String parentClassStr = WindowsEnumeratedXml.escapeXmlAttributeValue(Api.getWindowClassName(parent));
				String parentTxtOrig = Api.getWindowText(parent);
				String parentTxtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(parentTxtOrig);
				if (!parentTxtStr.isEmpty()) {
					if (parentTxtOrig.length() > maxParentTextLength) {// if the parent text is too long only test the first maxParentTextLength characters
						parentTxtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(parentTxtOrig.substring(0, maxParentTextLength));
						parentTxtStr = " and starts-with(@text,'" + parentTxtStr + "')";
					}
					else
						parentTxtStr = " and @text='" + parentTxtStr + "'";
				}
				if (!parentClassStr.isEmpty())
				{
					if (!txtStr.isEmpty()&&useFullTextMatching) {
						String copyOfTxtStr = txtStr;
						if (copyOfTxtStr.length() > maxTextLength) {// if the text is too long only test the first maxTextLength characters
							copyOfTxtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(copyOfTxtStr.substring(0, maxTextLength));
							copyOfTxtStr = " and starts-with(@text,'" + copyOfTxtStr + "')";
						}
						else
							copyOfTxtStr = " and @text='" + copyOfTxtStr + "'";
						builtXpath = "//win[@class='" + parentClassStr + "'" + parentTxtStr + "]/win[@class='" + classStr + "'" + copyOfTxtStr + "]";
					}
					else
					{
						builtXpath = "//win[@class='" + parentClassStr + "'" + parentTxtStr + "]/win[@class='" + classStr + "']";
					}
				}
				System.out.println(builtXpath);
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
						if (txtStr.length() > maxTextLength) {// if the text is too long only test the first maxTextLength characters
							txtStr = WindowsEnumeratedXml.escapeXmlAttributeValue(txtStr.substring(0, maxTextLength));
							txtStr = " and starts-with(@text,'" + txtStr + "')";
						}
						else
							txtStr = " and @text='" + txtStr + "'";
					}
					builtXpath = "//win[@class='" + classStr + "'" + txtStr + "]";
				}
				resultList = WindowsEnumeratedXml.evaluateXpathGetValues(xml, builtXpath);
				if (resultList.size() > 1) //still too many matched, only use hwnd
					builtXpath = "//win[@hwnd='" + handleStr + "']";
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
				int cPos = 0;
				try {
					cPos = target.getCaretPosition();
				} catch(Exception ex) {
					//return 0;//something is throwing nullpointer exception
				}
				if (alwaysFromTop)
					cPos = 0;
				int len = target.getStyledDocument().getLength();
				String xml = target.getText(0, len);
				WindowsEnumeratedXml.lastException = null;
				List<String> resultList = WindowsEnumeratedXml.evaluateXpathGetValues(xml, xpathExpr);
				if (resultList.size() == 0 && WindowsEnumeratedXml.lastException != null) {
					String errMsg = WindowsEnumeratedXml.lastException.getCause().getMessage();
					JOptionPane.showMessageDialog(target.getTopLevelAncestor(), "Exception: " + errMsg, "Error", JOptionPane.ERROR_MESSAGE);
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
				lblStatus.setText(results + " match(es)");
				if (cPos > 0  && matches == 0 && !alwaysFromTop) { //ask if user wants to search from top
					int result = JOptionPane.showConfirmDialog(target.getTopLevelAncestor(), "No more matches found.  Do you want to search from the top of the document?", "Find", JOptionPane.YES_NO_OPTION);
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
