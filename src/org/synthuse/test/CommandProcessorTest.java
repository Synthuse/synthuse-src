package org.synthuse.test;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.synthuse.*;

public class CommandProcessorTest {

	String goodTestScript1 = "| do | pause | on | 10 |";
	String goodTestScript2 = "| do | pause | on | 10 |\n| do | pause | on | 20 |\n";
	String goodTestScript3 = "|do|pause|on|8|\n|do|pause|on|16|\n";
	String goodTestScript4 = "| do|pause|on|8|\r\n|do|pause|on|16|\r\n";

	String badTestScript1 = "| do | pause | on | bob |\n";
	String badTestScript2 = "| do | pause | on | bob |\n| do | pause | on | joe |\n";

	CommandProcessor.Events testEvents = new CommandProcessor.Events() {
		@Override
		public void statusChanged(String status) {
		}
		@Override
		public void executionCompleted() {
		}
	};
	

	@Before
	public void setUp() throws Exception {
		CommandProcessor.DEFAULT_QUIET = true;
		CommandProcessor.SPEED = 0;
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void initCommandProcessor() {
		CommandProcessor commandProcessor = null;
		
		commandProcessor = new CommandProcessor("");
		assertEquals(commandProcessor.scriptStr, "");
		
		commandProcessor = new CommandProcessor(goodTestScript1);
		assertEquals(commandProcessor.scriptStr, goodTestScript1);
		
		//this script will be initialized and executed too
		commandProcessor = CommandProcessor.executeThreaded(goodTestScript2, testEvents);
		assertEquals(commandProcessor.scriptStr, goodTestScript2);
	}
	
	@Test
	public void ExecuteSingleThreadedValidScripts() {
		CommandProcessor commandProcessor = new CommandProcessor("", testEvents);
		commandProcessor.run();
		assertEquals(commandProcessor.getErrors(), 0);
		
		commandProcessor.setScript(goodTestScript1);
		assertEquals(commandProcessor.scriptStr, goodTestScript1);
		commandProcessor.run();
		assertEquals(commandProcessor.getErrors(), 0);
		
		commandProcessor.setScript(goodTestScript2);
		assertEquals(commandProcessor.scriptStr, goodTestScript2);
		commandProcessor.run();
		assertEquals(commandProcessor.getErrors(), 0);
		
		commandProcessor = new CommandProcessor(goodTestScript3, testEvents);
		commandProcessor.run();
		assertEquals(commandProcessor.getErrors(), 0);
	}

	@Test
	public void ExecuteMultiThreadedValidScripts() {
		CommandProcessor.executeThreaded("", testEvents);
		CommandProcessor commandProcessor = null;
		commandProcessor = CommandProcessor.executeThreaded(goodTestScript1, testEvents);
		assertEquals(commandProcessor.getErrors(), 0);
		commandProcessor = CommandProcessor.executeThreaded(goodTestScript2, testEvents);		
		assertEquals(commandProcessor.getErrors(), 0);
		commandProcessor = CommandProcessor.executeThreaded(goodTestScript3, testEvents);
		assertEquals(commandProcessor.getErrors(), 0);
		//CommandProcessor.executeThreaded(goodTestScript3, null);	
	}
	
	@Test
	public void ExecuteSingleThreadedBadScripts() {
		//undefined command
		CommandProcessor commandProcessor = new CommandProcessor("| do | xasdffds |", testEvents);
		commandProcessor.setQuiet(true);
		commandProcessor.run();
		//System.out.println(commandProcessor.lastError);
		//2014-03-25 10:19:37.54 - Error: Command 'xasdffds' not found.
		assertEquals(commandProcessor.getErrors(), 1);

		commandProcessor.setScript(badTestScript1);
		commandProcessor.setQuiet(true);
		commandProcessor.run();
		//System.out.println(commandProcessor.lastError);
		assertEquals(commandProcessor.getErrors(), 1);
		assertTrue(commandProcessor.lastError.length() > 1);
		
		commandProcessor.setScript(badTestScript2);
		commandProcessor.setQuiet(true);
		assertEquals(commandProcessor.scriptStr, badTestScript2);
		commandProcessor.run();
		//System.out.println(commandProcessor.lastError);
		assertEquals(commandProcessor.getErrors(), 2);
		assertTrue(commandProcessor.lastError.length() > 1);

	}
	
	// test running mulithreaded bad scripts
	
	// test parsing bars, and arguments that contain values with bars
	
	// test command sections ie keyboard, mouse, win, main, base
}
