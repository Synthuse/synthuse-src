/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse.commands;

import java.io.*;
import org.synthuse.*;

public class FileCommands extends BaseCommand {

	public FileCommands(CommandProcessor cp) {
		super(cp);
	}
	
	public String cmdGrepFile(String[] args) throws Exception {
		if (!checkArgumentLength(args, 2))
			return null;
		String filename = args[0];
		String pattern = args[1];
		StringBuilder result = new StringBuilder("");
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(filename);
			DataInputStream dis = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(dis));
			String strLine = "";
			while ((strLine = br.readLine()) != null) {
				if (strLine.matches(pattern))
					result.append(strLine + "\n");
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			if (fis != null)
				fis.close();
			if (br != null)
				br.close();
	    }
		return result.toString();
	}
	
	public String cmdFileSearch(String[] args) {
		if (!checkArgumentLength(args, 2))
			return null;
		String path = args[0];
		String filenamePattern = args[1];
		StringBuilder result = new StringBuilder("");
		File parent = new File(path);
		for(File child : parent.listFiles()) {
			if (child.isFile() && child.getName().matches(filenamePattern))
				result.append(child.getAbsolutePath() + "\n");
			else if (child.isDirectory()) {
				result.append(cmdFileSearch(new String[] {child.getAbsolutePath(), filenamePattern}));
			}				
		}
		return result.toString();
	}

}
