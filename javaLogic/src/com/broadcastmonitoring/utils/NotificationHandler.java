package com.broadcastmonitoring.utils;

import java.util.concurrent.Callable;

import com.broadcastmonitoring.utils.StreamGobbler;

public class NotificationHandler implements Callable<String>
{
	private String summary;
	private String message;
	public NotificationHandler(String summary,String body) 
	{
		this.summary=summary;
		this.message=body;
	}

	@Override
	public String call() throws Exception
	{
		try
		{
			
			String command="notify-send "+summary+" "+message;
			final Process notificationProcess=Runtime.getRuntime().exec(command);
			
			StreamGobbler errorGobbler=new StreamGobbler(notificationProcess.getErrorStream(), "ERROR", true);
			StreamGobbler outputGobbler=new StreamGobbler(notificationProcess.getInputStream(), "OUTPUT", true);
			errorGobbler.start();
			outputGobbler.start();
			
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
