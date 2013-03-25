package com.broadcastmonitoring.searching;

import com.broadcastmonitoring.utils.StreamGobbler;
//import com.petersalomonsen.jjack.javasound.JJackMixer;

import java.applet.AudioClip;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Scanner;

import org.jaudiolibs.audioservers.AudioClient;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.jack.JackAudioServer;

/*import de.gulden.framework.jjack.JJackAudioEvent;
import de.gulden.framework.jjack.JJackAudioProcessor;
import de.gulden.framework.jjack.JJackClient;
import de.gulden.framework.jjack.JJackSystem;*/

public class Jackd 
{
	private static int exitFlag=0;
	public static void main(String[] args) 
	{
		try
		{
			//1. kill jackd
			killJackd();
			
			//2. start jackd daemon
			Thread jackdThread=new Thread(new JackdHandler());
			jackdThread.run();
			System.out.println("Jackd started");
			
			/*//3. init JJackClient
			//MyJJackClient client=new MyJJackClient();
			MyJJackAudioProcessor audioProcessor=new MyJJackAudioProcessor();
			JJackSystem.setProcessor(audioProcessor);*/
			System.out.println("Waiting for 3 seconds for Jackd to start");
			Thread.sleep(3000);
			AudioClientImpl client=new AudioClientImpl();
			AudioConfiguration audioConfiguration=new AudioConfiguration(44100, 1, 1, 2048, true);
			JackAudioServer audioServer=JackAudioServer.create("broadcastMonitoring", audioConfiguration, true, client);//TODO: server should run in a high priority thread
			audioServer.run();
			
			//4. play using mplayer
			mplayerHandler();
			
			//5. kill jackd daemon
			while(true)
			{
				if(exitFlag==1)
				{
					System.out.println("Shutting down JackAudioServer");
					if(audioServer.isActive())
					{
						audioServer.shutdown();
					}
					else
					{
						System.out.println("AudioServer was not active. No need to shutdown");
					}
					System.out.println("stopping Jackd");
					jackdThread.interrupt();
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static void killJackd()
	{
		try 
		{
			System.out.println("Killing all previous instances of Jackd");
			String command="killall jackd";
			Process jackdProcess=Runtime.getRuntime().exec(command);
			
			StreamGobbler errorGobbler=new StreamGobbler(jackdProcess.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler=new StreamGobbler(jackdProcess.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();
			
			jackdProcess.waitFor();
			Thread.sleep(2000);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void mplayerHandler()
	{
		try
		{
			Scanner in=new Scanner(System.in);
			System.out.println("enter the absolute url of the audio file");
			String url=in.nextLine();
			String command="mplayer -slave -ao jack:name=mplayer -vo null -vc null "+url;
			Process mPlayerProcess=Runtime.getRuntime().exec(command);
			
			StreamGobbler errorGobbler=new StreamGobbler(mPlayerProcess.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler=new StreamGobbler(mPlayerProcess.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();
			
			mPlayerProcess.waitFor();
			
			System.out.println("finished playing audio");
			exitFlag=1;
			
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static class AudioClientImpl implements AudioClient
	{

		@Override
		public void configure(AudioConfiguration audioConfiguration) throws Exception 
		{
			System.out.println("\nAudioServer Configuration");
			System.out.println("--------------------------");
			System.out.println("Input Channel Count = "+audioConfiguration.getInputChannelCount());
			System.out.println("Max Buffer Size = "+audioConfiguration.getMaxBufferSize());
			System.out.println("Is Fixed Buffer Size = "+audioConfiguration.isFixedBufferSize());
			System.out.println("Output Channel Count = "+audioConfiguration.getOutputChannelCount());
			System.out.println("Sample Rate = "+audioConfiguration.getSampleRate()+"\n");
		}

		/*The method that actually processes the audio. The client is provided with the time for the current buffer,
		 *  measured in nanoseconds and relative to System.nanotime().
		 *  The client should always use the time provided by the server.
		 *  The server will provide the client with unmodifiable lists of input and output audio buffers as FloatBuffers.
		 *  In the case of there being no input channels, a zero length list rather than null will be passed in.
		 *  Input buffers should be treated as read-only.
		 *  The server will provide the number of frames of audio in each buffer.
		 *  The count will always be the same across each input and output buffer.
		 *  If the context passed to configure returns true for isFixedBufferSize() then the number of
		 *  frames will always be equal to getMaxBufferSize().
		 *  Otherwise, the number of frames may be between 1 and getMaxBufferSize().
		 *  The client should return a boolean value - true if the audio has been processed OK,
		 *  false to disconnect the client from the server.*/
		@Override
		public boolean process(long arg0, List<FloatBuffer> inputs, List<FloatBuffer> outputs, int numberOfFrames)//time for the current buffer, inputs, outputs, 
		{
			// length of input list is 0 if no channes gotten
			return true;//returning false will disconnect from server
		}

		@Override
		public void shutdown()
		{
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/*private static class MyJJackAudioProcessor implements JJackAudioProcessor
	{

		@Override
		public void process(JJackAudioEvent event) 
		{
			System.out.println("event has "+event.countChannels()+" channels");
			FloatBuffer in=event.getInput(0);//TODO: which channel should be analyzed?
			for(int i=0; i<in.capacity(); i++)
			{
				System.out.print(" "+in.get(i));
			}
		}
		
	}
	
	private static class MyJJackClient extends JJackClient
	{

		@Override
		public void process(JJackAudioEvent event) 
		{
			System.out.println("event has "+event.countChannels()+" channels");
			FloatBuffer in=event.getInput(0);//TODO: which channel should be analyzed?
			for(int i=0; i<in.capacity(); i++)
			{
				System.out.print(" "+in.get(i));
			}
		}
		
	}*/
	
	private static class JackdHandler implements Runnable
	{
		Process jackdProcess;
		@Override
		public void run()
		{
			try 
			{
				System.out.println("MESSAGE: ensure that your user is allowed to run Jackd in realtime");
				String command="jackd -R -d alsa -p 2048 -r 44100";
				jackdProcess=Runtime.getRuntime().exec(command);
				
				StreamGobbler errorGobbler=new StreamGobbler(jackdProcess.getErrorStream(), "ERROR");
				StreamGobbler outputGobbler=new StreamGobbler(jackdProcess.getInputStream(), "OUTPUT");
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

}
