/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;
import java.io.*;

import javax.swing.JLabel;

public class TestIdeFrame extends JFrame {

	public static String RES_STR_MAIN_ICON = "/org/synthuse/img/applications-education.png";
	public static String RES_STR_STOP_IMG = "/org/synthuse/img/dialog-close.png";
	public static String RES_STR_RUN_IMG = "/org/synthuse/img/arrow-right-3.png";
	public static String RES_STR_CLEAR_IMG = "/org/synthuse/img/user-trash-2.png";
	public static String RES_STR_COPY_IMG = "/org/synthuse/img/edit-copy-7.png";
	public static String RES_STR_SAVE_IMG = "/org/synthuse/img/document-save-6.png";
	public static String RES_STR_OPEN_IMG = "/org/synthuse/img/document-open-folder.png";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public JTextArea txtTest;
	private JButton btnRun;
	private JButton btnClear;
	private JButton btnCopy;
	private JButton btnSave;
	private JButton btnOpen;
	private JLabel lblStatus;

	/**
	 * Create the frame.
	 */
	
	public TestIdeFrame() {
		setTitle("Test IDE - Synthuse");
		setIconImage(Toolkit.getDefaultToolkit().getImage(TestIdeFrame.class.getResource(RES_STR_MAIN_ICON)));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 367);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runTestScript();
			}
		});
		btnRun.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_RUN_IMG)));
		toolBar.add(btnRun);
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveTestScript();
			}
		});
		btnSave.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_SAVE_IMG)));
		toolBar.add(btnSave);
		
		btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openTestScript();
			}
		});
		btnOpen.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_OPEN_IMG)));
		toolBar.add(btnOpen);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtTest.setText("");
			}
		});
		btnClear.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_CLEAR_IMG)));
		toolBar.add(btnClear);
		
		btnCopy = new JButton("Copy Script");
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StringSelection stringSelection = new StringSelection(txtTest.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
				//StatusWindow sw = new StatusWindow("this is a test BLAH really long string goes here to test status of stuff yaya!!!!123123 123 123", 4000);
			}
		});
		btnCopy.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_COPY_IMG)));
		toolBar.add(btnCopy);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		txtTest = new JTextArea();
		txtTest.setText("Click the Run button above to test the script below...\r\n\r\n");
		scrollPane.setViewportView(txtTest);
		
		lblStatus = new JLabel(" ");
		contentPane.add(lblStatus, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//TestIdeFrame.this.setVisible(false);
				TestIdeFrame.this.dispose();
			}
		});
		super.setAlwaysOnTop(SynthuseDlg.config.isAlwaysOnTop());
	}
	
	public void runTestScript()
	{
		if (btnRun.getText().equals("Run")) {
			btnRun.setText("Stop");
			btnRun.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_STOP_IMG)));
			CommandProcessor.STOP_PROCESSOR.set(false);
			CommandProcessor.executeThreaded(txtTest.getText(), new CommandProcessor.Events() {
				@Override
				public void statusChanged(String status) {
					lblStatus.setText(status);
				}
				@Override
				public void executionCompleted() {
					btnRun.setText("Run");
					btnRun.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_RUN_IMG)));
				}
			});
		}
		else {
			CommandProcessor.STOP_PROCESSOR.set(true);
			//btnRun.setText("Run");
			//btnRun.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_RUN_IMG)));
		}
	}
	
	private void saveTestScript()
	{
		JFileChooser fChoose = new JFileChooser();
		fChoose.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
		int result = fChoose.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		File file = fChoose.getSelectedFile();
		if (fChoose.getFileFilter().getDescription().startsWith("Text") && !file.getAbsolutePath().toLowerCase().endsWith(".txt"))
			file = new File(file.getAbsolutePath() + ".txt"); //append extension if not already there
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(txtTest.getText());
			fw.flush();
			fw.close();
			fw = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fw != null)
			try { fw.close(); } catch (Exception e){ e.printStackTrace(); };
		lblStatus.setText("Script Saved: " + file.getAbsolutePath());
	}
	
	private void openTestScript()
	{
		JFileChooser fChoose = new JFileChooser();
		fChoose.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
		int result = fChoose.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		File file = fChoose.getSelectedFile();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
	        String line = "";
	        txtTest.setText("");
	        while((line = br.readLine()) != null){
	        	txtTest.append(line + System.getProperty("line.separator"));
	            //System.out.println(line);
	        }
	        br.close();
	        br = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (br != null)
			try { br.close(); } catch (Exception e){ e.printStackTrace(); };
			lblStatus.setText("Script Loaded: " + file.getAbsolutePath());
	}

}
