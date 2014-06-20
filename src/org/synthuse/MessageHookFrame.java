/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/

// This class is not actually used and is only here as a reference

package org.synthuse;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.WindowConstants;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.ptr.IntByReference;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;


public class MessageHookFrame extends JFrame {
	
	private static final long serialVersionUID = -5863279004595502801L;
	
	public static final String newLine = System.getProperty("line.separator");
	
	public static final int WH_CALLWNDPROC = 4;
	public static final int WH_GETMESSAGE = 3;
	public static final int WH_KEYBOARD_LL = 13;
	
	public static final int WM_COPYDATA = 74;
	public static final int GWLP_WNDPROC = -4;
	
	private JTextArea textArea;
	private JButton btnSave;
	private JButton btnStartMsgHook;
	private JButton btnPause;
	private JButton btnClear;
	private LONG_PTR oldWndProc;
	private MsgHook msgHook = null;
	
	public static volatile boolean quit = false;
	private JLabel lblTargetHwnd;
	public JTextField txtTarget;
	private Component horizontalStrut;
	
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
		
		lblTargetHwnd = new JLabel("Target HWND: ");
		toolBar.add(lblTargetHwnd);
		
		txtTarget = new JTextField();
		txtTarget.setMaximumSize(new Dimension(70, 2147483647));
		txtTarget.setText("0");
		toolBar.add(txtTarget);
		txtTarget.setColumns(10);
		
		horizontalStrut = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut);
		toolBar.add(btnStartMsgHook);
		
		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				final HWND myHwnd = new HWND(Native.getWindowPointer(MessageHookFrame.this));
				oldWndProc = User32.INSTANCE.GetWindowLongPtr(myHwnd, GWLP_WNDPROC);
				
				Api.WNDPROC wndProc = new Api.WNDPROC() {
			        public LRESULT callback(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam) {
						if (uMsg == WM_COPYDATA){
							System.out.println("WM_COPYDATA");
			                    //handle the window message here
						}
						else
							System.out.println("MESSAGE: " + uMsg);
						return Api.User32Ex.instance.CallWindowProc(oldWndProc, hWnd, uMsg, wParam, lParam);
						//return new LRESULT(0);
			            //return User32.INSTANCE.DefWindowProc(hWnd, uMsg, wParam, lParam);
			        }
			    };

				Api.User32Ex.instance.SetWindowLongPtr(myHwnd, GWLP_WNDPROC, wndProc);
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
		
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//TestIdeFrame.this.setVisible(false);
				MessageHookFrame.this.dispose();
			}
		});
		
		super.setAlwaysOnTop(SynthuseDlg.config.isAlwaysOnTop());
		
	}
	/*
	typedef struct
	{
		HWND hWnd;
		int nCode;
		DWORD dwHookType;
		WPARAM wParam;
		LPARAM lParam;
		TCHAR wParamStr[25];
		TCHAR lParamStr[25];
	}HEVENT;
	*/
	
    public static class HEVENT extends Structure {
    	//The by-reference version of this structure.
        public static class ByReference extends HEVENT implements Structure.ByReference { }
        
        public HEVENT() { }

        //Instantiates a new COPYDATASTRUCT with existing data given the address of that data.
        public HEVENT(final long pointer) {
            this(new Pointer(pointer));
        }

        //Instantiates a new COPYDATASTRUCT with existing data given a pointer to that data.
        public HEVENT(final Pointer memory) {
            super(memory);
            read();
        }

		public WORD hWnd;
		public WORD nCode;
		public DWORD dwHookType;
		public DWORD wParam;
		public DWORD lParam;
		//public TCHAR wParamStr[25];
		//public TCHAR lParamStr[25];
		
        @SuppressWarnings("rawtypes")
		@Override
        protected final List getFieldOrder() {
            return Arrays.asList(new String[] {"hWnd", "nCode", "dwHookType", "wParam", "lParam" });
        }
    }


	public void createMessageHook() {
		
		quit = false; //don't quit
		
		//find the HWND and current WNDPROC on this java window
		final HWND myHwnd = new HWND(Native.getWindowPointer(MessageHookFrame.this));
		oldWndProc = User32.INSTANCE.GetWindowLongPtr(myHwnd, GWLP_WNDPROC);
		
		Api.WNDPROC wndProc = new Api.WNDPROC() {
	        public LRESULT callback(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam) {
				if (uMsg == WM_COPYDATA){
					//(COPYDATASTRUCT *) lParam
					//if( pCopyDataStruct->cbData == sizeof(HEVENT)) {
					//	HEVENT Event;
					//	memcpy(&Event, (HEVENT*)pCopyDataStruct->lpData, sizeof(HEVENT)); // transfer data to internal variable
					//}

					Api.WinDefEx.COPYDATASTRUCT cds = new Api.WinDefEx.COPYDATASTRUCT(lParam.longValue());
					HEVENT he = new HEVENT(cds.lpData);
					appendLine("msg: WM_COPYDATA" + cds.cbData);
					appendLine("hwnd: " + he.hWnd + ", msg: " + he.nCode + ", wParam: " + he.wParam + ", lParam: " + he.lParam);
					//System.out.println("WM_COPYDATA");
	                    //handle the window message here
				}
				//else
				//	System.out.println("MESSAGE: " + uMsg);
				
				return Api.User32Ex.instance.CallWindowProc(oldWndProc, hWnd, uMsg, wParam, lParam);
	            //return User32.INSTANCE.DefWindowProc(hWnd, uMsg, wParam, lParam);
	        }
	    };
	    
	    //Set the wndproc callback on this MessageHookFrame so we can process Windows Messages
		Api.User32Ex.instance.SetWindowLongPtr(myHwnd, GWLP_WNDPROC, wndProc);
	
		IntByReference intByRef = new IntByReference(0);
		final int threadId = User32.INSTANCE.GetWindowThreadProcessId(Api.GetHandleFromString(txtTarget.getText()), intByRef);
				
		//int myPid = Kernel32.INSTANCE.GetCurrentProcessId();
		//HWND myHwnd = Api.FindMainWindowFromPid(myPid);
		final long myHwndLong = Api.GetHandleAsLong(myHwnd);
				
		//System.out.println("Starting Msg Hook for " + myHwndLong + " on id " + threadId);
		if (threadId == 0 ) // don't allow global
		{
			System.out.println("Not allowing global message hook " + threadId);
			User32.INSTANCE.SetWindowLongPtr(myHwnd, GWLP_WNDPROC, oldWndProc); //restore default WNDPROC
			quit = true;
			return;
		}
	
		msgHook = new MsgHook();
		if (!msgHook.setMessageHook((int) myHwndLong, threadId))
			appendLine("Error setting message hook");
		else
			appendLine("Message hook started");
				
	}
	
	public void unhook(MsgHook msgHook) {
		msgHook.removeMessageHook();
	}
	
	//stops Keyboard hook and causes the unhook command to be called
	public void stopMessageHook() {
		//if (!quit) //if not hooked skip
		//	return;
		quit = true;
		appendLine("Message hook stopped");
		final HWND myHwnd = new HWND(Native.getWindowPointer(MessageHookFrame.this));
		User32.INSTANCE.SetWindowLongPtr(myHwnd, GWLP_WNDPROC, oldWndProc); //restore default WNDPROC
		unhook(msgHook);
	}
	
	public void appendLine(String txt)
	{
		textArea.append(txt + newLine);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
