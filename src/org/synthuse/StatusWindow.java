/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JWindow;


public class StatusWindow extends JWindow {

	private static final long serialVersionUID = 1L;
	public static int FONT_SIZE = 14;
	public static int FONT_BOLD = Font.BOLD; //Font.PLAIN
	public static int Y_BOTTOM_OFFSET = -100;
	public static Color BACKGROUND_COLOR = Color.yellow;
	public static Color FOREGROUND_COLOR = Color.black;
	//private int displayTime = -1;
	//private String displayText = "";
	public StatusWindow(String lblText, int displayTime) {
		super();
		//this.displayTime = displayTime;
		//this.displayText = lblText;
		//this.setLayout(new FlowLayout());
		JLabel lbl = new JLabel(lblText);
		lbl.setFont(new Font(lbl.getName(), FONT_BOLD, FONT_SIZE));
		lbl.setOpaque(true); //background isn't painted without this
		lbl.setBackground(BACKGROUND_COLOR);
		lbl.setForeground(FOREGROUND_COLOR);
		this.getContentPane().setLayout(new FlowLayout());
		this.getContentPane().add(lbl);
		this.pack();
		this.setVisible(true);
		//
		if (displayTime > 0) {
			Timer timer = new Timer(true);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					StatusWindow.this.dispose();
				}
			}, displayTime);
		}
	}
	
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		// ...and bring window to the front.. in a strange and weird way
		if (visible) {
			super.setAlwaysOnTop(true);
			super.toFront();
			super.requestFocus();
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			//this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
			this.setLocation(dim.width/2-this.getSize().width/2, dim.height + Y_BOTTOM_OFFSET );
		}
	}

}
