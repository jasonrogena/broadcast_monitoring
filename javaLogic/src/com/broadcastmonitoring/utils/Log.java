package com.broadcastmonitoring.utils;

import javafx.beans.property.SimpleStringProperty;

public class Log 
{
	private final SimpleStringProperty time;
	private final SimpleStringProperty message;
	private final SimpleStringProperty tag;
	public Log(String time, String tag, String message) 
	{
		this.time=new SimpleStringProperty(time);
		this.message=new SimpleStringProperty(message);
		this.tag=new SimpleStringProperty(tag);
	}
	
	public String getMessage()
	{
		return this.message.get();
	}
	
	public void setMessage(String message)
	{
		this.message.set(message);
	}
	
	public String getTag()
	{
		return this.tag.get();
	}
	
	public void setTag(String tag)
	{
		this.tag.set(tag);
	}
	
	public String getTime()
	{
		return this.time.get();
	}
	
	public void setTime(String time)
	{
		this.time.set(time);
	}

}
