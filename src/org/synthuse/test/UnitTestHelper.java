package org.synthuse.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// This class doesn't contain Test, only some methods that are used by the unit tests
public class UnitTestHelper {
	
	static Process runningApp = null;
	public static void RunApp(String ResourceFilePath) {
		String tempFilename = ExtractFileFromJar(ResourceFilePath);
		Runtime runtime = Runtime.getRuntime();
		try {
			runningApp = runtime.exec(tempFilename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void DestroyApp() {
		if (runningApp != null)
			runningApp.destroy();
	}

    public static String ExtractFileFromJar(String path) {
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
            return null;
        }
 
        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;
        // Open and check input stream
        InputStream is = WpfBridgeTest.class.getResourceAsStream(path);
        if (is == null) { //check if valid
            System.out.println("File " + path + " was not found inside JAR.");
            return null;
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
        return temp.getAbsolutePath();
        // Finally, load the library
        //System.load(temp.getAbsolutePath());
    }

}
