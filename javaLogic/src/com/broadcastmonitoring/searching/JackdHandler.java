package com.broadcastmonitoring.searching;

import com.broadcastmonitoring.utils.StreamGobbler;

public class JackdHandler implements Runnable
{
	Process jackdProcess;
	@Override
	public void run()
	{
		try 
		{
			System.out.println("Jackd started");
			System.out.println("MESSAGE>Ensure that your user is allowed to run Jackd in realtime");
			String command="jackd -R -d alsa -p 2048 -r 44100";
			jackdProcess=Runtime.getRuntime().exec(command);
			
			StreamGobbler errorGobbler=new StreamGobbler(jackdProcess.getErrorStream(), "ERROR", false);
			StreamGobbler outputGobbler=new StreamGobbler(jackdProcess.getInputStream(), "OUTPUT", false);
			errorGobbler.start();
			outputGobbler.start();
			
			//jackdProcess.waitFor();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
}
