/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

public class Config extends PropertiesSerializer {

	public static String DEFAULT_PROP_FILENAME = "synthuse.properties";
	
	public String urlList = "";
	public String xpathList = "";
	public String xpathHightlight = ".*process=\"([^\"]*)\".*";
	
	public Config() //needed for cloning
	{
	}
	
	public Config(String propertyFilename) 
	{
		super(propertyFilename);
		load(propertyFilename);
	}
}
