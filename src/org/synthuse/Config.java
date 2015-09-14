/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

public class Config extends PropertiesSerializer {

	public static String DEFAULT_PROP_FILENAME = "synthuse.properties";
	
	public String disableUiaBridge = "false";
	public String disableFiltersUia = "false";
	public String alwaysOnTop = "true";
	public String refreshKey = "3";
	public String targetKey = "`";
	public String urlList = "";
	public String xpathList = "";
	public String xpathHightlight = ".*process=\"([^\"]*)\".*";
	
	private boolean useStrongTextMatching = false; 

	
	public Config() //needed for cloning
	{
	}
	
	public Config(String propertyFilename) 
	{
		super(propertyFilename);
		load(propertyFilename);
	}
	
	public boolean isUiaBridgeDisabled()
	{
		if (disableUiaBridge == null)
			return false;
		return disableUiaBridge.equals("true") || disableUiaBridge.equals("True");
	}
	
	public boolean isFilterUiaDisabled()
	{
		if (disableFiltersUia == null)
			return false;
		return disableFiltersUia.equals("true") || disableFiltersUia.equals("True");
	}
	
	public boolean isAlwaysOnTop()
	{
		if (alwaysOnTop == null)
			return new Config().alwaysOnTop.equals("true") || new Config().alwaysOnTop.equals("True");
		return alwaysOnTop.equals("true") || alwaysOnTop.equals("True");
	}
	
	public int getRefreshKeyCode()
	{
		String keyStr = ""; 
		if (this.refreshKey == null)
			keyStr = new Config().refreshKey; //use default value
		else if (this.refreshKey.isEmpty())
			keyStr = new Config().refreshKey; //use default value
		else
			keyStr = this.refreshKey;
		return RobotMacro.getKeyCode(keyStr.charAt(0))[0];
	}
	
	public int getTargetKeyCode()
	{
		String keyStr = ""; 
		if (this.targetKey == null)
			keyStr = new Config().targetKey; //use default value
		else if (this.targetKey.isEmpty())
			keyStr = new Config().targetKey; //use default value
		else
			keyStr = this.targetKey;
		return RobotMacro.getKeyCode(keyStr.charAt(0))[0];
	}

	public boolean isUseStrongTextMatching() {
		return useStrongTextMatching;
	}

	public void setUseStrongTextMatching(boolean useStrongTextMatching) {
		this.useStrongTextMatching = useStrongTextMatching;
	}

	public String getXpathList() {
		return xpathList;
	}

	public String getXpathHighlight() {
		return xpathHightlight;
	}
}
