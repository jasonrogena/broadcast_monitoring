package com.broadcastmonitoring.indexing;

public class Peak 
{
	private final int frequency;
	private final int time;
	private final int realTime;
	private final String timestamp;
	private final long timestampMilliseconds;
	public Peak(int time, int freq, int realTime, String timestamp, long timestampMilliseconds) 
	{
		this.time=time;
		this.frequency=freq;
		this.realTime=realTime;
		this.timestamp=timestamp;
		this.timestampMilliseconds=timestampMilliseconds;
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
	
	public String getTimestamp()
	{
		return timestamp;
	}
	
	public long getTimestampMilliseconds()
	{
		return timestampMilliseconds;
	}
}
