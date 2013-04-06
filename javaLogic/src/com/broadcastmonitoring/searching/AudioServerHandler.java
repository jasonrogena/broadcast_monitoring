package com.broadcastmonitoring.searching;

import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.jack.JackAudioServer;

public class AudioServerHandler implements Runnable
{

	@Override
	public void run()
	{
		System.out.println("Starting audio server");
		JackAudioServer jackAudioServer=null;
		try
		{
			AudioClientImpl client=new AudioClientImpl(jackAudioServer);
			AudioConfiguration audioConfiguration=new AudioConfiguration(44100, 1, 1, 2048, true);
			jackAudioServer=JackAudioServer.create("broadcastMonitoring", audioConfiguration, true, client);
			jackAudioServer.run();
			while(true)
			{
				System.out.print("*");
				/*if(exitFlag==Jackd.MPLAYER_STOPPED)
				{
					if(jackAudioServer.isActive())
					{
						System.out.println("Shutting down JackAudioServer");
						jackAudioServer.shutdown();
					}
					else
					{
						System.out.println("JackAudioServer was not running. No need for shutting down");
					}
				}*/
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
