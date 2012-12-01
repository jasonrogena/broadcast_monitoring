package com.broadcastmonitoring.indexing;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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

	public Streamer(float sampleRate, int frameSize, int hashmapSize, int redundantThreshold, int startFreq) throws LineUnavailableException
	{
		this.sampleRate=sampleRate;
		this.frameSize=frameSize;
		this.redundantThreshold=redundantThreshold;
		this.startFreq=startFreq;
		this.hashmapSize=hashmapSize;
		
		DataLine.Info info=new DataLine.Info(TargetDataLine.class, getFormat());
		line=(TargetDataLine)AudioSystem.getLine(info);
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
			Frame[] frameBuffer=new Frame[hashmapSize];
			int frameCount=1;
			while(true)
			{
				int count=line.read(buffer, 0, buffer.length);
				if(count>0 && frameCount<=frameBuffer.length)
				{
					frameBuffer[frameCount-1]=new Frame(buffer);
					if(frameCount==hashmapSize)
					{
						HashMap hashMap=new HashMap(frameBuffer);
						//TODO: visualize frames
						//TODO: generate hashes
						frameCount=1;
						frameBuffer=new Frame[hashmapSize];
					}
					else
					{
						frameCount++;
					}
				}
				else
				{
					if(count<=0)
					{
						System.err.println("****The targetDataLine is not streaming any data****");
					}
					if(frameCount>frameBuffer.length)
					{
						System.err.println("****The number of frames has just exceeded what the frameBuffer can handle****");
					}
				}
			}
		}
	}
}
