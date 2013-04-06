package com.broadcastmonitoring.searching;

import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.jack.JackAudioServer;

public class AudioServerThread extends Thread
{
	public AudioServerThread(String exitFlag) 
	{
		super(new Runnable() 
		{
			
			@Override
			public void run()
			{
				JackAudioServer jackAudioServer=null;
				try
				{
					AudioClientImpl client=new AudioClientImpl(jackAudioServer);
					AudioConfiguration audioConfiguration=new AudioConfiguration(44100, 1, 1, 2048, true);
					jackAudioServer=JackAudioServer.create("broadcastMonitoring", audioConfiguration, true, client);
					jackAudioServer.run();
					
					//kill all jacked processes
					System.out.println("Waiting for one second..");
					Thread.sleep(1000);
					System.out.println("Killing all instances of Jackd");
					Jackd.killPreviousJackd();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		setPriority(Thread.MAX_PRIORITY);
	}
}
