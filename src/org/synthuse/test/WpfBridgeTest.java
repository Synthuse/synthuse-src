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
	public void countChildren() {
		WpfBridge wb = new WpfBridge();
		wb.setFrameworkId("Win32");//We should find some Win32 windows, maybe not WPF
		int win32Cnt = wb.countChildrenWindows(); 
		assertTrue(win32Cnt > 0);
		System.out.println("win32 countChildrenWindows: " + win32Cnt);
		wb.setFrameworkId("WPF");// maybe not WPF
		//System.out.println("wpf countChildrenWindows: " + wb.countChildrenWindows());
	}
	
	@Test
	public void getRuntimeFromHandle() {
		long handle = 1639790;
		//String rid = wb.getRuntimeIdFromHandle(handle);
		//System.out.println("getRuntimeIdFromHandle: " + rid);
		handle = 984416;
		//rid = wb.getRuntimeIdFromHandle(handle);
		//System.out.println("getRuntimeIdFromHandle: " + rid);
	}
	
	@Test
	public void enumerateWindowInfo() {
		WpfBridge wb = new WpfBridge();
		/*
		EnumerateWindowsWithWpfBridge: 1639790
		getRuntimeIdFromHandle
		runtimeId=42-1639790
		enumDescendantWindowIds 18
		EnumerateWindowsWithWpfBridge: 984416
		getRuntimeIdFromHandle
		runtimeId=42-984416
		*/
		//int count = wb.countDescendantWindows("42-984416");
		String[] wInfo = wb.enumDescendantWindowInfo("42-984416", WindowInfo.WPF_PROPERTY_LIST);
		if (wInfo != null)
			System.out.println("enumDescendantWindowInfo: " + wInfo.length);
	}
}
