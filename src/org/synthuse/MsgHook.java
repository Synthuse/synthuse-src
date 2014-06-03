/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/

package org.synthuse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;


import javax.swing.JOptionPane;

public class MsgHook {

	public static String targetdllName = "";
	public static String dll64bitName = "";  
	public static String dll32bitName = "";  
	static
	{
		String loadFailedMsg = "Failed to load MsgHook library.\n";
		//System.out.println("SynthuseDlg.config.disableUiaBridge: " + SynthuseDlg.config.disableUiaBridge);
	    String archDataModel = System.getProperty("sun.arch.data.model");//32 or 64 bit
	    try {
	    	targetdllName = "/MsgHook" + archDataModel + ".dll";
	    	dll64bitName = SaveNativeLibraryFromJar("/MsgHook64.dll"); //need to save both 32 and 64 bit dlls for hooking both types
	    	dll32bitName = SaveNativeLibraryFromJar("/MsgHook32.dll");
	    	if (archDataModel.equals("32"))
	    		System.load(dll32bitName);
	    	else
	    		System.load(dll64bitName);
	    	
	    } catch (Exception ex) {
	    	StringWriter sw = new StringWriter();
	    	PrintWriter pw = new PrintWriter(sw);
	    	ex.printStackTrace(pw);
	    	System.out.println(sw.toString());
	    	JOptionPane.showMessageDialog(null, loadFailedMsg + sw.toString() , "Native Library Load Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
    public static String SaveNativeLibraryFromJar(String path) {
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
            return "";
        }
 
        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;
        // Open and check input stream
        InputStream is = MsgHook.class.getResourceAsStream(path);
        if (is == null) { //check if valid
            System.out.println("File " + path + " was not found inside JAR.");
            return "";
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
        return temp.getAbsolutePath();
    }
    
    public native boolean initialize(String dll32bitName, String dll64bitName);
    public native boolean createMsgHookWindow();
    public native boolean setMsgHookWindowTargetHwnd(int hwnd);
    public native boolean setMsgHookWindowTargetPid(int pid);
	public native boolean setMessageHook(int hwnd, int threadId);
	public native boolean removeMessageHook();
	//public native boolean shutdown();
	
	public static Thread createMsgHookWinThread(final long targetHwnd, final int targetPid)
	{
		Thread t = new Thread() {
			public void run() {
				MsgHook mh = new MsgHook();
				mh.initialize(dll32bitName, dll64bitName);
				if (targetPid != 0)
					mh.setMsgHookWindowTargetPid(targetPid);
				if (targetHwnd != 0)
					mh.setMsgHookWindowTargetHwnd((int)targetHwnd);
				mh.createMsgHookWindow();
			}
		};
		t.start();
		return t;
	}
	
}
