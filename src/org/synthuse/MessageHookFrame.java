package org.synthuse;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;


import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;
import com.sun.jna.platform.win32.WinUser.MSG;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MessageHookFrame extends JFrame {
	
	private static final long serialVersionUID = -5863279004595502801L;
	
	public static final int WH_CALLWNDPROC = 4;
	public static final int WH_GETMESSAGE = 3;
	public static final int WH_KEYBOARD_LL = 13;
	
	public static final int WM_COPYDATA = 74;
	
	private JTextArea textArea;
	private JButton btnSave;
	private JButton btnStartMsgHook;
	private JButton btnPause;
	private JButton btnClear;
	
	public static HHOOK hHook = null;
	//public static LowLevelKeyboardProc lpfn;
	public static volatile boolean quit = false;
	
	public MessageHookFrame() {
		setTitle("Message Hook");
		setBounds(100, 100, 700, 367);
		
		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		btnStartMsgHook = new JButton("Start Hook");
		btnStartMsgHook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnStartMsgHook.getText().equals("Start Hook"))
				{ //start Message Hook
					btnStartMsgHook.setText("Stop Hook");
					createMessageHook();
				}
				else
				{ //stop message hook
					btnStartMsgHook.setText("Start Hook");
					stopMessageHook();
				}
			}
		});
		toolBar.add(btnStartMsgHook);
		
		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		toolBar.add(btnPause);
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		toolBar.add(btnSave);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textArea.setText("");
			}
		});
		toolBar.add(btnClear);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		super.setAlwaysOnTop(SynthuseDlg.config.isAlwaysOnTop());
	}

	public void createMessageHook() {
		/*
		// Below set windows hook is called from inside the native DLL
		if (hHook != null)
			return; //hook already running don't add anymore
		System.out.println("starting global message hook");
		HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		int threadId = Kernel32.INSTANCE.GetCurrentThreadId();
		HOOKPROC lpfn = new HOOKPROC() {
	    	@SuppressWarnings("unused")
			public LRESULT callback(int nCode, WPARAM wParam, LPARAM lParam) {
	    		//System.out.println("Msg " + nCode);
	    		
	    		return User32.INSTANCE.CallNextHookEx(hHook, nCode, wParam, lParam);
	    	}
		};
		
		hHook = User32.INSTANCE.SetWindowsHookEx(WH_CALLWNDPROC, lpfn, hMod, threadId);
		if (hHook == null)
			return;
		*/
		MsgHook mh = new MsgHook();
		//mh.setMessageHook(hwnd, threadId);
		
		MSG msg = new MSG();
		try {
			
			while (!quit) {
				User32.INSTANCE.PeekMessage(msg, null, 0, 0, 1);
				if (msg.message == WM_COPYDATA){ // && msg.wParam.intValue() == 1
					System.out.println("WM_COPYDATA");
					msg = new MSG(); //must clear msg so it doesn't repeat
				}
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("message loop stopped");
		unhook();
	}
	
	public void unhook() {
		if (hHook == null)
			return;
	    if (!User32.INSTANCE.UnhookWindowsHookEx(hHook))
	    	System.out.println("Failed to unhook");
	    System.out.println("Unhooked");
	    hHook = null;
	}
	
	//stops Keyboard hook and causes the unhook command to be called
	public static void stopMessageHook() {
		quit = true;
	}
}
