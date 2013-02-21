package com.broadcastmonitoring.indexing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

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

	public Streamer(float sampleRate, int frameSize, int hashmapSize, int redundantThreshold, int startFreq, int targetZoneSize, int anchor2peakMaxFreqDiff) throws LineUnavailableException
	{
		this.sampleRate=sampleRate;
		this.frameSize=frameSize;
		this.redundantThreshold=redundantThreshold;
		this.startFreq=startFreq;
		this.hashmapSize=hashmapSize;
		this.targetZoneSize=targetZoneSize;
		this.anchor2peakMaxFreqDiff=anchor2peakMaxFreqDiff;
		
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
			line.open();
			line.start();
			Thread thread=new Thread(new StreamingThread());
			thread.run();
			return true;
		} 
		catch (LineUnavailableException e) 
		{
			System.err.println("****LineUnavailableException thrown****");
			e.printStackTrace();
		}
		return false;
	}
	
	private class StreamingThread implements Runnable 
	{
		@Override
		public void run() 
		{
			OutputStream out=new ByteArrayOutputStream();
			byte[] buffer=new byte[frameSize];
			
			//byte[] previous=null;
			
			Frame[] frameBuffer=new Frame[hashmapSize];
			int frameCount=1;
			int timeCounter=lastTimeValue;
			Scanner in=new Scanner(System.in);
			System.out.println("enter the channel number");
			int channelNumber=in.nextInt();
			int smoothingWidth=5;
			while(true)
			{
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
						
						frameBuffer[frameCount-1]=new Frame(buffer,redundantThreshold,startFreq,timeCounter, timestamp);
						//frameBuffer[0].printBuffer();
						if(frameCount==hashmapSize)
						{
							HashMap hashMap=new HashMap(frameBuffer,targetZoneSize, anchor2peakMaxFreqDiff,channelNumber,0,smoothingWidth);
							//TODO: visualize frames
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
						System.err.println("****unable to write to output stream****");
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
							System.err.println("****unable to close output stream****");
							e.printStackTrace();
						}
					}
					
				}
				else
				{
					if(count<=0)
					{
						System.err.println("****The targetDataLine is not streaming any data****\n# Streamer.java #");
					}
					if(frameCount>frameBuffer.length)
					{
						System.err.println("****The number of frames has just exceeded what the frameBuffer can handle****\n# Streamer.java #");
					}
				}
			}
		}
	}
}
