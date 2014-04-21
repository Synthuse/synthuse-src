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
import com.sun.jna.platform.win32.WinUser.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.win32.W32APIOptions;

public class KeyboardHook implements Runnable{
	
	
	// Keyboard event class, interface, and array list
	public static class TargetKeyPress {
		int targetKeyCode;
		boolean withShift, withCtrl, withAlt;
		public TargetKeyPress (int targetKeyCode) {
			this.targetKeyCode = targetKeyCode;
			this.withShift = false;
			this.withCtrl = false;
			this.withAlt = false;
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
	
	public static HHOOK hHook = null;
	public static LowLevelKeyboardProc lpfn;
	public static volatile boolean quit = false;
	
	public interface User32 extends W32APIOptions {  
		User32 instance = (User32) Native.loadLibrary("user32", User32.class, DEFAULT_OPTIONS);  
		
		LRESULT LowLevelKeyboardProc(int nCode,WPARAM wParam,LPARAM lParam);
	    HHOOK SetWindowsHookEx(int idHook, HOOKPROC lpfn, HMODULE hMod, int dwThreadId);
	    LRESULT CallNextHookEx(HHOOK idHook, int nCode, WPARAM wParam, LPARAM lParam);
	    LRESULT CallNextHookEx(HHOOK idHook, int nCode, WPARAM wParam, Pointer lParam);
	    boolean PeekMessage(MSG lpMsg, HWND hWnd, int wMsgFilterMin, int wMsgFilterMax, int wRemoveMsg);
	    boolean UnhookWindowsHookEx(HHOOK idHook);
	    short GetKeyState(int nVirtKey);
        
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
	    		return User32.instance.CallNextHookEx(hHook, nCode, wParam, lParam.getPointer());
	    	}
		};
		
		hHook = User32.instance.SetWindowsHookEx(WH_KEYBOARD_LL, lpfn, hMod, 0);
		if (hHook == null)
			return;
		MSG msg = new MSG();
		try {
			while (!quit) {
				User32.instance.PeekMessage(msg, null, 0, 0, 0);
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//unhook the Global Windows Keyboard hook
	public void unhook() {
		if (hHook == null)
			return;
	    if (!User32.instance.UnhookWindowsHookEx(hHook))
	    	System.out.println("Failed to unhook");
	    //System.out.println("Unhooked");
	    hHook = null;
	}
	
	//stops Keyboard hook and causes the unhook command to be called
	public static void stopGlobalKeyboardHook() {
		quit = true;
	}
	
	// search target keyboard event list for a match and return it otherwise return null if no match
	public TargetKeyPress getTargetKeyPressed(int keyCode) {
		TargetKeyPress target = null;
		for (TargetKeyPress tkp : KeyboardHook.targetList) {
			if (tkp.targetKeyCode != keyCode)
				continue;
			if (!tkp.withShift || ((User32.instance.GetKeyState(VK_SHIFT) & 0x8000) != 0)) {
				if (!tkp.withCtrl || ((User32.instance.GetKeyState(VK_CONTROL) & 0x8000) != 0)) {
					if (!tkp.withAlt || ((User32.instance.GetKeyState(VK_MENU) & 0x8000) != 0)) {
						return tkp;
					}
				}
			}
		}
		return target;
	}
	
	// add more target keys to watch for
	public static void addKeyEvent(int targetKeyCode, boolean withShift, boolean withCtrl, boolean withAlt) {
		KeyboardHook.targetList.add(new TargetKeyPress(targetKeyCode, withShift, withCtrl, withAlt));
	}
	// add more target keys to watch for
	public static void addKeyEvent(int targetKeyCode) {
		KeyboardHook.targetList.add(new TargetKeyPress(targetKeyCode));
	}
	
	@Override
	public void run() {
		createGlobalKeyboardHook();
		unhook();//wait for quit == true then unhook
	}
	
	public KeyboardHook() {
	}

	public KeyboardHook(KeyboardEvents events) {
		this.events = events;
	}
	
	public static void StartGlobalKeyboardHookThreaded(KeyboardEvents events) {
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
