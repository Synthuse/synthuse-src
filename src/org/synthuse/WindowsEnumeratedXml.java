/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;

public class WindowsEnumeratedXml implements Runnable{

	public static Exception lastException = null;
	public static AtomicBoolean enumeratingXmlFlag = new AtomicBoolean(false);
	public JTextPane outputPane = null;
	public JLabel lblStatus = null;
	public WindowsEnumeratedXml() {
	}
	
	public WindowsEnumeratedXml(JTextPane outputPane, JLabel lblStatus) {
		this.outputPane = outputPane;
		this.lblStatus = lblStatus;
	}
	
	@Override
	public void run() {
		lblStatus.setText("Loading Windows Enumerated Xml...");
		long startTime = System.nanoTime();    
		outputPane.setText(getXml());
		outputPane.setCaretPosition(0);
		double seconds = ((double)(System.nanoTime() - startTime) / 1000000000);
		lblStatus.setText("Windows Enumerated Xml loaded in " + new DecimalFormat("#.###").format(seconds) + " seconds");
		enumeratingXmlFlag.set(false);
	}
	
	public static void getXmlThreaded(JTextPane outputPane, JLabel lblStatus) {
		if (enumeratingXmlFlag.get())
			return; //something is already running
		enumeratingXmlFlag.set(true); //if we don't do this the multiple xml's could get combined on the textpane
		Thread t = new Thread(new WindowsEnumeratedXml(outputPane, lblStatus));
        t.start();
	}
	
	public static String getXml() {
		final Map<String, WindowInfo> infoList = new LinkedHashMap<String, WindowInfo>();
		
		HWND desktopRootHwnd = Api.User32Ex.instance.GetDesktopWindow();
		WindowInfo wi = new WindowInfo(desktopRootHwnd, false);
		wi.controlType = "DesktopRoot";
		infoList.put(wi.hwndStr, wi);
	    
	    class ParentWindowCallback implements WinUser.WNDENUMPROC {
			@Override
			public boolean callback(HWND hWnd, Pointer lParam) {
				WindowInfo wi = new WindowInfo(hWnd, false);
				infoList.put(wi.hwndStr, wi);
				infoList.putAll(EnumerateWin32ChildWindows(hWnd));
				return true;
			}
	    }
	    Api.User32Ex.instance.EnumWindows(new ParentWindowCallback(), 0);
	    
	    //process all windows that have been flagged for uiaBridge (useUiaBridge == true)
	    appendUiaBridgeWindows(infoList);
	    
	    return generateWindowsXml(infoList, "EnumeratedWindows");
	}
	
	public static void appendUiaBridgeWindows(Map<String, WindowInfo> infoList)
	{
	    //Enumerate WPF, WinForm, Silverlight windows and add to list
	    if (!SynthuseDlg.config.isUiaBridgeDisabled())
	    {
	    	UiaBridge uiabridge = new UiaBridge();
	    	Map<String, WindowInfo> uiaInfoList = new LinkedHashMap<String, WindowInfo>();
	    	for (String handle : infoList.keySet()) {
	    		if (infoList.get(handle).useUiaBridge) {
	    			uiaInfoList.putAll(EnumerateWindowsWithUiaBridge(uiabridge, handle, "*"));
	    		}
	    	}
	    	infoList.putAll(uiaInfoList);
	    }
	    //return infoList;
	}
	
	public static Map<String, WindowInfo> EnumerateWin32ChildWindows(HWND parentHwnd)
	{
		final Map<String, WindowInfo> infoList = new LinkedHashMap<String, WindowInfo>();
		
	    class ChildWindowCallback implements WinUser.WNDENUMPROC {
			@Override
			public boolean callback(HWND hWnd, Pointer lParam) {
				WindowInfo wi = new WindowInfo(hWnd, true);
				infoList.put(wi.hwndStr, wi);
				return true;
			}
	    }
	    
		Api.User32Ex.instance.EnumChildWindows(parentHwnd, new ChildWindowCallback(), new Pointer(0));
	    
		return infoList;
	}

	public static String generateWindowsXml(Map<String, WindowInfo> infoList, String rootElementName)
	{
		final Map<String, String> processList = new LinkedHashMap<String, String>();
		int wpfCount = 0;
		int winFormCount = 0;
		int silverlightCount = 0;
		int menuCount = 0;
	    // convert window info list to xml dom
	    try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		    
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(rootElementName);
			doc.appendChild(rootElement);
			
			long parentCount = 0;
			long childCount = 0;
		    for (String handle : infoList.keySet()) {
		    	WindowInfo w = infoList.get(handle);
		    	//System.out.println(w);
		    	// create new win xml element
		    	Element win = null;
		    	if (w.framework.equals("win32"))
		    		win = doc.createElement("win");
		    	else if (w.framework.equals(UiaBridge.FRAMEWORK_ID_WPF))
		    	{
		    		win = doc.createElement("wpf");
		    		++wpfCount;
		    	}
		    	else if (w.framework.equals(UiaBridge.FRAMEWORK_ID_WINFORM))
		    	{
		    		win = doc.createElement("winfrm");
		    		++winFormCount;
		    	}
		    	else if (w.framework.equals(UiaBridge.FRAMEWORK_ID_SILVER))
		    	{
		    		win = doc.createElement("silver");
		    		++silverlightCount;
		    	}
		    	else
		    		win = doc.createElement("win");
		    	//System.out.println(w.toString());
		    	
				win.setAttribute("hwnd", w.hwndStr);
				win.setAttribute("text", w.text);
				if (w.value != "" && w.value != null)
					win.setAttribute("value", w.value);
				if (w.text != null)
					win.setAttribute("TEXT", w.text.toUpperCase());
				win.setAttribute("class", w.className);
				if (w.className != null)
					win.setAttribute("CLASS", w.className.toUpperCase());
				if (w.controlType != null)
					if (!w.controlType.isEmpty())
						win.setAttribute("type", w.controlType);
				if (!w.isChild) {
					//win.setAttribute("parent", "yes");
					parentCount++;
					if (w.processName != null && !w.processName.isEmpty()) {
						if (!processList.containsKey(w.pid+""))
							processList.put(w.pid+"", w.hwndStr);
						win.setAttribute("process", w.processName);
						if (w.processName != null)
							win.setAttribute("PROCESS", w.processName.toUpperCase());
					}
					if (w.pid != 0)
					{
						if (w.is64bit)
							win.setAttribute("bits", "64");
						else
							win.setAttribute("bits", "32");
					}
				}
				if (w.pid != 0)
					win.setAttribute("pid", w.pid+"");
				//else
					//win.setAttribute("parent", w.parent + ""); // not really needed since child node is append to parent node
				
				
				if (w.extra != null) {
					for(String extraName: w.extra.keySet()) {
						win.setAttribute(extraName, w.extra.get(extraName)+"");
					}
				}
				
				if (w.menus > 0) {
					win.setAttribute("menus", w.menus+"");
					//String menuStr = MenuInfo.GetHandleMenuAsString(w.menu);
					buildMenuXmlElements(doc, win, w.menu, w.hwndStr);
					++menuCount;
				}
				
				if (w.isChild && infoList.containsKey(w.parentStr)) {
					childCount++;
					WindowInfo parentWi = infoList.get(w.parentStr);
					if (parentWi.xmlObj != null)
						((Element)parentWi.xmlObj).appendChild(win);
					else
						rootElement.appendChild(win);
				}
				else
					rootElement.appendChild(win);
				w.xmlObj = win;

		    }
		    
		    // calculate totals on various windows.
		    Element totals = doc.createElement("totals");
		    totals.setAttribute("parentCount", parentCount+"");
		    totals.setAttribute("childCount", childCount+"");
		    totals.setAttribute("windowCount", infoList.size()+"");
		    //totals.setAttribute("wpfWrapperCount", wpfParentList.size()+"");
		    totals.setAttribute("wpfCount", wpfCount+"");
		    totals.setAttribute("winFormCount", winFormCount+"");
		    totals.setAttribute("silverlightCount", silverlightCount+"");
		    totals.setAttribute("menuCount", menuCount+"");
		    totals.setAttribute("processCount", processList.size()+"");
		    totals.setAttribute("updatedLast", new Timestamp((new Date()).getTime()) + "");
		    rootElement.appendChild(totals);
		    String output = nodeToString(rootElement);
		    //System.out.println("count - " + infoList.size() + "\r\n");
		    return output;		    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	lastException = e;
	    }
	    return "";
	}
	
	public static Element buildMenuXmlElements(Document xmlDoc, Element xmlElement, HMENU targetMenu, String targetWin)
	{
		MenuInfo firstMi = new MenuInfo(targetWin, targetMenu);
		for (int i = 0 ; i < firstMi.menuCount ; i++ ) {
			MenuInfo menuInfo = new MenuInfo(targetWin, targetMenu, i);
			Element menuElement = xmlDoc.createElement("menu");
			menuElement.setAttribute("unaltered", menuInfo.unaltered + "");
			menuElement.setAttribute("text", menuInfo.text + "");
			menuElement.setAttribute("id", menuInfo.id + "");
			menuElement.setAttribute("position", menuInfo.position + "");
			menuElement.setAttribute("hmenu", menuInfo.hmenuStr + "");
			menuElement.setAttribute("hwnd", menuInfo.hwndStr + "");
			if (!menuInfo.center.isEmpty())
				menuElement.setAttribute("center", menuInfo.center + "");
			if (menuInfo.hasSubMenu) {
				buildMenuXmlElements(xmlDoc, menuElement, menuInfo.submenu, targetWin);
			}
			xmlElement.appendChild(menuElement);
		}
		return xmlElement;
	}
	
	public static Map<String, WindowInfo> EnumerateWindowsWithUiaBridge(UiaBridge uiabridge, String parentHwndStr, String frameworkType) {
		final Map<String, WindowInfo> infoList = new LinkedHashMap<String, WindowInfo>();
    	//WpfBridge wb = new WpfBridge();
		//wpf.setFrameworkId(frameworkType);
    	long hwnd = Long.parseLong(parentHwndStr);
		//System.out.println("getRuntimeIdFromHandle of " + hwnd);
    	String parentRuntimeId = uiabridge.getWindowInfo((int) hwnd, WindowInfo.UIA_RUNTIME_ID);
		//System.out.println("runtimeId=" + runtimeId);
    	String[] allIds = null;
    	if (SynthuseDlg.config.isFilterUiaDisabled())
    		allIds = uiabridge.enumWindowInfo((int) hwnd, "*");
    	else
    		allIds = uiabridge.enumWindowInfo((int) hwnd, WindowInfo.UIA_PROPERTY_LIST);
    	if (allIds == null)
    		return infoList; //empty list
		//System.out.println("enumDescendantWindowIds " + allIds.length);
    	for(String runtimeIdAndInfo : allIds) {
    		//System.out.println("getting window info for: " + runtimeIdAndInfo);
    		WindowInfo wi = new WindowInfo(runtimeIdAndInfo, true);
    		if (wi.parentStr.equals(parentRuntimeId))
    			wi.parentStr = parentHwndStr;
    		//System.out.println("is parent? " + onlyRuntimeId);
    		infoList.put(wi.runtimeId, wi);
    	}
		return infoList;
	}
	
	public static String escapeXmlAttributeValue(String unescapedStr) {
		String result = "";
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();	    
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("temp");
			doc.appendChild(rootElement);
			Attr attribute = doc.createAttribute("attrib");
			attribute.setNodeValue(unescapedStr);
			rootElement.setAttributeNode(attribute);
			result = nodeToString(rootElement);
			result = result.replaceAll("[^\"]*\"([^\"]*)\"[^\"]*", "$1"); // keep the string within quotes.
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	//lastException = e;
	    }
		return result;
	}
	
	private static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (Exception e) {
			e.printStackTrace();
			lastException = e;
		}
		return sw.toString();
	}
	
	public static String queryWindowInfoXml(String xpathExpr) {
		String nl = System.getProperty("line.separator");
		String result = "<result>" + nl;
		String xml = getXml();
		List<String> resultList = evaluateXpathGetValues(xml, xpathExpr);
		for(String item: resultList) {
			result += "  " + item;
		}
		result += "</result>" + nl;
		return result;
	}
	
	public static HWND queryHandleWindowInfoXml(String xpathExpr) {
		String xml = getXml();
		String result =  "";
		List<String> resultList = evaluateXpathGetValues(xml, xpathExpr);
		for(String item: resultList) {
			if (item.contains("hwnd")) {
				List<String> hwndList = evaluateXpathGetValues(item, "//@hwnd");
				result = hwndList.get(0);
			}
			else
				result = item;
			break;
		}
		return Api.GetHandleFromString(result);
	}

	
	public static List<String> evaluateXpathGetValues(String xml, String xpathExpr) {
		List<String> resultLst = new ArrayList<String>();
		try {
			InputSource inSource = new InputSource(new StringReader(xml));
			XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    XPathExpression expr = xpath.compile(xpathExpr);

		    Object result = expr.evaluate(inSource, XPathConstants.NODESET);
		    NodeList nodes = (NodeList) result;
		    for (int i = 0; i < nodes.getLength(); i++) {
		    	String val = nodes.item(i).getNodeValue();
		    	if (val == null) // if we can't get a string value try to transform the xml node to a string
		    		val = nodeToString(nodes.item(i));
		    	else
		    		val += System.getProperty("line.separator");
		    	resultLst.add(val);
		        //System.out.println("match " + i + ": " + val); 
		    }
		} catch(Exception e) {
			e.printStackTrace();
			lastException = e;
		}
		return resultLst;
	}

}
