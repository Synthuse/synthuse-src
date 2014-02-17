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
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;

public class WindowsEnumeratedXml implements Runnable{
	public static Exception lastException = null;

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
	}
	
	public static void getXmlThreaded(JTextPane outputPane, JLabel lblStatus) {
		Thread t = new Thread(new WindowsEnumeratedXml(outputPane, lblStatus));
        t.start();
	}
	
	public static String getXml() {
		final Map<String, WindowInfo> infoList = new LinkedHashMap<String, WindowInfo>();
		final Map<String, String> processList = new LinkedHashMap<String, String>(); 
		
	    class ChildWindowCallback implements WinUser.WNDENUMPROC {
			@Override
			public boolean callback(HWND hWnd, Pointer lParam) {
				infoList.put(Api.GetHandleAsString(hWnd), new WindowInfo(hWnd, true));
				return true;
			}
	    }
	    
	    class ParentWindowCallback implements WinUser.WNDENUMPROC {
			@Override
			public boolean callback(HWND hWnd, Pointer lParam) {
				infoList.put(Api.GetHandleAsString(hWnd), new WindowInfo(hWnd, false));
				Api.User32.instance.EnumChildWindows(hWnd, new ChildWindowCallback(), new Pointer(0));
				return true;
			}
	    }	    
	    Api.User32.instance.EnumWindows(new ParentWindowCallback(), 0);

	    // convert window info list to xml dom
	    try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		    
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("EnumeratedWindows");
			doc.appendChild(rootElement);
			
			long parentCount = 0;
			long childCount = 0;
		    for (String handle : infoList.keySet()) {
		    	WindowInfo w = infoList.get(handle);
		    	//System.out.println(w);
		    	// create new win xml element
		    	Element win = doc.createElement("win");
				win.setAttribute("hwnd", w.hwndStr);
				win.setAttribute("text", w.text);
				win.setAttribute("TEXT", w.text.toUpperCase());
				win.setAttribute("class", w.className);
				win.setAttribute("CLASS", w.className.toUpperCase());
				if (!w.isChild) {
					parentCount++;
					if (w.processName != null && !w.processName.isEmpty()) {
						if (!processList.containsKey(w.pid+""))
							processList.put(w.pid+"", w.hwndStr);
						win.setAttribute("process", w.processName);
						win.setAttribute("PROCESS", w.processName.toUpperCase());
						win.setAttribute("pid", w.pid+"");
					}
				}
				//else
					//win.setAttribute("parent", w.parent + ""); // not really needed since child node is append to parent node
				
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
