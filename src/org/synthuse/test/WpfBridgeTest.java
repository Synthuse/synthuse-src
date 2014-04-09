package org.synthuse.test;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.synthuse.*;

public class WpfBridgeTest {
	
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
	
	@Test
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
	
	@Test
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
}
