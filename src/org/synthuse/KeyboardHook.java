/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/


package org.synthuse;

//import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.jna.*;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.win32.W32APIOptions;

public class KeyboardHook implements Runnable{
	
	
	// Keyboard event class, interface, and array list
	public static class TargetKeyPress {
		int idNumber;
		int targetKeyCode;
		boolean withShift, withCtrl, withAlt;
		public TargetKeyPress (int targetKeyCode) {
			this.targetKeyCode = targetKeyCode;
			this.withShift = false;
			this.withCtrl = false;
			this.withAlt = false;
		}
		public TargetKeyPress (int idNumber, int targetKeyCode, boolean withShift, boolean withCtrl, boolean withAlt) {
			this.idNumber = idNumber;
			this.targetKeyCode = targetKeyCode;
			this.withShift = withShift;
			this.withCtrl = withCtrl;
			this.withAlt = withAlt;
		}
		public TargetKeyPress (int targetKeyCode, boolean withShift, boolean withCtrl, boolean withAlt) {
			this.targetKeyCode = targetKeyCode;
			this.withShift = withShift;
			this.withCtrl = withCtrl;
			this.withAlt = withAlt;
		}
	}
	
	public static List<TargetKeyPress> targetList = Collections.synchronizedList(new ArrayList<TargetKeyPress>());// all keys we want to throw events on
	
	public static interface KeyboardEvents {
		void keyPressed(TargetKeyPress target);
	}
	public KeyboardEvents events = new KeyboardEvents() {
		public void keyPressed(TargetKeyPress target) {
			//System.out.println("target key pressed: " + target.targetKeyCode);
		}
	};
	
	// JNA constants and functions
	public static final int WH_KEYBOARD_LL = 13;
	//Modifier key vkCode constants 
	public static final int VK_SHIFT = 0x10;
	public static final int VK_CONTROL = 0x11;
	public static final int VK_MENU = 0x12;
	public static final int VK_CAPITAL = 0x14;
	
	public static final int MOD_ALT = 0x0001;
	public static final int MOD_CONTROL = 0x0002;
	public static final int MOD_NOREPEAT = 0x4000;
	public static final int MOD_SHIFT = 0x0004;
	public static final int MOD_WIN = 0x0008;
	
	public static final int QS_HOTKEY = 0x0080;
	public static final int INFINITE = 0xFFFFFFFF;
	
	public static HHOOK hHook = null;
	public static LowLevelKeyboardProc lpfn;
	public static volatile boolean quit = false;
	
	
	public interface User32Ex extends W32APIOptions {  
		User32Ex instance = (User32Ex) Native.loadLibrary("user32", User32Ex.class, DEFAULT_OPTIONS);  
		
		LRESULT LowLevelKeyboardProc(int nCode,WPARAM wParam,LPARAM lParam);
	    HHOOK SetWindowsHookEx(int idHook, HOOKPROC lpfn, HMODULE hMod, int dwThreadId);
	    LRESULT CallNextHookEx(HHOOK idHook, int nCode, WPARAM wParam, LPARAM lParam);
	    LRESULT CallNextHookEx(HHOOK idHook, int nCode, WPARAM wParam, Pointer lParam);
	    boolean PeekMessage(MSG lpMsg, HWND hWnd, int wMsgFilterMin, int wMsgFilterMax, int wRemoveMsg);
	    boolean UnhookWindowsHookEx(HHOOK idHook);
	    short GetKeyState(int nVirtKey);
	    short GetAsyncKeyState(int nVirtKey);
	    
	    /*
	    DWORD WINAPI MsgWaitForMultipleObjects(
	    __in  DWORD nCount, //The number of object handles in the array pointed to by pHandles.
	    __in  const HANDLE *pHandles, //An array of object handles.
	    __in  BOOL bWaitAll, //If this parameter is TRUE, the function returns when the states of all objects in the pHandles array have been set to signaled and an input event has been received.
	    __in  DWORD dwMilliseconds, //if dwMilliseconds is INFINITE, the function will return only when the specified objects are signaled.
	    __in  DWORD dwWakeMask //The input types for which an input event object handle will be added to the array of object handles.
	    );*/
	    int MsgWaitForMultipleObjects(int nCount, Pointer pHandles, boolean bWaitAll, int dwMilliSeconds, int dwWakeMask);
	    boolean RegisterHotKey(Pointer hWnd, int id, int fsModifiers, int vk);

	    //public static interface HOOKPROC extends StdCallCallback  {
        //    LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT lParam);
        //}
	}
	
	public interface Kernel32 extends W32APIOptions {
		Kernel32 instance = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, DEFAULT_OPTIONS);  
		
        HMODULE GetModuleHandle(String name);
	}
	
	// Create Global Windows Keyboard hook and wait until quit == true
	public void createGlobalKeyboardHook() {

		if (hHook != null)
			return; //hook already running don't add anymore
		System.out.println("starting global keyboard hook");
		HMODULE hMod = Kernel32.instance.GetModuleHandle(null);
		HOOKPROC lpfn = new HOOKPROC() {
	    	@SuppressWarnings("unused")
			public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT lParam) {
	    		//System.out.println("here " + lParam.vkCode);
	    		TargetKeyPress target = getTargetKeyPressed(lParam.vkCode); //find if this is a target key pressed
	    		if (target != null)
	    			events.keyPressed(target);
	    		//if (lParam.vkCode == 87) //w
	    		//	quit = true;
	    		return User32.INSTANCE.CallNextHookEx(hHook, nCode, wParam, lParam.getPointer());
	    	}
		};
		
		hHook = User32.INSTANCE.SetWindowsHookEx(WH_KEYBOARD_LL, lpfn, hMod, 0);
		if (hHook == null)
			return;
		
		//System.out.println("starting message loop");
		MSG msg = new MSG();
		try {
			
			while (!quit) {
				User32.INSTANCE.PeekMessage(msg, null, 0, 0, 1);
				if (msg.message == User32.WM_HOTKEY){ // && msg.wParam.intValue() == 1
					//System.out.println("Hot key pressed!");
					msg = new MSG(); //must clear msg so it doesn't repeat
				}
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("message loop stopped");
	}
	
	// Create HotKeys Windows hook and wait until quit == true
	public void createHotKeysHook() {
		registerAllHotKeys();
		//User32Ex.instance.MsgWaitForMultipleObjects(0, Pointer.NULL, true, INFINITE, QS_HOTKEY);

		//System.out.println("starting message loop");
		MSG msg = new MSG();
		try {
			
			while (!quit) {
				User32.INSTANCE.PeekMessage(msg, null, 0, 0, 1);
				if (msg.message == User32.WM_HOTKEY){ // && msg.wParam.intValue() == 1
					//System.out.println("Hot key pressed " + msg.wParam);
					TargetKeyPress target = findTargetKeyPressById(msg.wParam.intValue());
					if (target != null)
		    			events.keyPressed(target);
					msg = new MSG(); //must clear msg so it doesn't repeat
				}
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		unregisterAllHotKeys();
		//System.out.println("message loop stopped");
	}
	
	//unhook the Global Windows Keyboard hook
	public void unhook() {
		if (hHook == null)
			return;
	    if (!User32.INSTANCE.UnhookWindowsHookEx(hHook))
	    	System.out.println("Failed to unhook");
	    //System.out.println("Unhooked");
	    hHook = null;
	}
	
	//stops Keyboard hook and causes the unhook command to be called
	public static void stopKeyboardHook() {
		quit = true;
	}
	
	// search target keyboard event list for a match and return it otherwise return null if no match

	private TargetKeyPress getTargetKeyPressed(int keyCode) {
		TargetKeyPress target = null;
		for (TargetKeyPress tkp : KeyboardHook.targetList) {
			if (tkp.targetKeyCode != keyCode)
				continue;
			if (!tkp.withShift || ((User32Ex.instance.GetKeyState(VK_SHIFT) & 0x8000) != 0)) {
				if (!tkp.withCtrl || ((User32Ex.instance.GetKeyState(VK_CONTROL) & 0x8000) != 0)) {
					if (!tkp.withAlt || ((User32Ex.instance.GetKeyState(VK_MENU) & 0x8000) != 0)) {
						return tkp;
					}
				}
			}
		}
		return target;
	}
	
	private TargetKeyPress findTargetKeyPressById(int idNumber) 
	{
		TargetKeyPress target = null;
		for (TargetKeyPress tkp : KeyboardHook.targetList) {
			if (tkp.idNumber == idNumber)
				return tkp;
		}
		return target;
	}
	
	// add more target keys to watch for
	public static void addKeyEvent(int targetKeyCode, boolean withShift, boolean withCtrl, boolean withAlt) {
		KeyboardHook.targetList.add(new TargetKeyPress(KeyboardHook.targetList.size() + 1 , targetKeyCode, withShift, withCtrl, withAlt));
	}
	
	private void registerAllHotKeys() // must register hot keys in the same thread that is watching for hotkey messages
	{
		for (TargetKeyPress tkp : KeyboardHook.targetList) {
			//BOOL WINAPI RegisterHotKey(HWND hWnd, int id, UINT fsModifiers, UINT vk);
			int modifiers = User32.MOD_NOREPEAT;
			if (tkp.withShift)
				modifiers = modifiers | User32.MOD_SHIFT;
			if (tkp.withCtrl)
				modifiers = modifiers | User32.MOD_CONTROL;
			if (tkp.withAlt)
				modifiers = modifiers | User32.MOD_ALT;
			//System.out.println("RegisterHotKey " + tkp.idNumber + "," + modifiers + ", " + tkp.targetKeyCode);
			
			if (!User32.INSTANCE.RegisterHotKey(new WinDef.HWND(Pointer.NULL), tkp.idNumber, modifiers, tkp.targetKeyCode))
			{
	            System.out.println("Couldn't register hotkey " + tkp.targetKeyCode);
			}
		}
	}
	
	private void unregisterAllHotKeys() // must register hot keys in the same thread that is watching for hotkey messages
	{
		for (TargetKeyPress tkp : KeyboardHook.targetList) {
			if (!User32.INSTANCE.UnregisterHotKey(Pointer.NULL, tkp.idNumber))
			{
	            System.out.println("Couldn't unregister hotkey " + tkp.targetKeyCode);
			}
		}
	}

	
	// add more target keys to watch for
	public static void addKeyEvent(int targetKeyCode) {
		KeyboardHook.targetList.add(new TargetKeyPress(targetKeyCode));
	}
	
	@Override
	public void run() {
		//createGlobalKeyboardHook();
		createHotKeysHook();
		//System.out.println("Unhooking Global Keyboard Hook");
		unhook();//wait for quit == true then unhook
	}
	
	public KeyboardHook() {
	}

	public KeyboardHook(KeyboardEvents events) {
		this.events = events;
	}
	
	public static void StartKeyboardHookThreaded(KeyboardEvents events) {
		Thread t = new Thread(new KeyboardHook(events));
        t.start();
	}
	
	/*
	// testing 
	public static void main(String[] args) throws Exception {
		//add target keys
		KeyboardHook.addKeyEvent(KeyEvent.VK_3, true, true, false);
		KeyboardHook.addKeyEvent(KeyEvent.VK_5, false, true, false);
		KeyboardHook.addKeyEvent(KeyEvent.VK_Q);
		
		//add global hook and event
		KeyboardHook.StartGlobalKeyboardHookThreaded(new KeyboardHook.KeyboardEvents() {
			@Override
			public void keyPressed(KeyboardHook.TargetKeyPress target) {
				System.out.println("target key pressed " + target.targetKeyCode);
				if (target.targetKeyCode == KeyEvent.VK_Q){ // if Q was pressed then unhook
					KeyboardHook.stopGlobalKeyboardHook();
					System.out.println("unhooking");
				}
			}
		});
	}
	*/

}
