/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/

package org.synthuse;

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

	native void setFrameworkId(String propertyValue); //default is WPF, but also accepts Silverlight, Win32	
	
	//Descendants will walk the full tree of windows, NOT just one level of children
	native int countDescendantWindows();
	native int countDescendantWindows(String runtimeIdValue);
	
	native int countChildrenWindows();
	native int countChildrenWindows(String runtimeIdValue);
	
	native String[] enumChildrenWindowIds(String runtimeIdValue); //if runtimeIdValue is null will start at desktop
	native String[] enumDescendantWindowIds(String runtimeIdValue); //if runtimeIdValue is null will start at desktop
	native String[] enumDescendantWindowIds(long processId);
	native String[] enumDescendantWindowIdsFromHandle(long windowHandle);
	//In all the above Enumerate methods will return a list of Runtime Ids for all related windows.
	native String[] enumDescendantWindowInfo(String runtimeIdValue, String properties); //returns properties comma separated

	native String getParentRuntimeId(String runtimeIdValue);
	native String getProperty(String propertyName, String runtimeIdValue);
	native String[] getProperties(String runtimeIdValue);
	native String[] getPropertiesAndValues(String runtimeIdValue);
}
