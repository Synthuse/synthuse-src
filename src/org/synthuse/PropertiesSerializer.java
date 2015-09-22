/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.io.*;
import java.util.Properties;
import java.lang.reflect.Field; 


/*

// example class for PropertiesSerializer
public class Configuration extends PropertiesSerializer{
	
	public static final String DEFAULT_PROP_FILENAME = "./ctf.properties";
	
	// General Settings
	public String tests_dir = "./tests";
	public String logs_dir = "./logs";
	public int statusTimer = 2000;

	public Configuration() //needed for cloning
	{
	}
	
	public Configuration(String propertyFilename) 
	{
		super(propertyFilename);
		load(propertyFilename);
	}
}

 */

public class PropertiesSerializer {

	protected Properties prop = new Properties();
	protected String propertyFilename = null;
	
	public PropertiesSerializer()
	{		
	}
	
	public PropertiesSerializer(String propertyFilename)
	{
		this.propertyFilename = propertyFilename;
	}
	
	public void load(String propertyFilename)
	{
		try 
		{
			prop.load(new FileInputStream(propertyFilename));
		}
		catch (Exception e) 
		{
			System.out.println("Unable to load properties from file: "+propertyFilename+". Default values will be used.");
			return;
		}

		Field[] fields = this.getClass().getFields();
		for (int i = 0 ; i < fields.length; i++)
		{
			String pName = fields[i].getName();
			String pType = "String";
			try 
			{
				pType = fields[i].get(this).getClass().getSimpleName();
			}
			catch (Exception e) 
			{
//				e.printStackTrace();
			}
			final Object myProperty = prop.get(pName);
			try 
			{
				if(myProperty==null) {
					System.out.println("Property "+pName+"["+pType+"] not set; input was null");
				} else {
					if (pType.equalsIgnoreCase("integer"))
						fields[i].set(this, Integer.parseInt(myProperty + ""));
					if (pType.equalsIgnoreCase("boolean"))
						fields[i].set(this, Boolean.parseBoolean(myProperty + ""));
					else
						fields[i].set(this, myProperty);
					System.out.println("Property "+pName+"["+pType+"] set to: "+myProperty);
				}
			} 
			catch (Exception e) 
			{
//				e.printStackTrace();
			}
		}
	}
	
	public void save()
	{
		Field[] fields = this.getClass().getFields();
		for (int i = 0 ; i < fields.length; i++)
		{
			//fields[i].get(this);
			try {
				String pName = fields[i].getName();
				//String pType = fields[i].get(this).getClass().getSimpleName();
				if (fields[i].get(this) == null)
					prop.setProperty(pName, "");
				else
					prop.setProperty(pName, fields[i].get(this) + "");
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		try 
		{
			FileOutputStream fos = new FileOutputStream(propertyFilename);
			prop.store(fos, "");
			fos.flush();
			fos.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object clone()
	{
		Object newObject = null;
		try {
			newObject = (Object)this.getClass().newInstance();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		Field[] fields = this.getClass().getFields();
		for (int i = 0 ; i < fields.length; i++)
		{
			try {
				//fields[i].get(this);
				//String pName = fields[i].getName();
				fields[i].set(newObject, fields[i].get(this));
			}
			catch (Exception e) 
			{
				//e.printStackTrace();
			}
		}
		return newObject;
	}
	
	public boolean hasChanged()
	{
		boolean changes = false;
		Field[] fields = this.getClass().getFields();
		for (int i = 0 ; i < fields.length; i++)
		{
			//fields[i].get(this);
			try {
				String pName = fields[i].getName();
				//String pType = fields[i].get(this).getClass().getSimpleName();
				if (prop.getProperty(pName).compareTo(fields[i].get(this)+"") != 0)
					changes = true;
				
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		return changes;
	}
	
	public String getPropertyFilename()
	{
		return this.propertyFilename;
	}
	
	public void setPropertyFilename(String filename)
	{
		this.propertyFilename = filename;
	}
	
	public String readValue(String propertyName)
	{
		String val = "";
		val = prop.getProperty(propertyName);
		return val;
	}
	
	public void writeValue(String propertyName, String propertValue)
	{
		prop.setProperty(propertyName, propertValue);
		try {
			prop.store(new FileOutputStream(propertyFilename), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

