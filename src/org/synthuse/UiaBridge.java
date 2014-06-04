/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/

package org.synthuse;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.*;

import javax.swing.JOptionPane;

public class UiaBridge {
	
	public static String CACHED_PROPERTY_LIST = "RuntimeIdProperty,ParentRuntimeIdProperty,ProcessIdProperty,FrameworkIdProperty,LocalizedControlTypeProperty,ClassNameProperty,NameProperty,ValueProperty,BoundingRectangleProperty";
	public static final String FRAMEWORK_ID_WPF = "WPF";
	public static final String FRAMEWORK_ID_SILVER = "Silverlight";
	public static final String FRAMEWORK_ID_WINFORM = "WinForm";
	public static final String FRAMEWORK_ID_WIN = "Win32";
	
	static
	{
		String loadFailedMsg = "Failed to load uiabridge library, make sure you have .Net 4.0 already installed.\n";
		if (!Api.isDotNet4Installed()) { //if .net 4.0 isn't installed don't use uiabridge
			SynthuseDlg.config.disableUiaBridge = "true";
			JOptionPane.showMessageDialog(null, loadFailedMsg , "Native Library Load Error", JOptionPane.ERROR_MESSAGE);
		}
		if (!SynthuseDlg.config.isUiaBridgeDisabled()) {
			//System.out.println("SynthuseDlg.config.disableUiaBridge: " + SynthuseDlg.config.disableUiaBridge);
		    String archDataModel = System.getProperty("sun.arch.data.model");//32 or 64 bit
		    try {
		    	//System.loadLibrary("native/WpfBridge" + archDataModel); // WpfBridge32.dll (Windows) or WpfBridge32.so (Unixes)
		    	loadNativeLibraryFromJar("/uiabridge" + archDataModel + ".dll");
		    } catch (Exception ex) {
		    	StringWriter sw = new StringWriter();
		    	PrintWriter pw = new PrintWriter(sw);
		    	ex.printStackTrace(pw);
		    	System.out.println(sw.toString());
		    	JOptionPane.showMessageDialog(null, loadFailedMsg + sw.toString() , "Native Library Load Error", JOptionPane.ERROR_MESSAGE);
		    	SynthuseDlg.config.disableUiaBridge = "true";
		    }
		}
	}
	
    public static void loadNativeLibraryFromJar(String path) {
        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
        // Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null;
        }
        File temp = null;
        try {
	        // Prepare temporary file
	        temp = File.createTempFile(prefix, suffix);
	        temp.deleteOnExit();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        if (!temp.exists()) { //some reason the temp file wasn't create so abort
            System.out.println("File " + temp.getAbsolutePath() + " does not exist.");
            return;
        }
 
        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;
        // Open and check input stream
        InputStream is = UiaBridge.class.getResourceAsStream(path);
        if (is == null) { //check if valid
            System.out.println("File " + path + " was not found inside JAR.");
            return;
        }
        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = null;
        try {
        	os = new FileOutputStream(temp);
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
       		os.close();
            is.close();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        // Finally, load the library
        System.load(temp.getAbsolutePath());
    }
    
    public UiaBridge ()
    {
    	if (!SynthuseDlg.config.isUiaBridgeDisabled())
    		initialize(CACHED_PROPERTY_LIST);
    }
    
    public native void initialize(String properties);
    public native void shutdown();
    public native void useCachedRequests(boolean cacheRequestsFlg);
    public native int addEnumFilter(String propertyName, String propertyValue);
    public native void clearEnumFilters();
    public native String[] enumWindowInfo(String properties);
    public native String[] enumWindowInfo(int windowHandle, String properties);
    //native String[] enumWindowInfo(AutomationElement ^element, String properties);
    //native String[] enumWindowInfo(AutomationElement ^element, String properties, String[] filterModifierList);
    //native String getWindowInfo(AutomationElement ^element, String properties);
    public native String getWindowInfo(int x, int y, String properties);
    public native String getWindowInfo(int windowHandle, String properties);
    public native String getWindowInfo(String runtimeId, String properties);

    
    /*
	public native void setFrameworkId(String propertyValue); //default is WPF, but also accepts Silverlight, Win32	
	public native void setTouchableOnly(boolean val); //default is true
	
	//Descendants will walk the full tree of windows, NOT just one level of children
	public native int countDescendantWindows();
	public native int countDescendantWindows(String runtimeIdValue);
	
	public native int countChildrenWindows();
	public native int countChildrenWindows(String runtimeIdValue);
	
	public String[] enumChildrenWindowIds(String runtimeIdValue); //if runtimeIdValue is null will start at desktop
	public native String[] enumDescendantWindowIds(String runtimeIdValue); //if runtimeIdValue is null will start at desktop
	public native String[] enumDescendantWindowIds(long processId);
	//In all the above Enumerate methods will return a list of Runtime Ids for all related windows.
	public native String[] enumDescendantWindowInfo(String runtimeIdValue, String properties); //returns properties comma separated

	public native String getRuntimeIdFromHandle(long windowHandle);
	public native String getRuntimeIdFromPoint(int x, int y);
	public native String getParentRuntimeId(String runtimeIdValue);
	public native String getProperty(String propertyName, String runtimeIdValue);
	public native String[] getProperties(String runtimeIdValue);
	public native String[] getPropertiesAndValues(String runtimeIdValue);
	*/
    
    public static String replaceEscapedCodes(String input) {
    	//&#44; is a comma ,
    	String result = input;
    	result = result.replaceAll("&#44;", ",");
    	result = result.replaceAll("&lt;", "<");
    	result = result.replaceAll("&gt;", ">");
    	result = result.replaceAll("&apos;", "'");
    	result = result.replaceAll("&quot;", "\"");
    	result = result.replaceAll("&amp;", "&");
    	return result;
    }
    
	public Point getCenterOfElement(String runtimeIdValue) {
		Point p = new Point();
    	String boundaryProperty = getWindowInfo(runtimeIdValue, "BoundingRectangleProperty");
    	Rectangle rect = getBoundaryRect(boundaryProperty);
    	p.x = ((rect.width) /2) + rect.x;
    	p.y = ((rect.height) /2) + rect.y;
		return p;
	}
	
	//BoundingRectangleProperty is the last property listed in comma separated properties string
	public static Rectangle getBoundaryRect(String properties) {
		Rectangle rect = new Rectangle();
		String[] propSplt = properties.split(",");
		if (propSplt.length > 0)
		{
			String[] boundarySplt = replaceEscapedCodes(propSplt[propSplt.length - 1]).split(",");
			if (boundarySplt.length == 4 )
			{
				rect.x = Integer.parseInt(boundarySplt[0]);
				rect.y = Integer.parseInt(boundarySplt[1]);
				rect.width = Integer.parseInt(boundarySplt[2]);
				rect.height = Integer.parseInt(boundarySplt[3]);
			}
		}
		return rect;
	}
}
