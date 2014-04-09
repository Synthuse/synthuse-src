/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/

package org.synthuse;

import java.awt.Point;
import java.io.*;

public class WpfBridge {

	static
	{
	    String archDataModel = System.getProperty("sun.arch.data.model");//32 or 64 bit
	    
		//System.loadLibrary("native/WpfBridge" + archDataModel); // WpfBridge32.dll (Windows) or WpfBridge32.so (Unixes)
	    loadNativeLibraryFromJar("/wpfbridge" + archDataModel + ".dll");
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
        InputStream is = WpfBridge.class.getResourceAsStream(path);
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

	public native void setFrameworkId(String propertyValue); //default is WPF, but also accepts Silverlight, Win32	
	public native void setTouchableOnly(boolean val); //default is true
	
	//Descendants will walk the full tree of windows, NOT just one level of children
	public native int countDescendantWindows();
	public native int countDescendantWindows(String runtimeIdValue);
	
	public native int countChildrenWindows();
	public native int countChildrenWindows(String runtimeIdValue);
	
	public native String[] enumChildrenWindowIds(String runtimeIdValue); //if runtimeIdValue is null will start at desktop
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
	
	public Point getCenterOfElement(String runtimeIdValue) {
		Point p = new Point();
    	String boundary = getProperty("BoundingRectangleProperty", runtimeIdValue);
    	//System.out.println("boundary: " + boundary); //boundary: 841,264,125,29
    	String[] boundarySplt = boundary.split(",");
    	int x = Integer.parseInt(boundarySplt[0]);
    	int y = Integer.parseInt(boundarySplt[1]);
    	int width = Integer.parseInt(boundarySplt[2]);
    	int height = Integer.parseInt(boundarySplt[3]);
    	p.x = ((width) /2) + x;
    	p.y = ((height) /2) + y;
		return p;
	}
	
	public String getWindowClass(String runtimeIdValue) {
		return getProperty("ClassNameProperty", runtimeIdValue);
	}
	
	public String getWindowText(String runtimeIdValue) {
		return getProperty("NameProperty", runtimeIdValue);
	}
	
	public String getWindowValue(String runtimeIdValue) {
		return getProperty("ValueProperty", runtimeIdValue);
	}
	
	public String getWindowAutomationId(String runtimeIdValue) {
		return getProperty("AutomationIdProperty", runtimeIdValue);
	}

	public String getWindowFramework(String runtimeIdValue) {
		return getProperty("FrameworkIdProperty", runtimeIdValue);
	}
	
}
