package com.broadcastmonitoring.indexing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.collections.ObservableList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.broadcastmonitoring.searching.MPlayerHandler;
import com.broadcastmonitoring.utils.Log;
import com.broadcastmonitoring.utils.Time;

public class Streamer
{
	protected final TargetDataLine line;
	protected final int frameSize;
	protected final int redundantThreshold;
	protected final int startFreq;
	protected final float sampleRate;
	protected final int hashmapSize;
	protected final int lastTimeValue;
	protected final int targetZoneSize;
	protected final int anchor2peakMaxFreqDiff;
	protected final int sampledFrequencies;
	protected int searchableContentID=-1;
	protected Future<String> mplayerFuture;
	protected ExecutorService mplayerExecutorService;
	protected String searchableContentURL;
	protected Station station;
	protected final ObservableList<Log> logs;

	public Streamer(float sampleRate, int frameSize, int hashmapSize, int redundantThreshold, int startFreq, int targetZoneSize, int anchor2peakMaxFreqDiff, int sampledFrequencies, Station station, ObservableList<Log> logs) throws LineUnavailableException
	{
		this.sampleRate=sampleRate;
		this.frameSize=frameSize;
		this.redundantThreshold=redundantThreshold;
		this.startFreq=startFreq;
		this.hashmapSize=hashmapSize;
		this.targetZoneSize=targetZoneSize;
		this.anchor2peakMaxFreqDiff=anchor2peakMaxFreqDiff;
		this.sampledFrequencies=sampledFrequencies;
		this.station=station;
		this.logs=logs;
		
		DataLine.Info info=new DataLine.Info(TargetDataLine.class, getFormat());
		line=(TargetDataLine)AudioSystem.getLine(info);
		this.lastTimeValue=getLastTimeValue();
	}
	
	public Streamer(float sampleRate, int frameSize, int hashmapSize, int redundantThreshold, int startFreq, int targetZoneSize, int anchor2peakMaxFreqDiff, int sampledFrequencies, int searchableContentID, String searchableContentURL, ObservableList<Log> logs) throws LineUnavailableException
	{
		this.sampleRate=sampleRate;
		this.frameSize=frameSize;
		this.redundantThreshold=redundantThreshold;
		this.startFreq=startFreq;
		this.hashmapSize=hashmapSize;
		this.targetZoneSize=targetZoneSize;
		this.anchor2peakMaxFreqDiff=anchor2peakMaxFreqDiff;
		this.sampledFrequencies=sampledFrequencies;
		this.searchableContentID=searchableContentID;
		this.searchableContentURL=searchableContentURL;
		this.logs=logs;
		
		
		
		DataLine.Info info=new DataLine.Info(TargetDataLine.class, getFormat());
		line=(TargetDataLine)AudioSystem.getLine(info);
		this.lastTimeValue=getLastTimeValue();
	}
	
	private int getLastTimeValue()
	{
		//TODO: get last time value from database
		return 0;
	}
	
	private AudioFormat getFormat()
	{
		int sampleSize=8;
		int channels=1;
		boolean signed=true;
		boolean bigEndian=true;
		return new AudioFormat(this.sampleRate, sampleSize, channels, signed, bigEndian);
	}
	
	public boolean startAnalyzing()
	{
		try 
		{
			if(searchableContentID!=-1)//called by searching server
			{
				mplayerExecutorService=Executors.newFixedThreadPool(1);
				mplayerFuture=mplayerExecutorService.submit(new MPlayerHandler(searchableContentURL));
			}
			line.open();
			line.start();
			if(searchableContentID!=-1)
			{
				Thread thread=new Thread(new SynchronousStreamingThread());
				thread.run();
			}
			else
			{
				ExecutorService executorService=Executors.newFixedThreadPool(1);
				Future<String> future=executorService.submit(new AsynchonousStreamingThread());
			}
			return true;
		} 
		catch (LineUnavailableException e)
		{
			logs.add(new Log(Time.getTime("gmt"), "ERROR", "Streamer.java: LineUnavailableException thrown while trying to start analyzing input stream"));
			e.printStackTrace();
		}
		return false;
	}
	
	private class AsynchonousStreamingThread implements Callable<String> 
	{
		@Override
		public String call() throws Exception
		{
			return task();
		}
	}
	
	private class SynchronousStreamingThread implements Runnable
	{

		@Override
		public void run() 
		{
			task();
		}
		
	}
	
	private String task()
	{

		OutputStream out=new ByteArrayOutputStream();
		byte[] buffer=new byte[frameSize];
		
		//byte[] previous=null;
		
		Frame[] frameBuffer=new Frame[hashmapSize];
		int frameCount=1;
		int timeCounter=lastTimeValue;
		int channelNumber=-1;
		if(searchableContentID==-1)//called by indexing server
		{
			//Scanner in=new Scanner(System.in);
			//System.out.println("enter the channel number");
			//channelNumber=in.nextInt();
			channelNumber=station.getNumber();
			//in.close();
		}
		int smoothingWidth=101;
		while(true)
		{
			if(mplayerFuture!=null && mplayerFuture.isDone())
			{
				System.out.println("Streamer.java: Stopping the hash generation engine");
				logs.add(new Log(Time.getTime("gmt"), "INFO", "Streamer.java: Stopping the hash generation engine"));
				break;
			}
			else if(station!=null && station.isRunning()==false)
			{
				logs.add(new Log(Time.getTime("gmt"), "INFO", "Stopping stream on "+station.getStation()));
				break;
			}
			int count=line.read(buffer, 0, buffer.length);
			if(count>0 && frameCount<=frameBuffer.length)
			{
				try 
				{
					out.write(buffer,0,count);
					timeCounter++;
					/*for(int i=0;i<buffer.length;i++)
					{
						System.out.print(" "+buffer[i]+" ");
					}
					System.out.print("\n");*/
					
					Date date=new Date();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
					String timestamp=sdf.format(date);
					
					/*if(previous!=null)
					{
						if(Arrays.equals(previous, buffer)==false)
						{
							System.out.println("yes");
						}
					}*/
					//System.out.println(buffer[0]+" "+buffer[buffer.length/2]+" "+buffer[buffer.length-1]);
					//previous=buffer;
					
					frameBuffer[frameCount-1]=new Frame(buffer,redundantThreshold,startFreq,timeCounter, timestamp, date.getTime());
					//frameBuffer[0].printBuffer();
					if(frameCount==hashmapSize)
					{
						HashMap hashMap;
						//TODO: visualize frames
						if(searchableContentID==-1)//called by indexing server
						{
							hashMap=new HashMap(frameBuffer,targetZoneSize, anchor2peakMaxFreqDiff,channelNumber,0,smoothingWidth, sampledFrequencies);
						}
						else//called by indexing server
						{
							hashMap=new HashMap(frameBuffer,targetZoneSize, anchor2peakMaxFreqDiff,searchableContentID,1,smoothingWidth, sampledFrequencies);
						}
						hashMap.generateHashes();
						frameCount=1;
						frameBuffer=new Frame[hashmapSize];//make you use the framebuffer you passed in the hashmap before you reinitialize this framebuffer
					}
					else
					{
						frameCount++;
					}
				} 
				catch (IOException e)
				{
					logs.add(new Log(Time.getTime("gmt"), "ERROR", "Streamer.java: Unable to write to output stream"));
					e.printStackTrace();
				}
				finally
				{
					try 
					{
						out.close();
					} 
					catch (Exception e) 
					{
						logs.add(new Log(Time.getTime("gmt"), "ERROR", "Streamer.java: Unable to close output stream"));
						e.printStackTrace();
					}
				}
				
			}
			else
			{
				if(count<=0)
				{
					logs.add(new Log(Time.getTime("gmt"), "ERROR", "Streamer.java: The targetDataLine is not streaming any data"));
				}
				if(frameCount>frameBuffer.length)
				{
					logs.add(new Log(Time.getTime("gmt"), "ERROR", "Streamer.java: The number of frames has just exceeded what the frameBuffer can handle"));
				}
			}
		}
		return null;
	}
}
