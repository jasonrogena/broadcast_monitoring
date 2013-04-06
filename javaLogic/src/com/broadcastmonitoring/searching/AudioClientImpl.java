package com.broadcastmonitoring.searching;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jaudiolibs.audioservers.AudioClient;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.jack.JackAudioServer;

public class AudioClientImpl implements AudioClient
{
	private Scanner in=new Scanner(System.in);
	private Future<String> future;
	private ExecutorService executorService;
	private JackAudioServer audioServer;
	public AudioClientImpl(JackAudioServer jackAudioServer)
	{
		this.audioServer=audioServer;
	}
	
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
		
		System.out.println("enter the absolute url of the audio file");
		final String url=in.nextLine();
		
		executorService=Executors.newFixedThreadPool(1);
		future=executorService.submit(new MPlayerHandler(url));//should run asynchronously
		//new Thread(new MPlayerHandler(url)).run();//thread should run asynchronously
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
		//System.out.println("number of channels = "+inputs.size());
		// length of input list is 0 if no channes gotten
		//System.out.println(numberOfFrames);
		FloatBuffer input=inputs.get(0);
		for(int i=0; i<input.capacity(); i++)
		{
			System.out.print(" "+input.get(i));
		}
		if(future.isDone())
		{
			audioServer.shutdown();
		}
		
		return true;
	}

	@Override
	public void shutdown()
	{
		System.out.println("Shutting down audio client");
		in.close();
	}
	
}
