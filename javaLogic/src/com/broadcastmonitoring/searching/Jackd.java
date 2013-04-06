package com.broadcastmonitoring.searching;

import com.broadcastmonitoring.utils.StreamGobbler;
//import com.petersalomonsen.jjack.javasound.JJackMixer;
public class Jackd 
{
	public static final String MPLAYER_STOPPED="stopped";
	public static final String MPLAYER_RUNNING="running";
	
	private static String exitFlag=MPLAYER_RUNNING;
	public static void main(String[] args) 
	{
		try
		{
			//1. kill jackd
			killPreviousJackd();
			
			//2. start jackd daemon
			Thread jackdThread=new Thread(new JackdHandler());
			jackdThread.run();
			System.out.println("Jackd started");
			
			//3. init JJackClient and run mplayer
			System.out.println("Waiting for 3 seconds for Jackd to start");
			Thread.sleep(3000);
			AudioServerThread serverThread=new AudioServerThread(exitFlag);
			serverThread.run();
			
			//5. cleanup
			serverThread.interrupt();
			jackdThread.interrupt();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void killPreviousJackd()
	{
		try 
		{
			System.out.println("Killing all previous instances of Jackd");
			String command="killall jackd";
			Process jackdProcess=Runtime.getRuntime().exec(command);
			
			StreamGobbler errorGobbler=new StreamGobbler(jackdProcess.getErrorStream(), "ERROR", false);
			StreamGobbler outputGobbler=new StreamGobbler(jackdProcess.getInputStream(), "OUTPUT", false);
			errorGobbler.start();
			outputGobbler.start();
			
			jackdProcess.waitFor();
			System.out.println("Sleeping for two seconds....");
			Thread.sleep(2000);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
