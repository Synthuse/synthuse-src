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
import javax.swing.ImageIcon;
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

import javax.swing.JLabel;

public class TestIdeFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public JTextArea txtTest;
	private JButton btnRun;
	private JButton btnClear;
	private JButton btnCopy;
	private JLabel lblStatus;

	/**
	 * Create the frame.
	 */
	
	public TestIdeFrame() {
		setTitle("Test IDE - Synthuse");
		setIconImage(Toolkit.getDefaultToolkit().getImage(TestIdeFrame.class.getResource("/org/qedsys/synthuse/applications-education.png")));
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
				if (btnRun.getText().equals("Run")) {
					btnRun.setText("Stop");
					btnRun.setIcon(new ImageIcon(SynthuseDlg.class.getResource("/org/qedsys/synthuse/dialog-close.png")));
					CommandProcessor.STOP_PROCESSOR.set(false);
					CommandProcessor.executeThreaded(txtTest.getText(), new CommandProcessor.Events() {
						@Override
						public void statusChanged(String status) {
							lblStatus.setText(status);
						}
						@Override
						public void executionCompleted() {
							btnRun.setText("Run");
							btnRun.setIcon(new ImageIcon(SynthuseDlg.class.getResource("/org/qedsys/synthuse/arrow-right-3.png")));
						}
					});
				}
				else {
					CommandProcessor.STOP_PROCESSOR.set(true);
					//btnRun.setText("Run");
					//btnRun.setIcon(new ImageIcon(SynthuseDlg.class.getResource("/org/qedsys/synthuse/arrow-right-3.png")));
				}
			}
		});
		btnRun.setIcon(new ImageIcon(SynthuseDlg.class.getResource("/org/qedsys/synthuse/arrow-right-3.png")));
		toolBar.add(btnRun);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtTest.setText("");
			}
		});
		btnClear.setIcon(new ImageIcon(SynthuseDlg.class.getResource("/org/qedsys/synthuse/user-trash-2.png")));
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
		btnCopy.setIcon(new ImageIcon(SynthuseDlg.class.getResource("/org/qedsys/synthuse/edit-copy-7.png")));
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
	}

}
