package org.synthuse.test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.synthuse.*;

public class WpfBridgeTest {
	
	static Process mockApp = null;
	public static void RunMockWpfApp() {
		String tempFilename = ExtractFileFromJar("/org/synthuse/test/WpfMockTestApp.exe");
		Runtime runtime = Runtime.getRuntime();
		try {
			mockApp = runtime.exec(tempFilename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		//this runs only once for this class
		RunMockWpfApp();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		//this runs only once for this class
		if (mockApp != null)
			mockApp.destroy();
	}
		
	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void countChildrenWin32() {
		WpfBridge wb = new WpfBridge();
		wb.setFrameworkId("Win32");//We should find some Win32 windows, maybe not WPF
		int win32Cnt = wb.countChildrenWindows(); 
		System.out.println("win32 countChildrenWindows: " + win32Cnt);
		assertTrue(win32Cnt > 0);
		wb.setTouchableOnly(false);//disable filter
		int ufwin32Cnt = wb.countChildrenWindows(); 
		System.out.println("win32 unfiltered countChildrenWindows: " + ufwin32Cnt);
		assertTrue(ufwin32Cnt >= win32Cnt);		
	}
	
	@Test
	public void countChildrenWpf() {
		WpfBridge wb = new WpfBridge();
		wb.setFrameworkId("WPF");// maybe not WPF
		wb.setTouchableOnly(true);//enable filter
		System.out.println("wpf countChildrenWindows: " + wb.countChildrenWindows());
		wb.setTouchableOnly(false);//disable filter
		System.out.println("wpf unfiltered countChildrenWindows: " + wb.countChildrenWindows());
	}
	
	//@Test
	public void countDescendantsWin32() {
		WpfBridge wb = new WpfBridge();
		wb.setFrameworkId("Win32");//We should find some Win32 windows, maybe not WPF
		int win32Cnt = wb.countDescendantWindows(); 
		System.out.println("win32 countDescendantWindows: " + win32Cnt);
		assertTrue(win32Cnt > 0);
		wb.setTouchableOnly(false);//disable filter
		int ufwin32Cnt = wb.countDescendantWindows(); 
		System.out.println("win32 unfiltered countDescendantWindows: " + ufwin32Cnt);
		assertTrue(ufwin32Cnt >= win32Cnt);
	}
	
	//@Test
	public void countDescendantsWpf() {
		WpfBridge wb = new WpfBridge();
		wb.setFrameworkId("WPF");// maybe not WPF
		wb.setTouchableOnly(true);//enable filter
		System.out.println("wpf countDescendantWindows: " + wb.countDescendantWindows());
		wb.setTouchableOnly(false);//disable filter
		System.out.println("wpf unfiltered countDescendantWindows: " + wb.countDescendantWindows());
	}

	
	@Test
	public void getRuntimeFromHandle() {
		WpfBridge wb = new WpfBridge();
		Api api = new Api();
		wb.setFrameworkId("WPF");
		WinPtr wp = new WinPtr(api.user32.FindWindow(null, "MainWindow"));//find WpfMockTestApp.exe
		long handle = Long.parseLong(wp.hWndStr);
		if (handle == 0)
			return;
		System.out.println("calling getRuntimeIdFromHandle: " + handle);
		String rid = wb.getRuntimeIdFromHandle(handle);
		System.out.println(wp.hWndStr + " getRuntimeIdFromHandle: " + rid);
		assertTrue(rid != null);
		
		String[] props = wb.getPropertiesAndValues(rid);
		assertTrue(props != null);
		
		//for(String p : props)
		//	System.out.println(p);
	}
	
	@Test
	public void enumerateWindowInfo() {
		WpfBridge wb = new WpfBridge();
		Api api = new Api();
		wb.setFrameworkId("WPF");
		wb.setTouchableOnly(false);
		WinPtr wp = new WinPtr(api.user32.FindWindow(null, "MainWindow"));//find WpfMockTestApp.exe
		long handle = Long.parseLong(wp.hWndStr);
		if (handle == 0)
			return;
		String rid = wb.getRuntimeIdFromHandle(handle);
		System.out.println(wp.hWndStr + " getRuntimeIdFromHandle: " + rid);
		if (rid == null)
			return;
		String[] wInfo = wb.enumDescendantWindowInfo(rid, WindowInfo.WPF_PROPERTY_LIST);
		System.out.println("enumDescendantWindowInfo length: " + wInfo.length);
		System.out.println(WindowInfo.WPF_PROPERTY_LIST);
		for(String w : wInfo)
			System.out.println(w);
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
