package com.broadcastmonitoring.indexing;

public class Peak 
{
	private final int frequency;
	private final int time;
	private final int realTime;
	public Peak(int time, int freq, int realTime) 
	{
		this.time=time;
		this.frequency=freq;
		this.realTime=realTime;
	}
	
	public int getFrequency()
	{
		return frequency;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public int getRealTime()
	{
		return realTime;
	}
}
