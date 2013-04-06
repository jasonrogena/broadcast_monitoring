package com.broadcastmonitoring.searching;

import java.util.concurrent.Callable;

import com.broadcastmonitoring.utils.StreamGobbler;

public class MPlayerHandler implements Callable<String>
{
	private String url;
	
	public static final String MPLAYER_STOPPED="stopped";
	public static final String MPLAYER_RUNNING="running";
	
	public MPlayerHandler(String url) 
	{
		this.url=url;
	}

	@Override
	public String call() throws Exception
	{
		try
		{
			System.out.println("starting mplayer in silent mode");
			//String command="mplayer -slave -ao jack:name=MPlayer -af pan=1:0.5:0.5 -vo null -vc null "+url;
			String command="mplayer -slave -novideo "+url;
			//TODO: not sure if the client name in mplayer should be the same name in jackaudioserver
			final Process mPlayerProcess=Runtime.getRuntime().exec(command);
			
			StreamGobbler errorGobbler=new StreamGobbler(mPlayerProcess.getErrorStream(), "ERROR", true);
			StreamGobbler outputGobbler=new StreamGobbler(mPlayerProcess.getInputStream(), "OUTPUT", true);
			errorGobbler.start();
			outputGobbler.start();
			
			mPlayerProcess.waitFor();
			return MPLAYER_STOPPED;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
