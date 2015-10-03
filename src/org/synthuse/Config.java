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
	
	public boolean useStrongTextMatching = false; 

	
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
	

	public void setDisableUiaBridge(boolean aNewValue) {
		disableUiaBridge=aNewValue?"true":"false";
	}

	
	public boolean isFilterUiaDisabled()
	{
		if (disableFiltersUia == null)
			return false;
		return disableFiltersUia.equals("true") || disableFiltersUia.equals("True");
	}
	
	public void setDisableFiltersUia(boolean aNewValue) {
		disableFiltersUia=aNewValue?"true":"false";
	}

	
	public boolean isAlwaysOnTop()
	{
		if (alwaysOnTop == null)
			return new Config().alwaysOnTop.equals("true") || new Config().alwaysOnTop.equals("True");
		return alwaysOnTop.equals("true") || alwaysOnTop.equals("True");
	}
	
	public void setAlwaysOnTop(boolean aNewValue)
	{
		alwaysOnTop=aNewValue?"true":"false";
	}
	
	public char getRefreshKey()
	{
		String keyStr = ""; 
		if (this.refreshKey == null)
			keyStr = new Config().refreshKey; //use default value
		else if (this.refreshKey.isEmpty())
			keyStr = new Config().refreshKey; //use default value
		else
			keyStr = this.refreshKey;
		return keyStr.charAt(0);
	}

	public int getRefreshKeyCode()
	{
		return RobotMacro.getKeyCode(this.getRefreshKey())[0];
	}

	public void setRefreshKey(String aText) {
		this.refreshKey=aText;
	}
	
	public char getTargetKey()
	{
		String keyStr = ""; 
		if (this.targetKey == null)
			keyStr = new Config().targetKey; //use default value
		else if (this.targetKey.isEmpty())
			keyStr = new Config().targetKey; //use default value
		else
			keyStr = this.targetKey;
		return keyStr.charAt(0);
	}

	public int getTargetKeyCode()
	{
		return RobotMacro.getKeyCode(this.getTargetKey())[0];
	}

	public void setTargetKey(String aText) {
		this.targetKey=aText;
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

	public void setXPathList(String aText) {
		xpathList=aText;
	}

	public String getXpathHighlight() {
		return xpathHightlight;
	}

	public void setXPathHighlight(String aText) {
		xpathHightlight=aText;
	}
}
