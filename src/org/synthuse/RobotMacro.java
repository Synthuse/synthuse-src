/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.*;
import java.awt.event.*;

public class RobotMacro {
	
	public static Exception lastException = null;

	public static boolean executeCommand(String cmd) {
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(cmd);
		} catch (Exception e) {
			lastException = e;
			return false;
		}
		return true;
	}
	
	public static void delay(int time) {
		try {
			Robot robot = new Robot();
			robot.delay(time);
		} catch (Exception e) {
			lastException = e;
		}
	}

	public static void moveMouse(int x, int y) {
		try {
			Robot robot = new Robot();
			robot.mouseMove(x, y);
		} catch (Exception e) {
			lastException = e;
		}
	}	

	public static void leftClickMouse() {
		try {
			Robot robot = new Robot();
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (Exception e) {
			lastException = e;
		}
	}		

	public static void doubleClickMouse() {
		try {
			Robot robot = new Robot();
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.delay(100);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (Exception e) {
			lastException = e;
		}
	}		

	public static void rightClickMouse() {
		try {
			//System.out.println("rightClickMouse");
			Robot robot = new Robot();
			//robot.mouseMove(200, 200);
			//robot.delay(1000);
			robot.mousePress(InputEvent.BUTTON3_MASK);
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
			//System.out.println("rightClickMouse good");
		} catch (Exception e) {
			lastException = e;
			e.printStackTrace();
		}
	}
	
	public static void leftMouseDown() {
		try {
			Robot robot = new Robot();
			robot.mousePress(InputEvent.BUTTON1_MASK);
		} catch (Exception e) {
			lastException = e;
		}
	}
	
	public static void leftMouseUp() {
		try {
			Robot robot = new Robot();
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (Exception e) {
			lastException = e;
		}
	}
	
	public static void rightMouseDown() {
		try {
			Robot robot = new Robot();
			robot.mousePress(InputEvent.BUTTON3_MASK);
		} catch (Exception e) {
			lastException = e;
		}
	}
	
	public static void rightMouseUp() {
		try {
			Robot robot = new Robot();
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
		} catch (Exception e) {
			lastException = e;
			e.printStackTrace();
		}
	}
	
	public static void mouseMove(int x, int y) {
		try {
			Robot robot = new Robot();
			robot.mouseMove(x, y);
		} catch (Exception e) {
			lastException = e;
			e.printStackTrace();
		}
	}	
	
	public static void copyKey() {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_C);
			robot.keyRelease(KeyEvent.VK_C);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (Exception e) {
			lastException = e;
		}
	}
	
	public static void pasteKey() {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (Exception e) {
			lastException = e;
		}
	}

	public static void escapeKey() {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ESCAPE);
			robot.keyRelease(KeyEvent.VK_ESCAPE);
		} catch (Exception e) {
			lastException = e;
		}
	}
	
	public static void functionKey(int functionNum) {
		try {
			Robot robot = new Robot();
			int keyCode = 0;
	    	switch (functionNum) {
		    	case 1: keyCode = KeyEvent.VK_F1; break;
		    	case 2: keyCode = KeyEvent.VK_F2; break;
		    	case 3: keyCode = KeyEvent.VK_F3; break;
		    	case 4: keyCode = KeyEvent.VK_F4; break;
		    	case 5: keyCode = KeyEvent.VK_F5; break;
		    	case 6: keyCode = KeyEvent.VK_F6; break;
		    	case 7: keyCode = KeyEvent.VK_F7; break;
		    	case 8: keyCode = KeyEvent.VK_F8; break;
		    	case 9: keyCode = KeyEvent.VK_F9; break;
		    	case 10: keyCode = KeyEvent.VK_F10; break;
		    	case 11: keyCode = KeyEvent.VK_F11; break;
		    	case 12: keyCode = KeyEvent.VK_F12; break;
	    	}
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
		} catch (Exception e) {
			lastException = e;
		}
	}

	public static void tildeKey() {
		try {
			Robot robot = new Robot();
			pressKeyCodes(robot, new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE});
		} catch (Exception e) {
			lastException = e;
		}
	}
/*	 SendKeys Special Keys List
{BACKSPACE}, {BS}, or {BKSP}
{BREAK}
{CAPSLOCK}
{DELETE} or {DEL}
{DOWN}
{END}
{ENTER} or ~
{ESC}
{HELP}
{HOME}
{INSERT} or {INS}
{LEFT}
{NUMLOCK}
{PGDN}
{PGUP}
{PRTSC} (reserved for future use)
{RIGHT}
{SCROLLLOCK}
{TAB}
{UP}
{F1}
{F2}
{F3}
{F4}
{F5}
{F6}
{F7}
{F8}
{F9}
{F10}
{F11}
{F12}
{F13}
{F14}
{F15}
{F16}
{ADD}
{SUBTRACT}
{MULTIPLY}
{DIVIDE}
{{}
{}}
SHIFT +
CTRL ^
ALT %  
	 
	 */
	
	public static boolean sendKeys(String keyCommands) {
		try {
			Robot robot = new Robot();
			boolean specialKeyFlag = false;
			String specialKey = "";
			boolean modifierKeyFlag = false;
			String modifierKeys = "";
			for (int i = 0; i < keyCommands.length(); i++) {
	    		char key = keyCommands.charAt(i);
	    		if (specialKeyFlag)
	    			specialKey += key;
				if (key == '{' && specialKeyFlag == false) {
					specialKeyFlag = true;
					specialKey = "{";
				}
				
				if (!specialKeyFlag) { //not special key(tab,enter,...) just press normal keys and modifiers
					// Modifier key logic
					if (key == '+' || key == '^' || key == '%') { //shift alt or ctrl
						if (!modifierKeyFlag) {
							modifierKeys = key + "";
							modifierKeyFlag = true;
						}
						else
							modifierKeys += key + ""; //append multiple modifiers
						if (key == '+')
							robot.keyPress(KeyEvent.VK_SHIFT);
						if (key == '^')
							robot.keyPress(KeyEvent.VK_CONTROL);
						if (key == '%')
							robot.keyPress(KeyEvent.VK_ALT);
						continue; //skip to next key
					}
		    		pressKeyCodes(robot, getKeyCode(key));
				}
				if (specialKeyFlag) {
		    		if (specialKey.equals("{ENTER}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_ENTER} );
		    		}
		    		else if (specialKey.equals("{ESC}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_ESCAPE} );
		    		}
		    		else if (specialKey.equals("{HOME}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_HOME} );
		    		}
		    		else if (specialKey.equals("{END}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_END} );
		    		}
		    		else if (specialKey.equals("{PGDN}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_PAGE_DOWN} );
		    		}
		    		else if (specialKey.equals("{PGUP}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_PAGE_UP} );
		    		}
		    		else if (specialKey.equals("{TAB}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_TAB} );
		    		}
		    		else if (specialKey.equals("{UP}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_UP} );
		    		}
		    		else if (specialKey.equals("{DOWN}")) {
		    			specialKeyFlag = false;
		    			pressKeyCodes(robot, new int[]{KeyEvent.VK_DOWN} );
		    		}
		    	}
	    		
	    		if (modifierKeyFlag) { //time to release all the modifier keys
	    			modifierKeyFlag = false;
	    			for (int m = 0; m < modifierKeys.length(); m++) {
	    				char mkey = modifierKeys.charAt(m);
						if (mkey == '+')
							robot.keyRelease(KeyEvent.VK_SHIFT);
						if (mkey == '^')
							robot.keyRelease(KeyEvent.VK_CONTROL);
						if (mkey == '%')
							robot.keyRelease(KeyEvent.VK_ALT);
	    			}
	    			modifierKeys = "";
	    		}
	    	}
		} catch (Exception e) {
			lastException = e;
			return false;
		}
		return true;
	}
	
	public static boolean pressKey(char key) {
		try {
			Robot robot = new Robot();
			int[] keyCode = getKeyCode(key);
			pressKeyCodes(robot, keyCode);
		} catch (Exception e) {
			lastException = e;
			return false;
		}
		return true;
	}
	
	public static boolean keyDown(char key) {
		try {
			Robot robot = new Robot();
			int[] keyCodes = getKeyCode(key);
			for (int i = 0; i < keyCodes.length; i++) {
				robot.keyPress(keyCodes[i]);
				//System.out.println("pressed: " + keyCodes[i]);
			}
		} catch (Exception e) {
			lastException = e;
			return false;
		}
		return true;
	}
	
	public static boolean keyUp(char key) {
		try {
			Robot robot = new Robot();
			int[] keyCodes = getKeyCode(key);
			for (int i = keyCodes.length - 1; i >= 0; i--) {
				robot.keyRelease(keyCodes[i]);
				//System.out.println("released: " + keyCodes[i]);
			}
		} catch (Exception e) {
			lastException = e;
			return false;
		}
		return true;
	}
	
	public static void pressKeyCodes(Robot robot, int[] keyCodes) {
		for (int i = 0; i < keyCodes.length; i++) {
			robot.keyPress(keyCodes[i]);
			//System.out.println("pressed: " + keyCodes[i]);
		}
		//robot.delay(50);
		for (int i = keyCodes.length - 1; i >= 0; i--) {
			robot.keyRelease(keyCodes[i]);
			//System.out.println("released: " + keyCodes[i]);
		}
	}
	
	public static int[] getKeyCode(char key) {
    	switch (key) {
	    	case 'a': return(new int[]{KeyEvent.VK_A});
	    	case 'b': return(new int[]{KeyEvent.VK_B});
	    	case 'c': return(new int[]{KeyEvent.VK_C});
	    	case 'd': return(new int[]{KeyEvent.VK_D});
	    	case 'e': return(new int[]{KeyEvent.VK_E});
	    	case 'f': return(new int[]{KeyEvent.VK_F});
	    	case 'g': return(new int[]{KeyEvent.VK_G});
	    	case 'h': return(new int[]{KeyEvent.VK_H});
	    	case 'i': return(new int[]{KeyEvent.VK_I});
	    	case 'j': return(new int[]{KeyEvent.VK_J});
	    	case 'k': return(new int[]{KeyEvent.VK_K});
	    	case 'l': return(new int[]{KeyEvent.VK_L});
	    	case 'm': return(new int[]{KeyEvent.VK_M});
	    	case 'n': return(new int[]{KeyEvent.VK_N});
	    	case 'o': return(new int[]{KeyEvent.VK_O});
	    	case 'p': return(new int[]{KeyEvent.VK_P});
	    	case 'q': return(new int[]{KeyEvent.VK_Q});
	    	case 'r': return(new int[]{KeyEvent.VK_R});
	    	case 's': return(new int[]{KeyEvent.VK_S});
	    	case 't': return(new int[]{KeyEvent.VK_T});
	    	case 'u': return(new int[]{KeyEvent.VK_U});
	    	case 'v': return(new int[]{KeyEvent.VK_V});
	    	case 'w': return(new int[]{KeyEvent.VK_W});
	    	case 'x': return(new int[]{KeyEvent.VK_X});
	    	case 'y': return(new int[]{KeyEvent.VK_Y});
	    	case 'z': return(new int[]{KeyEvent.VK_Z});
	    	case 'A': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_A});
	    	case 'B': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_B});
	    	case 'C': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_C});
	    	case 'D': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_D});
	    	case 'E': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_E});
	    	case 'F': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_F});
	    	case 'G': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_G});
	    	case 'H': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_H});
	    	case 'I': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_I});
	    	case 'J': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_J});
	    	case 'K': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_K});
	    	case 'L': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_L});
	    	case 'M': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_M});
	    	case 'N': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_N});
	    	case 'O': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_O});
	    	case 'P': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_P});
	    	case 'Q': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Q});
	    	case 'R': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_R});
	    	case 'S': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_S});
	    	case 'T': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_T});
	    	case 'U': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_U});
	    	case 'V': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_V});
	    	case 'W': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_W});
	    	case 'X': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_X});
	    	case 'Y': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Y});
	    	case 'Z': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Z});
	    	case '`': return(new int[]{KeyEvent.VK_BACK_QUOTE});
	    	case '0': return(new int[]{KeyEvent.VK_0});
	    	case '1': return(new int[]{KeyEvent.VK_1});
	    	case '2': return(new int[]{KeyEvent.VK_2});
	    	case '3': return(new int[]{KeyEvent.VK_3});
	    	case '4': return(new int[]{KeyEvent.VK_4});
	    	case '5': return(new int[]{KeyEvent.VK_5});
	    	case '6': return(new int[]{KeyEvent.VK_6});
	    	case '7': return(new int[]{KeyEvent.VK_7});
	    	case '8': return(new int[]{KeyEvent.VK_8});
	    	case '9': return(new int[]{KeyEvent.VK_9});
	    	case '-': return(new int[]{KeyEvent.VK_MINUS});
	    	case '=': return(new int[]{KeyEvent.VK_EQUALS});
	    	case '~': return(new int[]{KeyEvent.VK_ENTER});//return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE});
	    	case '!': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_1});
	    	case '@': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_2});
	    	case '#': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_3});
	    	case '$': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_4});
	    	case '%': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_5});
	    	case '^': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_6});
	    	case '&': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_7});
	    	case '*': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_8});
	    	case '(': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_9});
	    	case ')': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_0});
	    	case '_': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS});
	    	case '+': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS});
	    	case '\t': return(new int[]{KeyEvent.VK_TAB});
	    	case '\n': return(new int[]{KeyEvent.VK_ENTER});
	    	case '[': return(new int[]{KeyEvent.VK_OPEN_BRACKET});
	    	case ']': return(new int[]{KeyEvent.VK_CLOSE_BRACKET});
	    	case '\\': return(new int[]{KeyEvent.VK_BACK_SLASH});
	    	case '{': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET});
	    	case '}': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET});
	    	case '|': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH});
	    	case ';': return(new int[]{KeyEvent.VK_SEMICOLON});
	    	case ':': return(new int[]{KeyEvent.VK_COLON});
	    	case '\'': return(new int[]{KeyEvent.VK_QUOTE});
	    	case '"': return(new int[]{KeyEvent.VK_QUOTEDBL});
	    	case ',': return(new int[]{KeyEvent.VK_COMMA});
	    	case '<': return(new int[]{KeyEvent.VK_LESS});
	    	case '.': return(new int[]{KeyEvent.VK_PERIOD});
	    	case '>': return(new int[]{KeyEvent.VK_GREATER});
	    	case '/': return(new int[]{KeyEvent.VK_SLASH});
	    	case '?': return(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH}); // needs Shift
	    	case ' ': return(new int[]{KeyEvent.VK_SPACE});
	    	default:
	    		throw new IllegalArgumentException("Cannot find Key Code for character " + key);
    	}
	}
}
